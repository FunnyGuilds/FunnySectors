package pl.rosehc.adapter.scoreboard;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.rosehc.adapter.AdapterPlugin;

public final class ScoreboardListeners implements Listener {

  private final AdapterPlugin plugin;

  public ScoreboardListeners(final AdapterPlugin plugin) {
    this.plugin = plugin;
    this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onJoin(final PlayerJoinEvent event) {
    this.plugin.getScoreboardFactory().createScoreboard(event.getPlayer());
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onQuit(final PlayerQuitEvent event) {
    this.plugin.getScoreboardFactory().removeScoreboard(event.getPlayer().getUniqueId());
  }
}