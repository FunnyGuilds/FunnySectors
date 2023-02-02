package pl.rosehc.platform.listener.player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import pl.rosehc.platform.PlatformPlugin;

public final class PlayerTeleportListener implements Listener {

  private final PlatformPlugin plugin;

  public PlayerTeleportListener(final PlatformPlugin plugin) {
    this.plugin = plugin;
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onTeleport(final PlayerTeleportEvent event) {
    if (event.getCause().equals(TeleportCause.ENDER_PEARL)) {
      event.setCancelled(true);
      event.getPlayer().teleport(event.getTo());
    }
  }
}
