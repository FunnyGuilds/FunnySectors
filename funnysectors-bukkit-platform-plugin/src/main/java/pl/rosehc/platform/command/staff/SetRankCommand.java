package pl.rosehc.platform.command.staff;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Name;
import me.vaperion.blade.annotation.Optional;
import me.vaperion.blade.annotation.Permission;
import me.vaperion.blade.annotation.Sender;
import org.bukkit.command.CommandSender;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.adapter.helper.TimeHelper;
import pl.rosehc.controller.packet.platform.user.PlatformUserRankUpdatePacket;
import pl.rosehc.platform.PlatformPlugin;
import pl.rosehc.platform.rank.Rank;
import pl.rosehc.platform.rank.RankEntry;
import pl.rosehc.platform.user.PlatformUser;

public final class SetRankCommand {

  private final PlatformPlugin plugin;

  public SetRankCommand(final PlatformPlugin plugin) {
    this.plugin = plugin;
  }

  @Permission("platform-command-setrank")
  @Command(value = {"setrank", "rank"}, description = "Ustawia rangę danemu graczu na podany czas.")
  public void handleSetRank(final @Sender CommandSender sender,
      final @Name("player") PlatformUser user, final @Name("rank") Rank rank,
      final @Name("time") @Optional String time) {
    final long parsedTime =
        time != null && !time.equals("null") ? TimeHelper.timeFromString(time) : 0L;
    user.setRank(RankEntry.create(parsedTime != 0L ? user.getRank().getCurrentRank() : null, rank,
        parsedTime));
    ChatHelper.sendMessage(sender,
        this.plugin.getPlatformConfiguration().messagesWrapper.rankSuccessfullySetSender.replace(
                "{RANK_NAME}", rank.getName()).replace("{PLAYER_NAME}", user.getNickname())
            .replace("{TIME}", parsedTime != 0L ? TimeHelper.timeToString(parsedTime) : "PERM"));
    user.sendMessage(
        this.plugin.getPlatformConfiguration().messagesWrapper.rankSuccessfullySetReceiver.replace(
                "{RANK_NAME}", rank.getName()).replace("{PLAYER_NAME}", sender.getName())
            .replace("{TIME}", parsedTime != 0L ? TimeHelper.timeToString(parsedTime) : "PERM"));
    this.plugin.getRedisAdapter().sendPacket(new PlatformUserRankUpdatePacket(user.getUniqueId(),
            user.getRank().getPreviousRank() != null ? user.getRank().getPreviousRank().getName()
                : null, user.getRank().getCurrentRank().getName(), parsedTime), "rhc_master_controller",
        "rhc_platform");
  }
}
