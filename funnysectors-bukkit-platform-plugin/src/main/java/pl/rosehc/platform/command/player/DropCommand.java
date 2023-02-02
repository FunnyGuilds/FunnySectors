package pl.rosehc.platform.command.player;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Sender;
import org.bukkit.entity.Player;
import pl.rosehc.platform.inventory.player.drop.PlayerDropMainInventory;

public final class DropCommand {

  @Command(value = {"drop", "stone", "casedrop",
      "cobblexdrop"}, description = "Otwiera GUI od dropu.")
  public void handleDrop(final @Sender Player player) {
    final PlayerDropMainInventory inventory = new PlayerDropMainInventory(player);
    inventory.open();
  }
}
