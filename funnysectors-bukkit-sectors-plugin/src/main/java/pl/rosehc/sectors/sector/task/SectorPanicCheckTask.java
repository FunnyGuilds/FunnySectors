package pl.rosehc.sectors.sector.task;

import org.bukkit.Bukkit;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.adapter.helper.TimeHelper;
import pl.rosehc.controller.ControllerPanicHelper;
import pl.rosehc.sectors.SectorsPlugin;

public final class SectorPanicCheckTask implements Runnable {

  private final SectorsPlugin plugin;

  public SectorPanicCheckTask(final SectorsPlugin plugin) {
    this.plugin = plugin;
    this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(this.plugin, this, 1L, 1L);
  }

  @Override
  public void run() {
    if (!ControllerPanicHelper.isInPanic()) {
      return;
    }

    this.plugin.getServer().getOnlinePlayers().forEach(
        player -> ChatHelper.sendTitle(player, "&cSERWER JEST W TRYBIE PANIKI",
            "&cWyłączenie sektora za: " + TimeHelper.timeToString(
                ControllerPanicHelper.getTimeToDisable())));
    if (ControllerPanicHelper.canDisable()) {
      ControllerPanicHelper.markDisabling();
      this.plugin.getServer().getScheduler()
          .scheduleSyncDelayedTask(this.plugin, () -> Bukkit.getServer().shutdown());
    }
  }
}
