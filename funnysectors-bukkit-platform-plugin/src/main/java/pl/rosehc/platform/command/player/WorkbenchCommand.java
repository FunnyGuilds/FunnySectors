package pl.rosehc.platform.command.player;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Permission;
import me.vaperion.blade.annotation.Sender;
import org.bukkit.entity.Player;
import pl.rosehc.platform.PlatformPlugin;

/**
 * @author stevimeister on 30/01/2022
 **/
public final class WorkbenchCommand {

  private final PlatformPlugin plugin;

  public WorkbenchCommand(final PlatformPlugin plugin) {
    this.plugin = plugin;
  }

  @Permission("platform-command-workbench")
  @Command(value = {"workbench", "wb"}, description = "Otwiera przenośny crafting")
  public void handleWorkbench(final @Sender Player player) {
    player.openWorkbench(null, true);
  }
}
