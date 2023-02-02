package pl.rosehc.adapter.scoreboard;

import java.util.concurrent.TimeUnit;
import org.bukkit.scheduler.BukkitRunnable;
import pl.rosehc.adapter.AdapterPlugin;

public final class ScoreboardUpdateTask extends BukkitRunnable {

  private final AdapterPlugin plugin;

  public ScoreboardUpdateTask(final AdapterPlugin plugin) {
    this.plugin = plugin;
    this.plugin.getScheduledExecutorService().scheduleAtFixedRate(this, 1L, 1L, TimeUnit.SECONDS);
  }

  @Override
  public void run() {
    for (final ScoreboardPlayer scoreboardPlayer : this.plugin.getScoreboardFactory()
        .getScoreboardCollection()) {
      scoreboardPlayer.update(false);
    }
  }
}