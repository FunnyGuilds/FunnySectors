package pl.rosehc.platform.command.staff;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Name;
import me.vaperion.blade.annotation.Permission;
import me.vaperion.blade.annotation.Sender;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.platform.PlatformPlugin;
import pl.rosehc.sectors.sector.Sector;

public final class SectorCommand {

  private final PlatformPlugin plugin;

  public SectorCommand(final PlatformPlugin plugin) {
    this.plugin = plugin;
  }

  @Permission("platform-command-sector")
  @Command(value = {"sector", "sektor"}, description = "Wykonuje teleportację na środek sektora")
  public void handleSector(final @Sender Player player, final @Name("sector") Sector sector) {
    final Location location = new Location(sector.getWorld(), sector.getCenterX(), 0D,
        sector.getCenterZ());
    location.setY(
        location.getWorld().getHighestBlockYAt((int) location.getX(), (int) location.getZ()));
    ChatHelper.sendMessage(player,
        this.plugin.getPlatformConfiguration().messagesWrapper.successfullyTeleportedToTheSector.replace(
            "{SECTOR_NAME}", sector.getName()));
    player.teleport(location);
  }
}
