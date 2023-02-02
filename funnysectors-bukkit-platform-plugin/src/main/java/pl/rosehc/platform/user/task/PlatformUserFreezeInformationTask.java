package pl.rosehc.platform.user.task;

import org.bukkit.Bukkit;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.platform.PlatformPlugin;

public final class PlatformUserFreezeInformationTask implements Runnable {

  private final PlatformPlugin plugin;

  public PlatformUserFreezeInformationTask(final PlatformPlugin plugin) {
    this.plugin = plugin;
    this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(this.plugin, this, 60L, 60L);
  }

  @Override
  public void run() {
    if (this.plugin.getPlatformConfiguration().serverFreezeState) {
      Bukkit.getOnlinePlayers().forEach(player -> ChatHelper.sendTitle(player,
          this.plugin.getPlatformConfiguration().messagesWrapper.serverIsFrozenInfoTitle,
          this.plugin.getPlatformConfiguration().messagesWrapper.serverIsFrozenInfoSubTitle, 10, 30,
          10));
    }
  }
}
