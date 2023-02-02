package pl.rosehc.platform.user.task;

import org.bukkit.entity.Player;
import pl.rosehc.actionbar.PrioritizedActionBarConstants;
import pl.rosehc.actionbar.PrioritizedActionBarPlugin;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.adapter.helper.TimeHelper;
import pl.rosehc.controller.ControllerPanicHelper;
import pl.rosehc.controller.packet.platform.user.PlatformUserCombatTimeUpdatePacket;
import pl.rosehc.platform.PlatformPlugin;

public final class PlatformUserCombatUpdateTask implements Runnable {

  private final PlatformPlugin plugin;

  public PlatformUserCombatUpdateTask(final PlatformPlugin plugin) {
    this.plugin = plugin;
    this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(this.plugin, this, 20L, 20L);
  }

  @Override
  public void run() {
    for (final Player player : this.plugin.getServer().getOnlinePlayers()) {
      this.plugin.getPlatformUserFactory().findUserByUniqueId(player.getUniqueId())
          .ifPresent(user -> {
            if (!user.isInCombat() || ControllerPanicHelper.isInPanic()
                || this.plugin.getPlatformConfiguration().serverFreezeState) {
              if (user.getCombatTime() != 0L) {
                user.setCombatTime(0L);
                PrioritizedActionBarPlugin.getInstance().getPrioritizedActionBarFactory()
                    .updateActionBar(player.getUniqueId(), ChatHelper.colored(
                            this.plugin.getPlatformConfiguration().messagesWrapper.combatEndInfo),
                        PrioritizedActionBarConstants.ANTI_LOGOUT_ACTION_BAR_PRIORITY);
                this.plugin.getRedisAdapter().sendPacket(
                    new PlatformUserCombatTimeUpdatePacket(user.getUniqueId(),
                        user.getCombatTime()), "rhc_master_controller", "rhc_platform");
              }
              return;
            }

            PrioritizedActionBarPlugin.getInstance().getPrioritizedActionBarFactory()
                .updateActionBar(player.getUniqueId(), ChatHelper.colored(
                        this.plugin.getPlatformConfiguration().messagesWrapper.combatLeftTimeInfo.replace(
                            "{TIME}", TimeHelper.timeToString(
                                user.getCombatTime() - System.currentTimeMillis()))),
                    PrioritizedActionBarConstants.ANTI_LOGOUT_ACTION_BAR_PRIORITY);
          });
    }
  }
}
