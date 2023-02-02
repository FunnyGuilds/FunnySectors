package pl.rosehc.platform.command.player;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Sender;
import org.bukkit.entity.Player;
import pl.rosehc.platform.inventory.sector.SectorChannelInventory;

public final class ChannelCommand {

  @Command(value = {"channel", "ch"}, description = "Otwiera GUI od kanałów dostępnych na spawnie.")
  public void handleChannel(final @Sender Player player) {
    final SectorChannelInventory inventory = new SectorChannelInventory(player);
    inventory.open();
  }
}
