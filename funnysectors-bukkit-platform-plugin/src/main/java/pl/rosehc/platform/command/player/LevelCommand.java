package pl.rosehc.platform.command.player;

import java.util.Objects;
import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Name;
import me.vaperion.blade.annotation.Optional;
import me.vaperion.blade.annotation.Sender;
import me.vaperion.blade.exception.BladeExitMessage;
import org.bukkit.entity.Player;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.platform.PlatformPlugin;
import pl.rosehc.platform.user.PlatformUser;
import pl.rosehc.platform.user.subdata.PlatformUserDropSettings;

public final class LevelCommand {

  private final PlatformPlugin plugin;

  public LevelCommand(final PlatformPlugin plugin) {
    this.plugin = plugin;
  }

  @Command(value = {"level", "lvl"}, description = "Wyświetla informacje o levelu danego gracza")
  public void handleLevel(final @Sender Player player,
      @Optional @Name("nickname") PlatformUser user) {
    if (Objects.isNull(user)) {
      user = this.plugin.getPlatformUserFactory().findUserByUniqueId(player.getUniqueId())
          .orElseThrow(() -> new BladeExitMessage(ChatHelper.colored(
              this.plugin.getPlatformConfiguration().messagesWrapper.playerNotFound.replace(
                  "{PLAYER_NAME}", player.getName()))));
    }

    final PlatformUserDropSettings dropSettings = user.getDropSettings();
    ChatHelper.sendMessage(player, (!player.getUniqueId().equals(user.getUniqueId())
        ? this.plugin.getPlatformConfiguration().messagesWrapper.userLevelInfoTarget.replace(
        "{PLAYER_NAME}", user.getNickname())
        : this.plugin.getPlatformConfiguration().messagesWrapper.userLevelInfoYourself).replace(
            "{LEVEL}", String.valueOf(dropSettings.getLevel()))
        .replace("{CURRENT_EXP}", String.valueOf(dropSettings.getCurrentXP()))
        .replace("{NEEDED_EXP}", String.valueOf(dropSettings.getNeededXP())));
  }
}
