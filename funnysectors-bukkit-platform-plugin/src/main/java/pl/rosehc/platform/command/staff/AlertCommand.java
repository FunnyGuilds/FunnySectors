package pl.rosehc.platform.command.staff;

import me.vaperion.blade.annotation.Combined;
import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Flag;
import me.vaperion.blade.annotation.Name;
import me.vaperion.blade.annotation.Permission;
import me.vaperion.blade.annotation.Sender;
import org.bukkit.command.CommandSender;
import pl.rosehc.controller.packet.platform.PlatformAlertMessagePacket;
import pl.rosehc.platform.PlatformPlugin;

public final class AlertCommand {

  private final PlatformPlugin plugin;

  public AlertCommand(final PlatformPlugin plugin) {
    this.plugin = plugin;
  }

  @Permission("platform-command-alert")
  @Command(value = {"alert", "oglos"}, description = "Ogłasza wiadomość na serwerze")
  public void handleAlert(final @Sender CommandSender sender,
      final @Flag(value = 'c', description = "Czy wiadomość ma zostać wysłana na chacie?") boolean isChat,
      final @Name("message") @Combined String message) {
    this.plugin.getRedisAdapter()
        .sendPacket(new PlatformAlertMessagePacket(message, !isChat), "rhc_platform");
  }
}
