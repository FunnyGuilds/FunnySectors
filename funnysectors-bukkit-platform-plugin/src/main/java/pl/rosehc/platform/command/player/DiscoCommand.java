package pl.rosehc.platform.command.player;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Permission;
import me.vaperion.blade.annotation.Sender;
import org.bukkit.entity.Player;
import pl.rosehc.platform.inventory.player.other.PlayerDiscoInventory;

public final class DiscoCommand {

  @Command(value = "disco", description = "Otwiera GUI od disco zbroi.")
  @Permission("platform-command-disco")
  public void handleDisco(final @Sender Player player) {
    final PlayerDiscoInventory inventory = new PlayerDiscoInventory(player);
    inventory.open();
  }
}
