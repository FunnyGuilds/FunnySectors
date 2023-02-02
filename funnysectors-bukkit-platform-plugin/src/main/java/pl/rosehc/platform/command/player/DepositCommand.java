package pl.rosehc.platform.command.player;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Sender;
import org.bukkit.entity.Player;
import pl.rosehc.platform.inventory.player.other.PlayerDepositInventory;

public final class DepositCommand {

  @Command(value = {"deposit", "depozyt", "schowek",
      "depo"}, description = "Otwiera GUI od depozytu gracza.")
  public void handleDeposit(final @Sender Player player) {
    final PlayerDepositInventory inventory = new PlayerDepositInventory(player);
    inventory.open();
  }
}
