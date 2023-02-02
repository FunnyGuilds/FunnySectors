package pl.rosehc.platform.deposit;

import org.bukkit.entity.Player;
import pl.rosehc.platform.PlatformPlugin;

public final class DepositItemCheckTask implements Runnable {

  private final PlatformPlugin plugin;

  public DepositItemCheckTask(final PlatformPlugin plugin) {
    this.plugin = plugin;
    this.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(this.plugin, this, 160L, 160L);
  }

  @Override
  public void run() {
    for (final Player player : this.plugin.getServer().getOnlinePlayers()) {
      DepositHelper.limit(player);
    }
  }
}
