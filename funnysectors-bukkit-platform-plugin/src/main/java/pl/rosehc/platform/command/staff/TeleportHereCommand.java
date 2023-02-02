package pl.rosehc.platform.command.staff;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Name;
import me.vaperion.blade.annotation.Permission;
import me.vaperion.blade.annotation.Sender;
import org.bukkit.entity.Player;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.platform.PlatformPlugin;
import pl.rosehc.platform.packet.player.PlayerSelfTeleportPacket;
import pl.rosehc.sectors.sector.user.SectorUser;

public final class TeleportHereCommand {

  private final PlatformPlugin plugin;

  public TeleportHereCommand(final PlatformPlugin plugin) {
    this.plugin = plugin;
  }

  @Permission("platform-command-teleport")
  @Command(value = {"teleporthere", "tphere", "stp",
      "s"}, description = "Wykonuje teleportację danego gracza do Ciebie")
  public void handleTeleport(final @Sender Player player, final @Name("player") SectorUser user) {
    this.plugin.getRedisAdapter()
        .sendPacket(new PlayerSelfTeleportPacket(player.getUniqueId(), user.getUniqueId(), false),
            "rhc_platform_" + user.getSector().getName());
    ChatHelper.sendMessage(player,
        this.plugin.getPlatformConfiguration().messagesWrapper.successfullySelfTeleportedThePlayer.replace(
            "{PLAYER_NAME}", user.getNickname()));
  }
}
