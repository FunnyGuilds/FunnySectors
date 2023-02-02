package pl.rosehc.platform.command.staff;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Name;
import me.vaperion.blade.annotation.Permission;
import me.vaperion.blade.annotation.Sender;
import org.bukkit.entity.Player;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.adapter.helper.SerializeHelper;
import pl.rosehc.adapter.redis.callback.Callback;
import pl.rosehc.adapter.redis.callback.CallbackPacket;
import pl.rosehc.platform.PlatformPlugin;
import pl.rosehc.platform.packet.player.PlayerLocationRequestPacket;
import pl.rosehc.platform.packet.player.PlayerLocationResponsePacket;
import pl.rosehc.sectors.SectorsPlugin;
import pl.rosehc.sectors.sector.user.SectorUser;

public final class TeleportCommand {

  private final PlatformPlugin plugin;

  public TeleportCommand(final PlatformPlugin plugin) {
    this.plugin = plugin;
  }

  @Permission("platform-command-teleport")
  @Command(value = {"teleport", "tp"}, description = "Wykonuje teleportację do danego gracza")
  public void handleTeleport(final @Sender Player player, final @Name("player") SectorUser user) {
    this.plugin.getRedisAdapter().sendPacket(new PlayerLocationRequestPacket(user.getUniqueId(),
            SectorsPlugin.getInstance().getSectorFactory().getCurrentSector().getName()),
        new Callback() {

          @Override
          public void done(final CallbackPacket packet) {
            if (player.isOnline()) {
              ChatHelper.sendMessage(player,
                  TeleportCommand.this.plugin.getPlatformConfiguration().messagesWrapper.successfullyTeleportedToThePlayer.replace(
                      "{PLAYER_NAME}", user.getNickname()));
              TeleportCommand.this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin,
                  () -> player.teleport(SerializeHelper.deserializeLocation(
                      ((PlayerLocationResponsePacket) packet).getLocation())));
            }
          }

          @Override
          public void error(final String message) {
            if (player.isOnline()) {
              ChatHelper.sendMessage(player, message);
            }
          }
        }, "rhc_platform_" + user.getSector().getName());
  }
}
