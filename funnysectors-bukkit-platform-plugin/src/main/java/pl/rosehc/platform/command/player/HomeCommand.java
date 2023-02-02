package pl.rosehc.platform.command.player;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Sender;
import org.bukkit.entity.Player;
import pl.rosehc.platform.inventory.player.other.PlayerHomeInventory;

public final class HomeCommand {

  @Command(value = {"home", "sethome", "dom", "domki",
      "ustawdom"}, description = "Otwiera GUI od domków podanego gracza.")
  public void handleChannel(final @Sender Player player) {
    final PlayerHomeInventory inventory = new PlayerHomeInventory(player);
    inventory.open();
  }
}
