package pl.rosehc.platform.listener.player;

import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import pl.rosehc.adapter.helper.NumberHelper;
import pl.rosehc.adapter.helper.TimeHelper;
import pl.rosehc.controller.wrapper.platform.PlatformUserCooldownType;
import pl.rosehc.platform.PlatformPlugin;
import pl.rosehc.platform.end.EndHelper;
import pl.rosehc.platform.user.PlatformUser;
import pl.rosehc.sectors.SectorsPlugin;
import pl.rosehc.sectors.helper.SectorHelper;
import pl.rosehc.sectors.sector.SectorType;

public final class PlayerPortalListener implements Listener {

  private final PlatformPlugin plugin;

  public PlayerPortalListener(final PlatformPlugin plugin) {
    this.plugin = plugin;
  }

  @EventHandler
  public void onPortal(final PlayerPortalEvent event) {
    if (event.getCause().equals(TeleportCause.NETHER_PORTAL)) {
      event.setCancelled(true);
      return;
    }

    if (event.getCause().equals(TeleportCause.END_PORTAL)) {
      final Player player = event.getPlayer();
      final Optional<PlatformUser> userOptional = this.plugin.getPlatformUserFactory()
          .findUserByUniqueId(player.getUniqueId());
      if (!userOptional.isPresent()) {
        event.setCancelled(true);
        return;
      }

      final PlatformUser user = userOptional.get();
      if (user.getCooldownCache().hasUserCooldown(PlatformUserCooldownType.END_BAN)
          && !player.hasPermission("platform-end-ban-bypass")) {
        event.setCancelled(true);
        SectorHelper.sendMessageIfNoPortalAntiSpam(player,
            this.plugin.getPlatformConfiguration().messagesWrapper.youAreBannedInEnd.replace(
                "{TIME}", TimeHelper.timeToString(
                    user.getCooldownCache().getUserCooldown(PlatformUserCooldownType.END_BAN))),
            true);
        SectorHelper.knockFromEndPortal(player);
        return;
      }

      if (SectorsPlugin.getInstance().getSectorFactory().getCurrentSector().getType()
          .equals(SectorType.END)) {
        final int portalPointState = EndHelper.checkPortalPoint(player.getLocation());
        if (portalPointState == -1) {
          event.setCancelled(true);
          SectorHelper.sendMessageIfNoPortalAntiSpam(player,
              this.plugin.getPlatformConfiguration().messagesWrapper.endPortalPointNotSet, true);
          SectorHelper.knockFromEndPortal(player);
          return;
        }

        if (portalPointState == 0) {
          event.setCancelled(true);
          SectorHelper.sendMessageIfNoPortalAntiSpam(player,
              this.plugin.getPlatformConfiguration().messagesWrapper.endPortalPointNotActive, true);
          SectorHelper.knockFromEndPortal(player);
          return;
        }
      }

      final Location to;
      if (!SectorsPlugin.getInstance().getSectorFactory().getCurrentSector().getType()
          .equals(SectorType.END)) {
        to = new Location(Bukkit.getWorld("world_the_end"),
            NumberHelper.range(this.plugin.getPlatformConfiguration().endMinRandCoordinate,
                this.plugin.getPlatformConfiguration().endMaxRandomCoordinate), 0D,
            NumberHelper.range(this.plugin.getPlatformConfiguration().endMinRandCoordinate,
                this.plugin.getPlatformConfiguration().endMaxRandomCoordinate));
        to.setY(to.getWorld().getHighestBlockYAt((int) to.getX(), (int) to.getZ()));
      } else {
        to = this.plugin.getEndPointFactory().getCurrentTeleportationPoint();
      }

      user.setEndPortalChange(true);
      event.setTo(to);
    }
  }
}
