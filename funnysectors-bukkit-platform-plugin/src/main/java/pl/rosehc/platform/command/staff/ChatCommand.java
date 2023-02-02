package pl.rosehc.platform.command.staff;

import java.util.Arrays;
import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Permission;
import me.vaperion.blade.annotation.Sender;
import me.vaperion.blade.exception.BladeExitMessage;
import org.bukkit.command.CommandSender;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.controller.packet.platform.PlatformAlertMessagePacket;
import pl.rosehc.controller.packet.platform.PlatformChatStateChangePacket;
import pl.rosehc.controller.wrapper.platform.PlatformChatStatusType;
import pl.rosehc.platform.PlatformPlugin;

public final class ChatCommand {

  private static final char[] CHARS = new char[7680];

  static {
    Arrays.fill(CHARS, ' ');
  }

  private final PlatformPlugin plugin;

  public ChatCommand(final PlatformPlugin plugin) {
    this.plugin = plugin;
  }

  @Permission("platform-command-chat")
  @Command(value = {"chat clear", "chat cc"}, description = "Czyści cały chat")
  public void handleChatClear(final @Sender CommandSender sender) {
    this.plugin.getRedisAdapter().sendPacket(new PlatformAlertMessagePacket(new String(CHARS) + "\n"
        + this.plugin.getPlatformConfiguration().messagesWrapper.chatHasBeenCleared.replace(
        "{PLAYER_NAME}", sender.getName()), false), "rhc_platform");
  }

  @Permission("platform-command-chat")
  @Command(value = "chat", description = "Wyświetla użycie komendy od chatu")
  public void handleChatUsage(final @Sender CommandSender sender) {
    ChatHelper.sendMessage(sender,
        this.plugin.getPlatformConfiguration().messagesWrapper.chatCommandUsage);
  }

  @Permission("platform-command-chat")
  @Command(value = {"chat enable", "chat on"}, description = "Włącza cały chat")
  public void handleChatEnable(final @Sender CommandSender sender) {
    this.changeStatus(PlatformChatStatusType.ENABLED, sender);
  }

  @Permission("platform-command-chat")
  @Command(value = {"chat disable", "chat off"}, description = "Wyłącza cały chat")
  public void handleChatDisable(final @Sender CommandSender sender) {
    this.changeStatus(PlatformChatStatusType.DISABLED, sender);
  }

  @Permission("platform-command-chat")
  @Command(value = {"chat enablepremium",
      "chat premium"}, description = "Włącza cały chat, ale tylko dla rang premium")
  public void handleChatEnablePremium(final @Sender CommandSender sender) {
    this.changeStatus(PlatformChatStatusType.PREMIUM, sender);
  }

  private void changeStatus(final PlatformChatStatusType type, final CommandSender sender) {
    if (this.plugin.getPlatformConfiguration().chatStatusType == type) {
      throw new BladeExitMessage(ChatHelper.colored(type.getAlreadyEnabledMessage()));
    }

    ChatHelper.sendMessage(sender, type.getSuccessfullyEnabledMessageSender());
    this.plugin.getRedisAdapter()
        .sendPacket(new PlatformChatStateChangePacket(type), "rhc_master_controller",
            "rhc_platform");
    this.plugin.getRedisAdapter().sendPacket(new PlatformAlertMessagePacket(
        type.getSuccessfullyEnabledMessageGlobal().replace("{PLAYER_NAME}", sender.getName()),
        false), "rhc_platform");
  }
}
