package pl.rosehc.platform.command.staff;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Permission;
import me.vaperion.blade.annotation.Sender;
import org.bukkit.entity.Player;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.platform.PlatformPlugin;

public final class FlightCommand {

  private final PlatformPlugin plugin;

  public FlightCommand(final PlatformPlugin plugin) {
    this.plugin = plugin;
  }

  @Permission("platform-command-flight")
  @Command(value = {"flight",
      "fly"}, description = "Włącza lub wyłącza tryb latania dla administratora")
  public void handleFlight(final @Sender Player player) {
    player.setAllowFlight(!player.getAllowFlight());
    ChatHelper.sendMessage(player, player.getAllowFlight()
        ? this.plugin.getPlatformConfiguration().messagesWrapper.flightEnabledInfo
        : this.plugin.getPlatformConfiguration().messagesWrapper.flightDisabledInfo);
  }
}
