package pl.rosehc.platform.user.task;

import org.bukkit.entity.Player;
import pl.rosehc.actionbar.PrioritizedActionBarConstants;
import pl.rosehc.actionbar.PrioritizedActionBarPlugin;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.platform.PlatformPlugin;
import pl.rosehc.platform.user.PlatformUser;

public final class PlatformUserVanishUpdateTask implements Runnable {

  private final PlatformPlugin plugin;

  public PlatformUserVanishUpdateTask(final PlatformPlugin plugin) {
    this.plugin = plugin;
    this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(this.plugin, this, 20L, 20L);
  }

  @Override
  public void run() {
    for (final Player player : this.plugin.getServer().getOnlinePlayers()) {
      if (this.plugin.getPlatformUserFactory().findUserByUniqueId(player.getUniqueId())
          .filter(PlatformUser::isVanish).isPresent()) {
        PrioritizedActionBarPlugin.getInstance().getPrioritizedActionBarFactory()
            .updateActionBar(player.getUniqueId(), ChatHelper.colored(
                    this.plugin.getPlatformConfiguration().messagesWrapper.vanishYouAreInvisibleActionBar),
                PrioritizedActionBarConstants.VANISH_ACTION_BAR_PRIORITY);
      }
    }
  }
}
