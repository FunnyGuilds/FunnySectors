package pl.rosehc.sectors.listener;

import java.util.Objects;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.adapter.helper.EventCompletionStage;
import pl.rosehc.adapter.helper.LocationHelper;
import pl.rosehc.controller.ControllerPanicHelper;
import pl.rosehc.sectors.SectorsPlugin;
import pl.rosehc.sectors.helper.ConnectHelper;
import pl.rosehc.sectors.helper.SectorHelper;
import pl.rosehc.sectors.sector.SectorConnectingEvent;
import pl.rosehc.sectors.sector.SectorType;
import pl.rosehc.sectors.sector.user.SectorUser;

public final class PlayerMoveListener implements Listener {

  private final SectorsPlugin plugin;

  public PlayerMoveListener(final SectorsPlugin plugin) {
    this.plugin = plugin;
  }

  @EventHandler
  public void onMove(final PlayerMoveEvent event) {
    final Location to = event.getTo();
    if (LocationHelper.isSameLocationXYZ(event.getFrom(), to)) {
      return;
    }

    if (ControllerPanicHelper.isInPanic()) {
      event.setCancelled(true);
      return;
    }

    final Player player = event.getPlayer();
    final SectorUser user = this.plugin.getSectorUserFactory().findUserByPlayer(player);
    if (Objects.isNull(user) || user.isRedirecting() || !user.canRunMovement()) {
      return;
    }

    user.markMovementAsRan();
    if (SectorHelper.isInsideBorder(to)) {
      SectorHelper.knockFromSector(player);
      ChatHelper.sendMessage(player,
          this.plugin.getSectorsConfiguration().messagesWrapper.sectorNotFound);
      return;
    }

    SectorHelper.getAt(to).ifPresent(sector -> {
      if (sector.getType().equals(SectorType.SPAWN)) {
        sector = SectorHelper.getRandomSector(SectorType.SPAWN).orElse(sector);
      }

      if (!sector.getStatistics().isOnline()) {
        SectorHelper.knockFromSector(player);
        ChatHelper.sendMessage(player,
            this.plugin.getSectorsConfiguration().messagesWrapper.sectorIsOffline);
        return;
      }

      if (sector.getStatistics().getLoad() >= 68.8D || sector.getStatistics().getTps() <= 5.58D) {
        SectorHelper.knockFromSector(player);
        ChatHelper.sendMessage(player,
            this.plugin.getSectorsConfiguration().messagesWrapper.sectorIsHeavilyLoaded);
        return;
      }

      user.setRedirecting(true);
      final SectorConnectingEvent sectorConnectingEvent = new SectorConnectingEvent(player, sector,
          false, false);
      final EventCompletionStage completionStage = new EventCompletionStage(
          () -> this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin,
              () -> ConnectHelper.connect(player, user, sectorConnectingEvent.getSector(), to),
              3L));
      sectorConnectingEvent.setCompletionStage(completionStage);
      this.plugin.getServer().getPluginManager().callEvent(sectorConnectingEvent);
      if (sectorConnectingEvent.isCancelled()) {
        SectorHelper.knockFromSector(player);
        user.setRedirecting(false);
        return;
      }

      completionStage.postFire();
    });
  }
}
