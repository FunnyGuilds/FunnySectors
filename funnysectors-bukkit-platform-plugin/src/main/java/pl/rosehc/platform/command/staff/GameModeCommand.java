package pl.rosehc.platform.command.staff;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Name;
import me.vaperion.blade.annotation.Permission;
import me.vaperion.blade.annotation.Sender;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.platform.PlatformPlugin;

/**
 * @author stevimeister on 30/01/2022
 **/
public final class GameModeCommand {

  private final PlatformPlugin plugin;

  public GameModeCommand(final PlatformPlugin plugin) {
    this.plugin = plugin;
  }

  @Permission("platform-command-gamemode")
  @Command(value = {"gamemode", "gm"}, description = "Zmienia tryb gry")
  public void handleGameMode(final @Sender Player player, final @Name("mode") GameMode mode) {
    player.setGameMode(mode);
    ChatHelper.sendMessage(player,
        this.plugin.getPlatformConfiguration().messagesWrapper.gameModeSuccessfullyChanged.replace(
            "{MODE_NAME}", mode.name().toLowerCase()));
  }
}