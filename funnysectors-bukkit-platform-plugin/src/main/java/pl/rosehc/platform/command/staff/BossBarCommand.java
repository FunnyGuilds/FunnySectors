package pl.rosehc.platform.command.staff;

import java.util.concurrent.TimeUnit;
import me.vaperion.blade.annotation.Combined;
import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Name;
import me.vaperion.blade.annotation.Permission;
import me.vaperion.blade.annotation.Sender;
import org.bukkit.command.CommandSender;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.adapter.helper.TimeHelper;
import pl.rosehc.controller.packet.platform.PlatformSpecialBossBarUpdatePacket;
import pl.rosehc.controller.wrapper.global.BarColorWrapper;
import pl.rosehc.controller.wrapper.global.BarStyleWrapper;
import pl.rosehc.platform.PlatformConfiguration.SpecialBossBarWrapper;
import pl.rosehc.platform.PlatformPlugin;

public final class BossBarCommand {

  private final PlatformPlugin plugin;

  public BossBarCommand(final PlatformPlugin plugin) {
    this.plugin = plugin;
  }

  @Permission("platform-command-bossbar")
  @Command(value = {"bossbar update", "specialbar update",
      "bar update"}, description = "Modyfikuje globalnego bossbara graczy")
  public void handleBossBarUpdate(final @Sender CommandSender sender,
      final @Name("time (0 = perm)") String time,
      final @Name("color") BarColorWrapper barColorWrapper,
      final @Name("style") BarStyleWrapper barStyleWrapper,
      final @Name("title") @Combined String title) {
    final SpecialBossBarWrapper specialBossBarWrapper = new SpecialBossBarWrapper();
    specialBossBarWrapper.title = title;
    specialBossBarWrapper.barColorWrapper = barColorWrapper;
    specialBossBarWrapper.barStyleWrapper = barStyleWrapper;
    specialBossBarWrapper.expiryTime =
        time != null && !time.equals("null") && !time.equals("0") ? System.currentTimeMillis()
            + TimeHelper.timeFromString(time) : 0L;
    specialBossBarWrapper.expiryMaxBars =
        specialBossBarWrapper.expiryTime != 0L ? TimeUnit.MILLISECONDS.toSeconds(
            specialBossBarWrapper.expiryTime - System.currentTimeMillis()) : 0F;
    ChatHelper.sendMessage(sender,
        this.plugin.getPlatformConfiguration().messagesWrapper.bossBarSuccessfullyUpdated);
    this.plugin.getPlatformConfiguration().specialBossBarWrapper = specialBossBarWrapper;
    this.plugin.getRedisAdapter()
        .sendPacket(new PlatformSpecialBossBarUpdatePacket(specialBossBarWrapper),
            "rhc_master_controller", "rhc_platform");
  }

  @Permission("platform-command-bossbar")
  @Command(value = {"bossbar remove", "specialbar remove",
      "bar remove"}, description = "Usuwa globalnego bossbara graczy")
  public void handleBossBarRemove(final @Sender CommandSender sender) {
    ChatHelper.sendMessage(sender,
        this.plugin.getPlatformConfiguration().messagesWrapper.bossBarSuccessfullyRemoved);
    this.plugin.getPlatformConfiguration().specialBossBarWrapper = null;
    this.plugin.getRedisAdapter()
        .sendPacket(new PlatformSpecialBossBarUpdatePacket(null), "rhc_master_controller",
            "rhc_platform");
  }

  @Permission("platform-command-bossbar")
  @Command(value = {"bossbar", "specialbar", "bar"}, description = "Wysyła użycie bossbarów")
  public void handleBossBarUsage(final @Sender CommandSender sender) {
    ChatHelper.sendMessage(sender,
        this.plugin.getPlatformConfiguration().messagesWrapper.bossBarUsage);
  }
}
