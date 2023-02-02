package pl.rosehc.platform.end;

import pl.rosehc.platform.PlatformPlugin;

public final class EndPointUpdateTask implements Runnable {

  private final PlatformPlugin plugin;

  public EndPointUpdateTask(final PlatformPlugin plugin) {
    this.plugin = plugin;
    this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(this.plugin, this, 40L, 20L);
  }

  @Override
  public void run() {
    this.plugin.getEndPointFactory().updatePoints();
  }
}
