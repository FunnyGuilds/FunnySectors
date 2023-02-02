package pl.rosehc.adapter.nametag;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.rosehc.adapter.AdapterPlugin;

public final class NameTagListeners implements Listener {

  private final AdapterPlugin plugin;

  public NameTagListeners(final AdapterPlugin plugin) {
    this.plugin = plugin;
    this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onJoin(final PlayerJoinEvent event) {
    if (Objects.isNull(event.getPlayer())) {
      return;
    }

    this.plugin.getScheduledExecutorService().schedule(() -> {
      this.plugin.getNameTagFactory().createNameTag(event.getPlayer());
      this.plugin.getNameTagFactory().updateNameTag(event.getPlayer());
    }, 2L, TimeUnit.SECONDS);
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onQuit(final PlayerQuitEvent event) {
    this.plugin.getNameTagFactory().removeNameTag(event.getPlayer());
  }
}
