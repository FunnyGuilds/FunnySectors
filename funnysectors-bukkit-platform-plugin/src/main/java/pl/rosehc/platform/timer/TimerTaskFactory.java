package pl.rosehc.platform.timer;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.platform.PlatformPlugin;

public final class TimerTaskFactory {

  private final Map<UUID, BukkitTask> timerTaskMap = new ConcurrentHashMap<>();

  public void addTimer(final Player player, final Location targetLocation, final int seconds) {
    if (player.hasPermission("platform-timer-bypass")) {
      ChatHelper.sendMessage(player, PlatformPlugin.getInstance()
          .getPlatformConfiguration().messagesWrapper.teleportationHasBeenFinished);
      player.teleport(targetLocation);
      return;
    }

    this.removeTimer(player.getUniqueId());
    this.timerTaskMap.put(player.getUniqueId(), Bukkit.getScheduler()
        .runTaskTimerAsynchronously(PlatformPlugin.getInstance(),
            new TimerTask(PlatformPlugin.getInstance(), player, player.getLocation(),
                targetLocation, seconds), 0L, 20L));
    ChatHelper.sendMessage(player, PlatformPlugin.getInstance()
        .getPlatformConfiguration().messagesWrapper.teleportationHasBeenStarted.replace("{SECONDS}",
            String.valueOf(seconds)));
  }

  public void removeTimer(final UUID uniqueId) {
    final BukkitTask task = this.timerTaskMap.remove(uniqueId);
    if (task != null) {
      task.cancel();
    }
  }
}
