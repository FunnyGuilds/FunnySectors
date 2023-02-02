package pl.rosehc.platform.command.player;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Sender;
import org.bukkit.entity.Player;
import pl.rosehc.platform.inventory.player.kit.PlayerKitListInventory;

public final class KitCommand {

  @Command(value = {"kit", "kity", "zestawy",
      "zestaw"}, description = "Otwiera GUI od kitów dostępnych na serwerze.")
  public void handleKit(final @Sender Player player) {
    final PlayerKitListInventory inventory = new PlayerKitListInventory(player);
    inventory.open();
  }
}
