package pl.rosehc.sectors;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.stream.Collectors;
import pl.rosehc.adapter.configuration.ConfigurationData;
import pl.rosehc.adapter.helper.ConfigurationHelper;
import pl.rosehc.adapter.helper.EventCompletionStage;
import pl.rosehc.adapter.plugin.BungeePlugin;
import pl.rosehc.adapter.redis.callback.Callback;
import pl.rosehc.adapter.redis.callback.CallbackPacket;
import pl.rosehc.controller.ControllerPanicHelper;
import pl.rosehc.controller.packet.SectorPacketHandler;
import pl.rosehc.controller.packet.configuration.ConfigurationSynchronizePacket;
import pl.rosehc.controller.packet.controller.ControllerUpdateTimePacket;
import pl.rosehc.controller.packet.proxy.ProxyInitializationRequestPacket;
import pl.rosehc.controller.packet.proxy.ProxyInitializationResponsePacket;
import pl.rosehc.controller.packet.proxy.ProxyUpdateStatisticsPacket;
import pl.rosehc.controller.packet.sector.SectorUpdateStatisticsPacket;
import pl.rosehc.controller.packet.sector.user.SectorUserCreatePacket;
import pl.rosehc.controller.packet.sector.user.SectorUserDeletePacket;
import pl.rosehc.controller.packet.sector.user.SectorUserUpdateSectorPacket;
import pl.rosehc.sectors.listener.PlayerDisconnectListener;
import pl.rosehc.sectors.listener.PlayerPreLoginListener;
import pl.rosehc.sectors.listener.PlayerServerConnectedListener;
import pl.rosehc.sectors.proxy.ProxyFactory;
import pl.rosehc.sectors.proxy.ProxyInitializationHook;
import pl.rosehc.sectors.proxy.ProxyUpdateInfoTask;
import pl.rosehc.sectors.sector.SectorFactory;
import pl.rosehc.sectors.sector.user.SectorGhostUserCleanupTask;
import pl.rosehc.sectors.sector.user.SectorUser;
import pl.rosehc.sectors.sector.user.SectorUserConnectPacket;
import pl.rosehc.sectors.sector.user.SectorUserFactory;

public final class SectorsPlugin extends BungeePlugin {

  private static SectorsPlugin instance;
  private final AtomicBoolean loaded = new AtomicBoolean();
  private SectorsConfiguration sectorsConfiguration;
  private SectorFactory sectorFactory;
  private ProxyFactory proxyFactory;
  private SectorUserFactory sectorUserFactory;
  private Set<ProxyInitializationHook> hookSet;

  public static SectorsPlugin getInstance() {
    return instance;
  }

  @Override
  public void onEnable() {
    final CurrentProxyConfiguration currentProxyConfiguration = ConfigurationHelper.load(
        new File(this.getDataFolder(), "config.json"), CurrentProxyConfiguration.class);
    this.getRedisAdapter().subscribe(new SectorPacketHandler(this), Arrays.asList(
        "rhc_sectors",
        "rhc_proxies",
        "rhc_global",
        "rhc_proxy_" + currentProxyConfiguration.currentProxyIdentifier
    ), Arrays.asList(
        ProxyInitializationResponsePacket.class,
        ProxyUpdateStatisticsPacket.class,
        SectorUserCreatePacket.class,
        SectorUserDeletePacket.class,
        SectorUserUpdateSectorPacket.class,
        SectorUserConnectPacket.class,
        SectorUpdateStatisticsPacket.class,
        ConfigurationSynchronizePacket.class,
        ControllerUpdateTimePacket.class
    ));
    this.registerListeners(new PlayerPreLoginListener(this));
    this.getRedisAdapter().sendPacket(
        new ProxyInitializationRequestPacket(currentProxyConfiguration.currentProxyIdentifier),
        new Callback() {

          @Override
          public void done(final CallbackPacket packet) {
            try {
              final ProxyInitializationResponsePacket responsePacket = (ProxyInitializationResponsePacket) packet;
              if (responsePacket.getProxyIdentifier()
                  == currentProxyConfiguration.currentProxyIdentifier) {
                sectorsConfiguration = ConfigurationHelper.deserializeConfiguration(
                    responsePacket.getSectorsConfigurationData(), SectorsConfiguration.class);
                sectorFactory = new SectorFactory(sectorsConfiguration);
                proxyFactory = new ProxyFactory(sectorsConfiguration,
                    currentProxyConfiguration.currentProxyIdentifier);
                sectorUserFactory = new SectorUserFactory(responsePacket.getSectorUsers());
                registerListeners(new PlayerServerConnectedListener(SectorsPlugin.this),
                    new PlayerPreLoginListener(SectorsPlugin.this),
                    new PlayerDisconnectListener(SectorsPlugin.this));
                new SectorGhostUserCleanupTask(SectorsPlugin.this);
                final EventCompletionStage completionStage = new EventCompletionStage(() -> {
                  loaded.set(true);
                  ControllerPanicHelper.markEnabled();
                  new ProxyUpdateInfoTask(SectorsPlugin.this);
                });
                hookSet.forEach(
                    hook -> hook.onInitialize(completionStage, proxyFactory.getCurrentProxy(),
                        true));
                completionStage.postFire();
              }
            } catch (final Exception ex) {
              getLogger().log(Level.SEVERE, "Plugin nie mógł zostać poprawnie załadowany!", ex);
              hookSet.forEach(hook -> hook.onInitialize(null, null, false));
            }
          }

          @Override
          public void error(final String message) {
            getLogger().log(Level.SEVERE, message);
            hookSet.forEach(hook -> hook.onInitialize(null, null, false));
          }
        }, "rhc_master_controller");
  }

  @Override
  public void onLoad() {
    instance = this;
    hookSet = ConcurrentHashMap.newKeySet();
  }

  @Override
  public void onDisable() {
    if (Objects.nonNull(this.sectorUserFactory) && Objects.nonNull(this.proxyFactory)) {
      final List<SectorUser> currentProxyUsers = this.sectorUserFactory.getUserMap().values()
          .stream().filter(
              user -> user.getProxy().getIdentifier() == this.proxyFactory.getCurrentProxy()
                  .getIdentifier()).collect(Collectors.toList());
      for (final SectorUser currentProxyUser : currentProxyUsers) {
        this.getRedisAdapter()
            .sendPacket(new SectorUserDeletePacket(currentProxyUser.getUniqueId()), "rhc_global");
      }

      this.sectorUserFactory.getUserMap().values().removeAll(currentProxyUsers);
    }
  }

  public SectorsConfiguration getSectorsConfiguration() {
    return this.sectorsConfiguration;
  }

  public void setSectorsConfiguration(SectorsConfiguration sectorsConfiguration) {
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

  public void registerHook(final ProxyInitializationHook hook) {
    this.hookSet.add(hook);
  }

  public static class CurrentProxyConfiguration extends ConfigurationData {

    public int currentProxyIdentifier = 1;
  }
}
