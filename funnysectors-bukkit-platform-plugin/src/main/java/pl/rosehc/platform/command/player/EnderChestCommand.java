package pl.rosehc.platform.command.player;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Permission;
import me.vaperion.blade.annotation.Sender;
import org.bukkit.entity.Player;
import pl.rosehc.platform.PlatformPlugin;

/**
 * @author stevimeister on 30/01/2022
 **/
public final class EnderChestCommand {

  private final PlatformPlugin plugin;

  public EnderChestCommand(final PlatformPlugin plugin) {
    this.plugin = plugin;
  }

  @Permission("platform-command-workbench")
  @Command(value = {"enderchest", "ec"}, description = "Otwiera przenośny enderchest")
  public void handleEnderChest(final @Sender Player player) {
    player.openInventory(player.getEnderChest());
  }
}
