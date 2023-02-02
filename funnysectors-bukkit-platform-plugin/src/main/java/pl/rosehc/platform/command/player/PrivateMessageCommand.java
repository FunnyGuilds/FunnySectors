package pl.rosehc.platform.command.player;

import me.vaperion.blade.annotation.Combined;
import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Name;
import me.vaperion.blade.annotation.Sender;
import me.vaperion.blade.exception.BladeExitMessage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.controller.packet.platform.user.PlatformUserLastPrivateMessageUpdatePacket;
import pl.rosehc.platform.PlatformPlugin;
import pl.rosehc.platform.user.PlatformUser;

public final class PrivateMessageCommand {

  private final PlatformPlugin plugin;

  public PrivateMessageCommand(final PlatformPlugin plugin) {
    this.plugin = plugin;
  }

  @Command(value = {"reply",
      "r"}, description = "Wysyła wiadomość prywatną do ostatniego gracza, z którym pisałeś.")
  public void handleReply(final @Sender Player player, @Name("message") @Combined String message) {
    message = ChatColor.stripColor(ChatHelper.colored(message));
    if (message.trim().isEmpty()) {
      throw new BladeExitMessage(ChatHelper.colored(
          this.plugin.getPlatformConfiguration().messagesWrapper.privateMessageCannotBeEmpty));
    }

    final PlatformUser playerUser = this.plugin.getPlatformUserFactory()
        .findUserByUniqueId(player.getUniqueId()).orElseThrow(() -> new BladeExitMessage(
            this.plugin.getPlatformConfiguration().messagesWrapper.playerNotFound.replace(
                "{PLAYER_NAME}", player.getName())));
    if (playerUser.getLastPrivateMessage() == null) {
      throw new BladeExitMessage(ChatHelper.colored(
          this.plugin.getPlatformConfiguration().messagesWrapper.privateMessageLastPlayerNotFound));
    }

    final PlatformUser targetUser = this.plugin.getPlatformUserFactory()
        .findUserByUniqueId(playerUser.getLastPrivateMessage()).orElseThrow(
            () -> new BladeExitMessage(
                this.plugin.getPlatformConfiguration().messagesWrapper.privateMessageLastPlayerNotFound));
    if (!targetUser.isOnline()) {
      throw new BladeExitMessage(ChatHelper.colored(
          this.plugin.getPlatformConfiguration().messagesWrapper.playerIsOffline.replace(
              "{PLAYER_NAME}", targetUser.getNickname())));
    }

    this.sendPrivateMessage(player, playerUser, targetUser, message);
  }

  @Command(value = {"tell", "msg",
      "m"}, description = "Wysyła wiadomość prywatną do danego gracza.")
  public void handleTell(final @Sender Player player, final @Name("player") PlatformUser targetUser,
      @Name("message") @Combined String message) {
    message = ChatColor.stripColor(ChatHelper.colored(message));
    if (message.trim().isEmpty()) {
      throw new BladeExitMessage(ChatHelper.colored(
          this.plugin.getPlatformConfiguration().messagesWrapper.privateMessageCannotBeEmpty));
    }

    if (targetUser.getUniqueId().equals(player.getUniqueId())) {
      throw new BladeExitMessage(ChatHelper.colored(
          this.plugin.getPlatformConfiguration().messagesWrapper.cannotExecuteThisActionOnYourself));
    }

    if (!targetUser.isOnline()) {
      throw new BladeExitMessage(ChatHelper.colored(
          this.plugin.getPlatformConfiguration().messagesWrapper.playerIsOffline.replace(
              "{PLAYER_NAME}", targetUser.getNickname())));
    }

    final PlatformUser playerUser = this.plugin.getPlatformUserFactory()
        .findUserByUniqueId(player.getUniqueId()).orElseThrow(() -> new BladeExitMessage(
            this.plugin.getPlatformConfiguration().messagesWrapper.playerNotFound.replace(
                "{PLAYER_NAME}", player.getName())));
    this.sendPrivateMessage(player, playerUser, targetUser, message);
  }

  private void sendPrivateMessage(final Player player, final PlatformUser playerUser,
      final PlatformUser targetUser, final String message) {
    if (!targetUser.getChatSettings().isPrivateMessages()) {
      throw new BladeExitMessage(ChatHelper.colored(
          this.plugin.getPlatformConfiguration().messagesWrapper.privateMessageTargetPlayerHasDisabledPMS));
    }

    if (targetUser.isIgnored(playerUser.getUniqueId())) {
      throw new BladeExitMessage(ChatHelper.colored(
          this.plugin.getPlatformConfiguration().messagesWrapper.privateMessageTargetPlayerIsIgnoringYou));
    }

    playerUser.setLastPrivateMessage(targetUser.getUniqueId());
    targetUser.setLastPrivateMessage(playerUser.getUniqueId());
    targetUser.sendMessage(
        this.plugin.getPlatformConfiguration().messagesWrapper.privateMessageFormatTarget.replace(
            "{PLAYER_NAME}", playerUser.getNickname()).replace("{MESSAGE}", message));
    ChatHelper.sendMessage(player,
        this.plugin.getPlatformConfiguration().messagesWrapper.privateMessageFormatPlayer.replace(
            "{PLAYER_NAME}", targetUser.getNickname()).replace("{MESSAGE}", message));
    this.plugin.getRedisAdapter().sendPacket(
        new PlatformUserLastPrivateMessageUpdatePacket(playerUser.getUniqueId(),
            targetUser.getUniqueId()), "rhc_master_controller", "rhc_platform");
  }
}
