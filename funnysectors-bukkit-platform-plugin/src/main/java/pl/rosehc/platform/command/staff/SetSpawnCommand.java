package pl.rosehc.platform.command.staff;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Permission;
import me.vaperion.blade.annotation.Sender;
import org.bukkit.entity.Player;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.controller.packet.platform.PlatformSetSpawnPacket;
import pl.rosehc.controller.wrapper.global.LocationWrapper;
import pl.rosehc.platform.PlatformPlugin;

public final class SetSpawnCommand {

  private final PlatformPlugin plugin;

  public SetSpawnCommand(final PlatformPlugin plugin) {
    this.plugin = plugin;
  }

  @Permission("platform-command-setspawn")
  @Command(value = "setspawn", description = "Ustawia nową lokację spawna")
  public void handleSetSpawn(final @Sender Player player) {
    this.plugin.getRedisAdapter()
        .sendPacket(new PlatformSetSpawnPacket(LocationWrapper.wrap(player.getLocation())),
            "rhc_master_controller", "rhc_platform");
    ChatHelper.sendMessage(player,
        this.plugin.getPlatformConfiguration().messagesWrapper.changedSpawnLocationSuccessfully);
  }
}
