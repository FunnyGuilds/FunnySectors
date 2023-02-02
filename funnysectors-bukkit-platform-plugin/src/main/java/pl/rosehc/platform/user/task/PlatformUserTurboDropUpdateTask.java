package pl.rosehc.platform.user.task;

import org.bukkit.entity.Player;
import pl.rosehc.actionbar.PrioritizedActionBarConstants;
import pl.rosehc.actionbar.PrioritizedActionBarPlugin;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.adapter.helper.TimeHelper;
import pl.rosehc.platform.PlatformPlugin;
import pl.rosehc.platform.user.subdata.PlatformUserDropSettings;

public final class PlatformUserTurboDropUpdateTask implements Runnable {

  private final PlatformPlugin plugin;

  public PlatformUserTurboDropUpdateTask(final PlatformPlugin plugin) {
    this.plugin = plugin;
    this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(this.plugin, this, 10L, 10L);
  }

  @Override
  public void run() {
    final long globalTurboDropTime = this.plugin.getPlatformConfiguration().dropSettingsWrapper.turboDropTime;
    final String formattedGlobalTurboDropTime = TimeHelper.timeToString(globalTurboDropTime
        - System.currentTimeMillis()), formattedGlobalTurboDropMultiplier = String.format("%.2f",
        this.plugin.getPlatformConfiguration().dropSettingsWrapper.turboDropMultiplier);
    for (final Player player : this.plugin.getServer().getOnlinePlayers()) {
      this.plugin.getPlatformUserFactory().findUserByUniqueId(player.getUniqueId())
          .ifPresent(user -> {
            final StringBuilder builder = new StringBuilder();
            if (globalTurboDropTime > System.currentTimeMillis()) {
              builder.append(
                  this.plugin.getPlatformConfiguration().messagesWrapper.globalTurboDropIsActiveActionBarInfo.replace(
                          "{TIME}", formattedGlobalTurboDropTime)
                      .replace("{MULTIPLIER}", formattedGlobalTurboDropMultiplier));
            }

            final PlatformUserDropSettings dropSettings = user.getDropSettings();
            if (dropSettings.getTurboDropTime() > System.currentTimeMillis()) {
              builder.append('\n');
              builder.append(
                  this.plugin.getPlatformConfiguration().messagesWrapper.yourTurboDropIsActiveActionBarInfo.replace(
                          "{TIME}", TimeHelper.timeToString(
                              dropSettings.getTurboDropTime() - System.currentTimeMillis()))
                      .replace("{MULTIPLIER}",
                          String.format("%.2f", user.getDropSettings().getTurboDropMultiplier())));
            }

            if (builder.length() != 0) {
              PrioritizedActionBarPlugin.getInstance().getPrioritizedActionBarFactory()
                  .updateActionBar(player.getUniqueId(), ChatHelper.colored(builder.toString()),
                      PrioritizedActionBarConstants.TURBO_DROP_ACTION_BAR_PRIORITY);
            }
          });
    }
  }
}
