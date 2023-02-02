package pl.rosehc.platform.listener.player;

import java.util.Objects;
import java.util.stream.Collectors;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import pl.rosehc.adapter.helper.ItemHelper;
import pl.rosehc.adapter.redis.callback.Callback;
import pl.rosehc.adapter.redis.callback.CallbackPacket;
import pl.rosehc.controller.ControllerPanicHelper;
import pl.rosehc.controller.packet.platform.user.PlatformUserCombatTimeUpdatePacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserSynchronizeSomeDropSettingsDataPacket;
import pl.rosehc.controller.wrapper.platform.PlatformUserCooldownType;
import pl.rosehc.platform.PlatformConfiguration.CustomItemsWrapper.CustomItemWrapper;
import pl.rosehc.platform.PlatformPlugin;
import pl.rosehc.platform.end.EndHelper;
import pl.rosehc.platform.user.subdata.PlatformUserDropSettings;
import pl.rosehc.sectors.SectorsPlugin;
import pl.rosehc.sectors.data.SectorPlayerData;
import pl.rosehc.sectors.data.SectorPlayerDataSynchronizeRequestPacket;
import pl.rosehc.sectors.helper.SectorHelper;
import pl.rosehc.sectors.sector.Sector;
import pl.rosehc.sectors.sector.SectorType;
import pl.rosehc.sectors.sector.user.SectorUser;

public final class PlayerQuitListener implements Listener {

  private final PlatformPlugin plugin;

  public PlayerQuitListener(final PlatformPlugin plugin) {
    this.plugin = plugin;
  }

  @EventHandler
  public void onQuit(final PlayerQuitEvent event) {
    final Player player = event.getPlayer();
    event.setQuitMessage(null);
    this.plugin.getHologramFactory().removeHolograms(player);
    this.plugin.getPlatformUserFactory().findUserByUniqueId(player.getUniqueId())
        .ifPresent(user -> {
          final PlatformUserDropSettings dropSettings = user.getDropSettings();
          this.plugin.getRedisAdapter().sendPacket(
              new PlatformUserSynchronizeSomeDropSettingsDataPacket(user.getUniqueId(),
                  dropSettings.getTurboDropMultiplier(), dropSettings.isCobbleStone(),
                  dropSettings.getTurboDropTime(), dropSettings.getCurrentXP(),
                  dropSettings.getNeededXP(), dropSettings.getLevel()), "rhc_master_controller",
              "rhc_platform");
          if (user.isGroupTeleportsChange()) {
            player.teleport(PlatformPlugin.getInstance()
                .getPlatformConfiguration().spawnLocationWrapper.unwrap());
          }

          user.setEndPortalChange(false);
          user.setGroupTeleportsChange(false);
          user.setPreparedForNameTagCreation(false);
          if (!SectorsPlugin.getInstance().getSectorUserFactory()
              .findUserByUniqueId(player.getUniqueId()).filter(SectorUser::isRedirecting)
              .isPresent()) {
            if (user.isInCombat()) {
              user.setCombatTime(0L);
              this.plugin.getRedisAdapter().sendPacket(
                  new PlatformUserCombatTimeUpdatePacket(user.getUniqueId(), user.getCombatTime()),
                  "rhc_master_controller", "rhc_platform");
              if (!ControllerPanicHelper.isInPanic() && !player.hasPermission(
                  "platform-combat-bypass")
                  && !this.plugin.getPlatformConfiguration().serverFreezeState) {
                player.setHealth(0D);
                if (SectorsPlugin.getInstance().getSectorFactory().getCurrentSector().getType()
                    .equals(SectorType.END)) {
                  user.getCooldownCache().putUserCooldown(PlatformUserCooldownType.END_BAN);
                }

                SectorHelper.getRandomSector(SectorType.SPAWN).ifPresent(sector -> {
                  ItemHelper.addItems(player,
                      this.plugin.getPlatformConfiguration().customItemsWrapper.respawnItemList.stream()
                          .map(CustomItemWrapper::asItemStack).collect(Collectors.toList()));
                  final SectorPlayerData data = SectorPlayerData.of(player);
                  data.setHealth(player.getMaxHealth());
                  data.setLocation(((sector.getStatistics().getPlayers() * 100D)
                      / this.plugin.getPlatformConfiguration().slotWrapper.spigotSlots) < 60D
                      ? this.plugin.getPlatformConfiguration().spawnLocationWrapper.unwrap()
                      : sector.random());
                  data.setPotionEffects(new PotionEffect[0]);
                  data.setExp(0F);
                  data.setFireTicks(0);
                  data.setFoodLevel(20);
                  data.setHeldSlot(0);
                  data.setLevel(0);
                  this.plugin.getRedisAdapter().sendPacket(
                      new SectorPlayerDataSynchronizeRequestPacket(data,
                          SectorsPlugin.getInstance().getSectorFactory().getCurrentSector()
                              .getName()),
                      new Callback() {

                        @Override
                        public void done(final CallbackPacket packet) {
                          plugin.getRedisAdapter()
                              .set("rhc_player_sectors", player.getUniqueId().toString(),
                                  sector.getName());
                        }

                        @Override
                        public void error(final String ignored) {
                        }
                      }, "rhc_playerdata_" + sector.getName());
                });
              }
            } else if (!SectorsPlugin.getInstance().getSectorFactory().getCurrentSector().getType()
                .equals(SectorType.SPAWN) && !SectorsPlugin.getInstance().getSectorFactory()
                .getCurrentSector().getType().equals(SectorType.GAME)) {
              if (SectorsPlugin.getInstance().getSectorFactory().getCurrentSector().getType()
                  .equals(SectorType.END)) {
                EndHelper.disableWaterSkill(player);
                this.plugin.getEndPortalPointEditingSessionFactory()
                    .findSession(event.getPlayer().getUniqueId())
                    .ifPresent(this.plugin.getEndPortalPointEditingSessionFactory()::removeSession);
              }

              final Sector sector = SectorHelper.getRandomSector(SectorType.SPAWN)
                  .orElse(SectorHelper.getRandomSector(SectorType.GAME).orElse(null));
              if (Objects.nonNull(sector)) {
                final SectorPlayerData data = SectorPlayerData.of(player);
                data.setLocation(sector.getType().equals(SectorType.SPAWN) &&
                    ((sector.getStatistics().getPlayers() * 100D)
                        / this.plugin.getPlatformConfiguration().slotWrapper.spigotSlots) < 60D
                    ? this.plugin.getPlatformConfiguration().spawnLocationWrapper.unwrap()
                    : sector.random());
                this.plugin.getRedisAdapter().sendPacket(
                    new SectorPlayerDataSynchronizeRequestPacket(data,
                        SectorsPlugin.getInstance().getSectorFactory().getCurrentSector()
                            .getName()), new Callback() {

                      @Override
                      public void done(final CallbackPacket packet) {
                        plugin.getRedisAdapter()
                            .set("rhc_player_sectors", player.getUniqueId().toString(),
                                sector.getName());
                      }

                      @Override
                      public void error(final String ignored) {
                      }
                    }, "rhc_playerdata_" + sector.getName());
              }
            }
          }
        });
    this.plugin.getMagicCaseFactory().removeMagicCase(player.getUniqueId());
  }
}
