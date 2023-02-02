package pl.rosehc.platform.command.staff;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Permission;
import me.vaperion.blade.annotation.Sender;
import me.vaperion.blade.exception.BladeExitMessage;
import org.bukkit.entity.Player;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.controller.packet.platform.user.PlatformUserGodStateUpdatePacket;
import pl.rosehc.platform.PlatformPlugin;
import pl.rosehc.platform.user.PlatformUser;

public final class GodCommand {

  private final PlatformPlugin plugin;

  public GodCommand(final PlatformPlugin plugin) {
    this.plugin = plugin;
  }

  @Permission("platform-command-god")
  @Command(value = "god", description = "Włącza nieśmiertelność dla administratora")
  public void handleGod(final @Sender Player player) {
    final PlatformUser user = this.plugin.getPlatformUserFactory()
        .findUserByUniqueId(player.getUniqueId()).orElseThrow(() -> new BladeExitMessage(
            ChatHelper.colored(
                this.plugin.getPlatformConfiguration().messagesWrapper.playerNotFound.replace(
                    "{PLAYER_NAME}", player.getName()))));
    user.setGod(!user.isGod());
    ChatHelper.sendMessage(player,
        user.isGod() ? this.plugin.getPlatformConfiguration().messagesWrapper.godEnabledInfo
            : this.plugin.getPlatformConfiguration().messagesWrapper.godDisabledInfo);
    this.plugin.getRedisAdapter()
        .sendPacket(new PlatformUserGodStateUpdatePacket(user.getUniqueId(), user.isGod()),
            "rhc_master_controller", "rhc_platform");
  }
}
