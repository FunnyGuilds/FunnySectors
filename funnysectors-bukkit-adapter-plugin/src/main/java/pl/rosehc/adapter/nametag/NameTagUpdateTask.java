package pl.rosehc.adapter.nametag;

import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.rosehc.adapter.AdapterPlugin;

public final class NameTagUpdateTask implements Runnable {

  private final AdapterPlugin plugin;

  public NameTagUpdateTask(final AdapterPlugin plugin) {
    this.plugin = plugin;
    this.plugin.getScheduledExecutorService().scheduleAtFixedRate(this, 5L, 5L, TimeUnit.SECONDS);
  }

  @Override
  public void run() {
    for (final Player player : Bukkit.getOnlinePlayers()) {
      this.plugin.getNameTagFactory().updateNameTag(player);
    }
  }
}
