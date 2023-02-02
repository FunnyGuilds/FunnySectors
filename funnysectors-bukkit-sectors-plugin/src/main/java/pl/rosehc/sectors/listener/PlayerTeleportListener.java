package pl.rosehc.sectors.listener;

import java.util.Objects;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.adapter.helper.EventCompletionStage;
import pl.rosehc.controller.ControllerPanicHelper;
import pl.rosehc.sectors.SectorsPlugin;
import pl.rosehc.sectors.helper.ConnectHelper;
import pl.rosehc.sectors.helper.SectorHelper;
import pl.rosehc.sectors.sector.SectorConnectingEvent;
import pl.rosehc.sectors.sector.SectorType;
import pl.rosehc.sectors.sector.user.SectorUser;

public final class PlayerTeleportListener implements Listener {

  private final SectorsPlugin plugin;

  public PlayerTeleportListener(final SectorsPlugin plugin) {
    this.plugin = plugin;
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onTeleport(final PlayerTeleportEvent event) {
    final Player player = event.getPlayer();
    final SectorUser user = this.plugin.getSectorUserFactory().findUserByPlayer(player);
    if (Objects.isNull(user) || user.isRedirecting()) {
      event.setCancelled(true);
      return;
    }

    final Location to = event.getTo();
    if (SectorHelper.isInsideBorder(to)) {
      event.setCancelled(true);
      ChatHelper.sendMessage(player,
          this.plugin.getSectorsConfiguration().messagesWrapper.sectorNotFound);
      return;
    }

    if (ControllerPanicHelper.isInPanic()) {
      event.setCancelled(true);
      return;
    }

    final boolean isEndPortal = event.getCause().equals(TeleportCause.END_PORTAL);
    user.markMovementAsRan();
    (isEndPortal && !this.plugin.getSectorFactory().getCurrentSector().getType()
        .equals(SectorType.END) ? SectorHelper.getRandomSector(SectorType.END, ignored -> true)
        : SectorHelper.getAt(to)).ifPresent(sector -> {
      event.setCancelled(true);
      if (sector.getType().equals(SectorType.END) && (!isEndPortal || !sector.getStatistics()
          .isOnline())) {
        sector = SectorHelper.getRandomSector(SectorType.END).orElse(sector);
      }
      if (sector.getType().equals(SectorType.SPAWN)) {
        sector = SectorHelper.getRandomSector(SectorType.SPAWN).orElse(sector);
      }

      if (!sector.getStatistics().isOnline()) {
        SectorHelper.sendMessageIfNoPortalAntiSpam(player, user,
            this.plugin.getSectorsConfiguration().messagesWrapper.sectorIsOffline, isEndPortal);
        if (isEndPortal) {
          SectorHelper.knockFromSector(player);
        }
        return;
      }

      if (sector.getStatistics().getLoad() >= 68.8D || sector.getStatistics().getTps() <= 5.58D) {
        SectorHelper.sendMessageIfNoPortalAntiSpam(player, user,
            this.plugin.getSectorsConfiguration().messagesWrapper.sectorIsHeavilyLoaded,
            isEndPortal);
        if (isEndPortal) {
          SectorHelper.knockFromSector(player);
        }
        return;
      }

      user.setRedirecting(true);
      final SectorConnectingEvent sectorConnectingEvent = new SectorConnectingEvent(player, sector,
          true, isEndPortal);
      final EventCompletionStage completionStage = new EventCompletionStage(
          () -> ConnectHelper.connect(player, user, sectorConnectingEvent.getSector(), to));
      sectorConnectingEvent.setCompletionStage(completionStage);
      this.plugin.getServer().getPluginManager().callEvent(sectorConnectingEvent);
      if (sectorConnectingEvent.isCancelled()) {
        event.setCancelled(true);
        if (isEndPortal) {
          SectorHelper.knockFromSector(player);
        }

        user.setRedirecting(false);
        return;
      }

      completionStage.postFire();
    });
  }
}
