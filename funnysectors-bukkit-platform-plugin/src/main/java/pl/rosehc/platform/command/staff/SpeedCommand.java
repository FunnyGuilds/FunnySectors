package pl.rosehc.platform.command.staff;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Name;
import me.vaperion.blade.annotation.Permission;
import me.vaperion.blade.annotation.Range;
import me.vaperion.blade.annotation.Sender;
import org.bukkit.entity.Player;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.platform.PlatformPlugin;

public final class SpeedCommand {

  private final PlatformPlugin plugin;

  public SpeedCommand(final PlatformPlugin plugin) {
    this.plugin = plugin;
  }

  @Permission("platform-command-speed")
  @Command(value = "speed", description = "Zmienia szybkośc latania lub chodzenia dla administratora")
  public void handleSpeed(final @Sender Player player,
      final @Name("speed") @Range(min = 1D, max = 10D) double speed) {
    final float minecraftSpeed = (float) (speed / 10F);
    if (player.isFlying()) {
      player.setFlySpeed(minecraftSpeed);
      ChatHelper.sendMessage(player,
          this.plugin.getPlatformConfiguration().messagesWrapper.flightSpeedSuccessfullyChanged.replace(
              "{SPEED}", String.valueOf(Math.round(speed))));
    } else {
      player.setWalkSpeed(minecraftSpeed);
      ChatHelper.sendMessage(player,
          this.plugin.getPlatformConfiguration().messagesWrapper.walkingSpeedSuccessfullyChanged.replace(
              "{SPEED}", String.valueOf(Math.round(speed))));
    }
  }
}
