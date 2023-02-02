package pl.rosehc.platform.command.player;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Sender;
import org.bukkit.entity.Player;
import pl.rosehc.platform.PlatformPlugin;
import pl.rosehc.sectors.SectorsPlugin;
import pl.rosehc.sectors.sector.SectorType;

/**
 * @author stevimeister on 30/01/2022
 **/
public final class SpawnCommand {

  private final PlatformPlugin plugin;

  public SpawnCommand(final PlatformPlugin plugin) {
    this.plugin = plugin;
  }

  @Command(value = "spawn", description = "Teleportuje gracza na spawn")
  public void handleSpawn(final @Sender Player player) {
    this.plugin.getTimerTaskFactory()
        .addTimer(player, this.plugin.getPlatformConfiguration().spawnLocationWrapper.unwrap(),
            !SectorsPlugin.getInstance().getSectorFactory().getCurrentSector().getType()
                .equals(SectorType.SPAWN) ? 8 : 3);
  }
}
