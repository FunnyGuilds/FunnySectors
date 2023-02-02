package pl.rosehc.platform.timer;

import java.util.Objects;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import pl.rosehc.actionbar.PrioritizedActionBarConstants;
import pl.rosehc.actionbar.PrioritizedActionBarPlugin;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.adapter.helper.LocationHelper;
import pl.rosehc.controller.ControllerPanicHelper;
import pl.rosehc.platform.PlatformPlugin;

public final class TimerTask implements Runnable {

  private final PlatformPlugin plugin;
  private final Player player;
  private final Location startLocation, targetLocation;
  private int seconds;

  public TimerTask(final PlatformPlugin plugin, final Player player, final Location startLocation,
      final Location targetLocation, final int seconds) {
    this.plugin = plugin;
    this.player = player;
    this.startLocation = startLocation;
    this.targetLocation = targetLocation;
    this.seconds = seconds;
  }

  @Override
  public void run() {
    if (!this.player.isOnline() || ControllerPanicHelper.isInPanic()
        || this.plugin.getPlatformConfiguration().serverFreezeState) {
      this.plugin.getTimerTaskFactory().removeTimer(this.player.getUniqueId());
      return;
    }

    final Location location = this.player.getLocation();
    if (!LocationHelper.isSameLocationXZ(this.startLocation, location)) {
      ChatHelper.sendMessage(this.player,
          this.plugin.getPlatformConfiguration().messagesWrapper.teleportationHasBeenCancelled);
      this.plugin.getTimerTaskFactory().removeTimer(this.player.getUniqueId());
      return;
    }

    if (this.seconds <= 0) {
      ChatHelper.sendMessage(this.player,
          this.plugin.getPlatformConfiguration().messagesWrapper.teleportationHasBeenFinished);
      if (Objects.nonNull(
          this.plugin.getPlatformConfiguration().messagesWrapper.teleportationHasBeenFinishedActionBar)
          && !this.plugin.getPlatformConfiguration().messagesWrapper.teleportationHasBeenFinishedActionBar.trim()
          .isEmpty()) {
        PrioritizedActionBarPlugin.getInstance().getPrioritizedActionBarFactory()
            .updateActionBar(this.player.getUniqueId(), ChatHelper.colored(
                    this.plugin.getPlatformConfiguration().messagesWrapper.teleportationHasBeenFinishedActionBar),
                PrioritizedActionBarConstants.TIMER_ACTION_BAR_PRIORITY);
      }

      this.plugin.getTimerTaskFactory().removeTimer(this.player.getUniqueId());
      this.plugin.getServer().getScheduler()
          .scheduleSyncDelayedTask(this.plugin, () -> this.player.teleport(this.targetLocation));
      return;
    }

    PrioritizedActionBarPlugin.getInstance().getPrioritizedActionBarFactory()
        .updateActionBar(this.player.getUniqueId(), ChatHelper.colored(
                this.plugin.getPlatformConfiguration().messagesWrapper.teleportationInfoActionBar.replace(
                    "{SECONDS}", String.valueOf(this.seconds))),
            PrioritizedActionBarConstants.TIMER_ACTION_BAR_PRIORITY);
    this.seconds--;
  }
}
