package pl.rosehc.sectors;

import java.io.File;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import pl.rosehc.adapter.configuration.ConfigurationData;
import pl.rosehc.adapter.helper.ConfigurationHelper;
import pl.rosehc.adapter.helper.EventCompletionStage;
import pl.rosehc.adapter.plugin.BukkitPlugin;
import pl.rosehc.adapter.redis.callback.Callback;
import pl.rosehc.adapter.redis.callback.CallbackPacket;
import pl.rosehc.controller.ControllerPanicHelper;
import pl.rosehc.controller.packet.SectorPacketHandler;
import pl.rosehc.controller.packet.configuration.ConfigurationSynchronizePacket;
import pl.rosehc.controller.packet.controller.ControllerUpdateTimePacket;
import pl.rosehc.controller.packet.proxy.ProxyUpdateStatisticsPacket;
import pl.rosehc.controller.packet.sector.SectorInitializationRequestPacket;
import pl.rosehc.controller.packet.sector.SectorInitializationResponsePacket;
import pl.rosehc.controller.packet.sector.SectorUpdateStatisticsPacket;
import pl.rosehc.controller.packet.sector.user.SectorUserCreatePacket;
import pl.rosehc.controller.packet.sector.user.SectorUserDeletePacket;
import pl.rosehc.controller.packet.sector.user.SectorUserUpdateSectorPacket;
import pl.rosehc.sectors.data.SectorPlayerDataSynchronizePacketHandler;
import pl.rosehc.sectors.data.SectorPlayerDataSynchronizeRequestPacket;
import pl.rosehc.sectors.data.SectorPlayerDataSynchronizeResponsePacket;
import pl.rosehc.sectors.listener.PlayerJoinListener;
import pl.rosehc.sectors.listener.PlayerMoveListener;
import pl.rosehc.sectors.listener.PlayerPreLoginListener;
import pl.rosehc.sectors.listener.PlayerTeleportListener;
import pl.rosehc.sectors.listener.SectorProtectionListener;
import pl.rosehc.sectors.proxy.ProxyFactory;
import pl.rosehc.sectors.sector.Sector;
import pl.rosehc.sectors.sector.SectorFactory;
import pl.rosehc.sectors.sector.SectorInitializationHook;
import pl.rosehc.sectors.sector.SectorType;
import pl.rosehc.sectors.sector.task.SectorBorderBarUpdateTask;
import pl.rosehc.sectors.sector.task.SectorBorderUpdateTask;
import pl.rosehc.sectors.sector.task.SectorGhostUserCleanupTask;
import pl.rosehc.sectors.sector.task.SectorPanicCheckTask;
import pl.rosehc.sectors.sector.task.SectorUpdateInfoTask;
import pl.rosehc.sectors.sector.user.SectorUserFactory;

public final class SectorsPlugin extends BukkitPlugin {

  private static SectorsPlugin instance;
  private final AtomicBoolean loaded = new AtomicBoolean();
  private CurrentSectorConfiguration currentSectorConfiguration;
  private SectorsConfiguration sectorsConfiguration;
  private SectorFactory sectorFactory;
  private ProxyFactory proxyFactory;
  private SectorUserFactory sectorUserFactory;
  private Set<SectorInitializationHook> hookSet;

  public static SectorsPlugin getInstance() {
    return instance;
  }

