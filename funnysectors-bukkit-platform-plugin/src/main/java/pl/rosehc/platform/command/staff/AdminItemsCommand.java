package pl.rosehc.platform.command.staff;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Permission;
import me.vaperion.blade.annotation.Sender;
import org.bukkit.entity.Player;
import pl.rosehc.platform.inventory.player.other.PlayerAdminItemsInventory;

public final class AdminItemsCommand {

  @Permission("platform-command-adminitems")
  @Command(value = {"adminitems", "aitems", "adminitemy",
      "aitemy"}, description = "Otwiera GUI z itemami dla administratora")
  public void handleAdminItems(final @Sender Player player) {
    final PlayerAdminItemsInventory inventory = new PlayerAdminItemsInventory(player);
    inventory.open();
  }
}
