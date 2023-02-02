package pl.rosehc.platform.listener.sector;

import java.util.Optional;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import pl.rosehc.adapter.AdapterPlugin;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.adapter.helper.TimeHelper;
import pl.rosehc.controller.packet.platform.user.PlatformUserCombatTimeUpdatePacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserSynchronizeSomeDropSettingsDataPacket;
import pl.rosehc.controller.wrapper.platform.PlatformUserCooldownType;
import pl.rosehc.platform.PlatformPlugin;
import pl.rosehc.platform.user.subdata.PlatformUserCooldownCache;
import pl.rosehc.platform.user.subdata.PlatformUserDropSettings;
import pl.rosehc.sectors.helper.SectorHelper;
import pl.rosehc.sectors.sector.Sector;
import pl.rosehc.sectors.sector.SectorConnectingEvent;
import pl.rosehc.sectors.sector.SectorType;

public final class SectorConnectingListener implements Listener {

  private final PlatformPlugin plugin;

  public SectorConnectingListener(final PlatformPlugin plugin) {
    this.plugin = plugin;
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onConnecting(final SectorConnectingEvent event) {
    final Player player = event.getPlayer();
    boolean isSectorFull = !player.hasPermission("platform-slots-bypass")
        && event.getSector().getStatistics().getPlayers()
        >= this.plugin.getPlatformConfiguration().slotWrapper.spigotSlots;
    if (event.getSector().getType().equals(SectorType.SPAWN) && isSectorFull) {
      final Optional<Sector> newSectorOptional = SectorHelper.getRandomSector(SectorType.SPAWN,
          sector -> !sector.equals(event.getSector()) && sector.getStatistics().isOnline()
              && sector.getStatistics().getLoad() < 68.8D && sector.getStatistics().getTps() > 5.58D
              && sector.getStatistics().getPlayers()
              < this.plugin.getPlatformConfiguration().slotWrapper.spigotSlots);
      newSectorOptional.ifPresent(event::setSector);
      isSectorFull = !newSectorOptional.isPresent();
    }

    if (isSectorFull) {
      event.setCancelled(true);
      SectorHelper.sendMessageIfNoPortalAntiSpam(player,
          this.plugin.getPlatformConfiguration().messagesWrapper.sectorIsFull, event.isPortal());
      return;
    }

    this.plugin.getPlatformUserFactory().findUserByUniqueId(player.getUniqueId())
        .ifPresent(user -> {
          final PlatformUserDropSettings dropSettings = user.getDropSettings();
          this.plugin.getRedisAdapter().sendPacket(
              new PlatformUserSynchronizeSomeDropSettingsDataPacket(user.getUniqueId(),
                  dropSettings.getTurboDropMultiplier(), dropSettings.isCobbleStone(),
                  dropSettings.getTurboDropTime(), dropSettings.getCurrentXP(),
                  dropSettings.getNeededXP(), dropSettings.getLevel()), "rhc_master_controller",
              "rhc_platform");
          if (user.isEndPortalChange()) {
            user.setEndPortalChange(false);
            return;
          }

          final boolean wasGroupTeleportsChange = user.isGroupTeleportsChange();
          if (wasGroupTeleportsChange) {
            player.teleport(PlatformPlugin.getInstance()
                .getPlatformConfiguration().spawnLocationWrapper.unwrap());
            user.setGroupTeleportsChange(false);
          }

          if (!wasGroupTeleportsChange) {
            if (user.isInCombat()) {
              if (!player.hasPermission("platform-combat-bypass")) {
                event.setCancelled(true);
                ChatHelper.sendMessage(player,
                    this.plugin.getPlatformConfiguration().messagesWrapper.cannotChangeSectorInCombat.replace(
                        "{TIME}",
                        TimeHelper.timeToString(
                            user.getCombatTime() - System.currentTimeMillis())));
                return;
              }

              user.setCombatTime(0L);
              this.plugin.getRedisAdapter().sendPacket(
                  new PlatformUserCombatTimeUpdatePacket(user.getUniqueId(), user.getCombatTime()),
                  "rhc_master_controller", "rhc_platform");
            }

            if (!event.isTeleport()) {
              final PlatformUserCooldownCache cooldownCache = user.getCooldownCache();
              if (cooldownCache.hasUserCooldown(PlatformUserCooldownType.SECTOR_CHANGE)) {
                event.setCancelled(true);
                ChatHelper.sendMessage(player,
                    this.plugin.getPlatformConfiguration().messagesWrapper.sectorChangeIsCooldowned.replace(
                        "{TIME}", TimeHelper.timeToString(
                            cooldownCache.getUserCooldown(
                                PlatformUserCooldownType.SECTOR_CHANGE))));
                return;
              }

              cooldownCache.putUserCooldown(PlatformUserCooldownType.SECTOR_CHANGE);
            }
          }

          AdapterPlugin.getInstance().getNameTagFactory().removeNameTag(player);
          user.setPreparedForNameTagCreation(true);
          this.plugin.getServer().getScheduler().runTaskLaterAsynchronously(this.plugin, () -> {
            if (user.isPreparedForNameTagCreation()) {
              AdapterPlugin.getInstance().getNameTagFactory().createNameTag(player);
              AdapterPlugin.getInstance().getNameTagFactory().updateNameTag(player);
            }
          }, 100L);
        });
  }
}
