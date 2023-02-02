package pl.rosehc.platform.vanishingblock;

import pl.rosehc.platform.PlatformPlugin;

public final class VanishingBlockResetTask implements Runnable {

  private final PlatformPlugin plugin;

  public VanishingBlockResetTask(final PlatformPlugin plugin) {
    this.plugin = plugin;
    this.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(this.plugin, this, 40L, 40L);
  }

  @Override
  public void run() {
    this.plugin.getVanishingBlockFactory().reset(false);
  }
}
