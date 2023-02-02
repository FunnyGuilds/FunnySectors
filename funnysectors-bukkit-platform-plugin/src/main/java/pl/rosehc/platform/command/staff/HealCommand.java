package pl.rosehc.platform.command.staff;

import java.util.Collections;
import java.util.Objects;
import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Name;
import me.vaperion.blade.annotation.Optional;
import me.vaperion.blade.annotation.Permission;
import me.vaperion.blade.annotation.Sender;
import org.bukkit.entity.Player;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.controller.packet.platform.user.PlatformUserMessagePacket;
import pl.rosehc.platform.PlatformPlugin;
import pl.rosehc.platform.packet.player.PlayerHealPacket;
import pl.rosehc.sectors.sector.user.SectorUser;

public final class HealCommand {

  private final PlatformPlugin plugin;

  public HealCommand(final PlatformPlugin plugin) {
    this.plugin = plugin;
  }

  @Permission("platform-command-heal")
  @Command(value = "heal", description = "Leczy podanego gracza (lub administratora)")
  public void handleClear(final @Sender Player player,
      final @Name("target") @Optional SectorUser targetUser) {
    if (Objects.isNull(targetUser)) {
      player.setHealth(player.getMaxHealth());
      player.setFoodLevel(20);
      player.setFireTicks(0);
      player.getActivePotionEffects()
          .forEach(effect -> player.removePotionEffect(effect.getType()));
      ChatHelper.sendMessage(player,
          this.plugin.getPlatformConfiguration().messagesWrapper.youHaveBeenHealed);
      return;
    }

    ChatHelper.sendMessage(player,
        this.plugin.getPlatformConfiguration().messagesWrapper.targetHaveBeenHealedSender.replace(
            "{PLAYER_NAME}", targetUser.getNickname()));
    this.plugin.getRedisAdapter().sendPacket(new PlayerHealPacket(targetUser.getUniqueId()),
        "rhc_platform_" + targetUser.getSector().getName());
    this.plugin.getRedisAdapter().sendPacket(
        new PlatformUserMessagePacket(Collections.singletonList(targetUser.getUniqueId()),
            this.plugin.getPlatformConfiguration().messagesWrapper.targetHaveBeenHealedReceiver.replace(
                "{PLAYER_NAME}", player.getName())),
        "rhc_platform_" + targetUser.getSector().getName());
  }
}
