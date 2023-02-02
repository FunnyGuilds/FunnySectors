package pl.rosehc.platform.command.staff;

import java.util.ArrayList;
import java.util.Collections;
import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Name;
import me.vaperion.blade.annotation.Permission;
import me.vaperion.blade.annotation.Sender;
import org.bukkit.entity.Player;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.controller.packet.platform.PlatformAlertMessagePacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserMessagePacket;
import pl.rosehc.platform.PlatformPlugin;
import pl.rosehc.platform.packet.player.PlayerGiveMagicCasePacket;
import pl.rosehc.sectors.sector.user.SectorUser;

public final class MagicCaseCommand {

  private final PlatformPlugin plugin;

  public MagicCaseCommand(final PlatformPlugin plugin) {
    this.plugin = plugin;
  }

  @Permission("platform-command-magiccase")
  @Command(value = {"magiccase player",
      "case player"}, description = "Rozdaje case dla danego gracza.")
  public void handleMagicCasePlayer(final @Sender Player player,
      final @Name("nickname") SectorUser user, final @Name("amount") int amount) {
    ChatHelper.sendMessage(player,
        this.plugin.getPlatformConfiguration().messagesWrapper.magicCaseGivenSender.replace(
            "{PLAYER_NAME}", user.getNickname()).replace("{AMOUNT}", String.valueOf(amount)));
    this.plugin.getRedisAdapter()
        .sendPacket(new PlayerGiveMagicCasePacket(user.getUniqueId(), amount),
            "rhc_platform_" + user.getSector().getName());
    this.plugin.getRedisAdapter().sendPacket(new PlatformUserMessagePacket(
        new ArrayList<>(Collections.singletonList(user.getUniqueId())),
        this.plugin.getPlatformConfiguration().messagesWrapper.magicCaseGivenReceiver.replace(
            "{PLAYER_NAME}", player.getName()).replace("{AMOUNT}", String.valueOf(amount))));
  }

  @Permission("platform-command-magiccase")
  @Command(value = {"magiccase all", "case all"}, description = "Rozdaje case dla każdego gracza.")
  public void handleMagicCaseAll(final @Sender Player player, final @Name("amount") int amount) {
    this.plugin.getRedisAdapter().sendPacket(new PlayerGiveMagicCasePacket(amount), "rhc_platform");
    this.plugin.getRedisAdapter().sendPacket(new PlatformAlertMessagePacket(
            this.plugin.getPlatformConfiguration().messagesWrapper.magicCaseGivenAll.replace("{AMOUNT}",
                String.valueOf(amount)).replace("{PLAYER_NAME}", player.getName()), false),
        "rhc_platform");
  }

  @Permission("platform-command-magiccase")
  @Command(value = {"magiccase", "case"}, description = "Wyświetla użycie tej oto komendy.")
  public void handleMagicCaseUsage(final @Sender Player player) {
    ChatHelper.sendMessage(player,
        this.plugin.getPlatformConfiguration().messagesWrapper.magicCaseCommandUsage);
  }
}
