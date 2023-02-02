package pl.rosehc.platform.hologram;

import java.util.List;
import java.util.concurrent.TimeUnit;
import org.bukkit.entity.Player;
import pl.rosehc.platform.PlatformPlugin;

public final class HologramUpdateTask implements Runnable {

  private final PlatformPlugin plugin;

  public HologramUpdateTask(final PlatformPlugin plugin) {
    this.plugin = plugin;
    this.plugin.getScheduledExecutorService().scheduleAtFixedRate(this, 1L, 1L, TimeUnit.SECONDS);
  }

  @Override
  public void run() {
    for (final Player player : this.plugin.getServer().getOnlinePlayers()) {
      final List<Hologram> hologramList = this.plugin.getHologramFactory()
          .findHologramList(player.getUniqueId());
      if (!hologramList.isEmpty()) {
        for (final Hologram hologram : hologramList) {
          hologram.update(player);
        }
      }
    }
  }
}
