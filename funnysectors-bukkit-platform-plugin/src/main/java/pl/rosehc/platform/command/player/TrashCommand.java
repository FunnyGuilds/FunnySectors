package pl.rosehc.platform.command.player;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Sender;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class TrashCommand {

  @Command(value = {"kosz", "smietnik"}, description = "Otwiera kosz dla danego gracza")
  public void handleTrash(final @Sender Player player) {
    player.openInventory(Bukkit.createInventory(player, 54, ChatColor.RED + "Śmietnik"));
  }
}
