package pl.rosehc.platform.command.staff;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Permission;
import me.vaperion.blade.annotation.Sender;
import me.vaperion.blade.exception.BladeExitMessage;
import org.bukkit.entity.Player;
import pl.rosehc.actionbar.PrioritizedActionBarConstants;
import pl.rosehc.actionbar.PrioritizedActionBarPlugin;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.controller.packet.platform.user.PlatformUserVanishStateUpdatePacket;
import pl.rosehc.platform.PlatformPlugin;
import pl.rosehc.platform.user.PlatformUser;

public final class VanishCommand {

  private final PlatformPlugin plugin;

  public VanishCommand(final PlatformPlugin plugin) {
    this.plugin = plugin;
  }

  @Permission("platform-command-vanish")
  @Command(value = {"vanish",
      "v"}, description = "Ukrywa administratora przed innymi graczami na serwerze")
  public void handleVanish(final @Sender Player player) {
    final PlatformUser user = this.plugin.getPlatformUserFactory()
        .findUserByUniqueId(player.getUniqueId()).orElseThrow(() -> new BladeExitMessage(
            ChatHelper.colored(
                this.plugin.getPlatformConfiguration().messagesWrapper.playerNotFound.replace(
                    "{PLAYER_NAME}", player.getName()))));
    user.setVanish(!user.isVanish());
    ChatHelper.sendMessage(player,
        user.isVanish() ? this.plugin.getPlatformConfiguration().messagesWrapper.vanishEnabledInfo
            : this.plugin.getPlatformConfiguration().messagesWrapper.vanishDisabledInfo);
    PrioritizedActionBarPlugin.getInstance().getPrioritizedActionBarFactory()
        .updateActionBar(player.getUniqueId(), ChatHelper.colored(
                this.plugin.getPlatformConfiguration().messagesWrapper.vanishYouAreInvisibleActionBar),
            PrioritizedActionBarConstants.VANISH_ACTION_BAR_PRIORITY);
    this.plugin.getRedisAdapter()
        .sendPacket(new PlatformUserVanishStateUpdatePacket(user.getUniqueId(), user.isVanish()),
            "rhc_master_controller", "rhc_platform");
    for (final Player target : this.plugin.getServer().getOnlinePlayers()) {
      if (!target.equals(player)) {
        if (user.isVanish() && !target.hasPermission("platform-vanish-bypass")) {
          target.hidePlayer(player);
        } else if (!user.isVanish()) {
          target.showPlayer(player);
        }
      }
    }
  }
}
