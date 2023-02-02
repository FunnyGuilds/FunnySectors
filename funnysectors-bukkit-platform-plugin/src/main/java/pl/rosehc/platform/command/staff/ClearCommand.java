package pl.rosehc.platform.command.staff;

import java.util.Collections;
import java.util.Objects;
import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Flag;
import me.vaperion.blade.annotation.Name;
import me.vaperion.blade.annotation.Optional;
import me.vaperion.blade.annotation.Permission;
import me.vaperion.blade.annotation.Sender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.controller.packet.platform.user.PlatformUserMessagePacket;
import pl.rosehc.platform.PlatformPlugin;
import pl.rosehc.platform.packet.player.PlayerClearInventoryPacket;
import pl.rosehc.sectors.sector.user.SectorUser;

public final class ClearCommand {

  private final PlatformPlugin plugin;

  public ClearCommand(final PlatformPlugin plugin) {
    this.plugin = plugin;
  }

  @Permission("platform-command-clear")
  @Command(value = {"clear",
      "ci"}, description = "Czyści inventory podanego gracza (lub administratora)")
  public void handleClear(final @Sender Player player,
      final @Flag(value = 'e', description = "Czy wyczyścić enderchesta?") @SuppressWarnings("SpellCheckingInspection") boolean enderchest,
      final @Name("target") @Optional SectorUser targetUser) {
    if (Objects.isNull(targetUser)) {
      player.getInventory().clear();
      player.getInventory().setArmorContents(new ItemStack[4]);
      if (enderchest) {
        player.getEnderChest().clear();
      }

      ChatHelper.sendMessage(player,
          this.plugin.getPlatformConfiguration().messagesWrapper.yourInventoryHasBeenCleared);
      return;
    }

    ChatHelper.sendMessage(player,
        this.plugin.getPlatformConfiguration().messagesWrapper.targetInventoryHasBeenClearedSender.replace(
            "{PLAYER_NAME}", targetUser.getNickname()));
    this.plugin.getRedisAdapter()
        .sendPacket(new PlayerClearInventoryPacket(targetUser.getUniqueId(), enderchest),
            "rhc_platform_" + targetUser.getSector().getName());
    this.plugin.getRedisAdapter().sendPacket(
        new PlatformUserMessagePacket(Collections.singletonList(targetUser.getUniqueId()),
            this.plugin.getPlatformConfiguration().messagesWrapper.targetInventoryHasBeenClearedReceiver.replace(
                "{PLAYER_NAME}", player.getName())),
        "rhc_platform_" + targetUser.getSector().getName());
  }
}