  @Override
  public void onEnable() {
    this.currentSectorConfiguration = ConfigurationHelper.load(
        new File(this.getDataFolder(), "config.json"), CurrentSectorConfiguration.class);
    this.getRedisAdapter().subscribe(new SectorPacketHandler(this), Arrays.asList(
        "rhc_sectors",
        "rhc_global"
    ), Arrays.asList(
        ProxyUpdateStatisticsPacket.class,
        SectorInitializationResponsePacket.class,
        SectorUserCreatePacket.class,
        SectorUserDeletePacket.class,
        SectorUserUpdateSectorPacket.class,
        SectorUpdateStatisticsPacket.class,
        ConfigurationSynchronizePacket.class,
        ControllerUpdateTimePacket.class
    ));
    this.registerListeners(new PlayerPreLoginListener(this));
    this.getRedisAdapter().sendPacket(
        new SectorInitializationRequestPacket(currentSectorConfiguration.currentSectorName),
        new Callback() {

          @Override
          public void done(final CallbackPacket packet) {
            try {
              final SectorInitializationResponsePacket responsePacket = (SectorInitializationResponsePacket) packet;
              if (responsePacket.getSectorName()
                  .equals(currentSectorConfiguration.currentSectorName)) {
                sectorsConfiguration = ConfigurationHelper.deserializeConfiguration(
                    responsePacket.getSectorsConfigurationData(), SectorsConfiguration.class);
                sectorFactory = new SectorFactory(sectorsConfiguration,
                    currentSectorConfiguration.currentSectorName);
                proxyFactory = new ProxyFactory(sectorsConfiguration);
                sectorUserFactory = new SectorUserFactory(responsePacket.getSectorUsers());
                final Sector currentSector = sectorFactory.getCurrentSector();
                getRedisAdapter().subscribe(
                    new SectorPlayerDataSynchronizePacketHandler(SectorsPlugin.this),
                    "rhc_playerdata_" + currentSector.getName(),
                    Arrays.asList(SectorPlayerDataSynchronizeRequestPacket.class,
                        SectorPlayerDataSynchronizeResponsePacket.class));
                if (currentSector.getType().equals(SectorType.END)) {
                  ((CraftWorld) currentSector.getWorld()).getHandle().paperSpigotConfig.disableEndCredits = true;
                }

                registerListeners(new PlayerJoinListener(SectorsPlugin.this),
                    new PlayerMoveListener(SectorsPlugin.this),
                    new PlayerTeleportListener(SectorsPlugin.this),
                    new SectorProtectionListener(SectorsPlugin.this));
                new SectorBorderBarUpdateTask(SectorsPlugin.this);
                new SectorGhostUserCleanupTask(SectorsPlugin.this);
                if (!currentSector.getType().equals(SectorType.GROUP_TELEPORTS)) {
                  new SectorBorderUpdateTask(SectorsPlugin.this);
                }

                final EventCompletionStage completionStage = new EventCompletionStage(() -> {
                  loaded.set(true);
                  ControllerPanicHelper.markEnabled();
                  new SectorUpdateInfoTask(SectorsPlugin.this);
                  new SectorPanicCheckTask(SectorsPlugin.this);
                });
                completionStage.postFire();
                hookSet.forEach(
                    hook -> hook.onInitialize(completionStage, currentSector,
                        true));
              }
            } catch (final Exception ex) {
              getLogger().log(Level.SEVERE, "Plugin nie mógł zostać poprawnie załadowany!", ex);
              hookSet.forEach(hook -> hook.onInitialize(null, null, false));
              getServer().getPluginManager().disablePlugin(SectorsPlugin.this);
            }
          }

          @Override
          public void error(final String message) {
            getLogger().log(Level.SEVERE, message);
            hookSet.forEach(hook -> hook.onInitialize(null, null, false));
            getServer().getPluginManager().disablePlugin(SectorsPlugin.this);
          }
        }, "rhc_master_controller");
  }

  @Override
  public void onLoad() {
    hookSet = ConcurrentHashMap.newKeySet();
    instance = this;
  }

  public CurrentSectorConfiguration getCurrentSectorConfiguration() {
    return this.currentSectorConfiguration;
  }

  public SectorsConfiguration getSectorsConfiguration() {
    return this.sectorsConfiguration;
  }

  public void setSectorsConfiguration(final SectorsConfiguration sectorsConfiguration) {
    this.sectorsConfiguration = sectorsConfiguration;
  }

  public SectorFactory getSectorFactory() {
    return this.sectorFactory;
  }

  public ProxyFactory getProxyFactory() {
    return this.proxyFactory;
  }

  public SectorUserFactory getSectorUserFactory() {
    return this.sectorUserFactory;
  }

  public boolean isLoaded() {
    return this.loaded.get();
  }

  public void registerHook(final SectorInitializationHook hook) {
    this.hookSet.add(hook);
  }

  public static class CurrentSectorConfiguration extends ConfigurationData {

    public String currentSectorName = "spawn_1";
    public int currentSectorBorderSize = -1;
  }
}
