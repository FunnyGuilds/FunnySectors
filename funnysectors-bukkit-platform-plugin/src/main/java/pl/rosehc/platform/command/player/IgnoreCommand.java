package pl.rosehc.platform.command.player;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Name;
import me.vaperion.blade.annotation.Sender;
import me.vaperion.blade.exception.BladeExitMessage;
import org.bukkit.entity.Player;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.controller.packet.platform.user.PlatformUserIgnoredPlayerUpdatePacket;
import pl.rosehc.platform.PlatformPlugin;
import pl.rosehc.platform.user.PlatformUser;

public final class IgnoreCommand {

  private final PlatformPlugin plugin;

  public IgnoreCommand(final PlatformPlugin plugin) {
    this.plugin = plugin;
  }

  @Command(value = {"ignore",
      "ignoruj"}, description = "Ignoruje lub przestaje ignorować podanego gracza.")
  public void handleIgnore(final @Sender Player player,
      final @Name("target") PlatformUser targetUser) {
    final PlatformUser playerUser = this.plugin.getPlatformUserFactory()
        .findUserByUniqueId(player.getUniqueId()).orElseThrow(() -> new BladeExitMessage(
            ChatHelper.colored(
                this.plugin.getPlatformConfiguration().messagesWrapper.playerNotFound.replace(
                    "{PLAYER_NAME}", player.getName()))));
    final boolean ignoring = playerUser.updateIgnoredPlayer(targetUser.getUniqueId());
    ChatHelper.sendMessage(player,
        (ignoring ? this.plugin.getPlatformConfiguration().messagesWrapper.ignoreNowIgnoringPlayer
            : this.plugin.getPlatformConfiguration().messagesWrapper.ignoreNotIgnoringPlayer).replace(
            "{PLAYER_NAME}", targetUser.getNickname()));
    this.plugin.getRedisAdapter().sendPacket(
        new PlatformUserIgnoredPlayerUpdatePacket(playerUser.getUniqueId(),
            targetUser.getUniqueId(), ignoring), "rhc_master_controller", "rhc_platform");
  }
}
