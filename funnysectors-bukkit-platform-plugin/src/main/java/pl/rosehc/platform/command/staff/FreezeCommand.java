package pl.rosehc.platform.command.staff;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Permission;
import me.vaperion.blade.annotation.Sender;
import me.vaperion.blade.exception.BladeExitMessage;
import org.bukkit.command.CommandSender;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.controller.packet.platform.PlatformAlertMessagePacket;
import pl.rosehc.controller.packet.platform.PlatformSetFreezeStatePacket;
import pl.rosehc.platform.PlatformPlugin;

public final class FreezeCommand {

  private final PlatformPlugin plugin;

  public FreezeCommand(final PlatformPlugin plugin) {
    this.plugin = plugin;
  }

  @Permission("platform-command-freeze")
  @Command(value = "freeze", description = "Zamraża cały serwer")
  public void handleFreezeEnable(final @Sender CommandSender sender) {
    this.changeFreezeState(sender, true);
  }

  @Permission("platform-command-unfreeze")
  @Command(value = "unfreeze", description = "Odmraża cały serwer")
  public void handleFreezeDisable(final @Sender CommandSender sender) {
    this.changeFreezeState(sender, false);
  }

  private void changeFreezeState(final CommandSender sender, final boolean state) {
    if (this.plugin.getPlatformConfiguration().serverFreezeState == state) {
      throw new BladeExitMessage(ChatHelper.colored(
          state ? this.plugin.getPlatformConfiguration().messagesWrapper.serverIsAlreadyFrozen
              : this.plugin.getPlatformConfiguration().messagesWrapper.serverIsNotFrozen));
    }

    ChatHelper.sendMessage(sender, !state
        ? this.plugin.getPlatformConfiguration().messagesWrapper.successfullyUnfrozenTheServer
        : this.plugin.getPlatformConfiguration().messagesWrapper.successfullyFrozenTheServer);
    this.plugin.getRedisAdapter()
        .sendPacket(new PlatformSetFreezeStatePacket(state), "rhc_master_controller",
            "rhc_platform");
    if (!state) {
      this.plugin.getRedisAdapter().sendPacket(new PlatformAlertMessagePacket(
          this.plugin.getPlatformConfiguration().messagesWrapper.serverGotUnfrozenInfoTitle + "|"
              + this.plugin.getPlatformConfiguration().messagesWrapper.serverGotUnfrozenInfoSubTitle,
          true), "rhc_platform");
    }
  }
}
