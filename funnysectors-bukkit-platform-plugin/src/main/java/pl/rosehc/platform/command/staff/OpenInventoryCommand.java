package pl.rosehc.platform.command.staff;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Flag;
import me.vaperion.blade.annotation.Name;
import me.vaperion.blade.annotation.Permission;
import me.vaperion.blade.annotation.Sender;
import org.bukkit.entity.Player;

public final class OpenInventoryCommand {

  @Permission("platform-command-openinventory")
  @Command(value = {"openinventory", "oi", "openinv",
      "invsee"}, description = "Otwiera podgląd inventory (lub enderchesta) podanego gracza")
  public void handleOpenInventory(final @Sender Player player,
      final @Flag(value = 'e', description = "Czy otworzyć podgląd enderchesta?") @SuppressWarnings("SpellCheckingInspection") boolean enderchest,
      final @Name("player") Player targetPlayer) {
    player.openInventory(enderchest ? targetPlayer.getEnderChest() : targetPlayer.getInventory());
  }
}
