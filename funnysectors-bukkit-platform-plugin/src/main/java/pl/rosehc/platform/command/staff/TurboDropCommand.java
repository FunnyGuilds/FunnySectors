package pl.rosehc.platform.command.staff;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Name;
import me.vaperion.blade.annotation.Permission;
import me.vaperion.blade.annotation.Range;
import me.vaperion.blade.annotation.Sender;
import org.bukkit.command.CommandSender;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.adapter.helper.TimeHelper;
import pl.rosehc.controller.packet.platform.PlatformDropSettingsUpdateTurboDropPacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserSynchronizeSomeDropSettingsDataPacket;
import pl.rosehc.platform.PlatformPlugin;
import pl.rosehc.platform.user.PlatformUser;
import pl.rosehc.platform.user.subdata.PlatformUserDropSettings;

public final class TurboDropCommand {

  private final PlatformPlugin plugin;

  public TurboDropCommand(final PlatformPlugin plugin) {
    this.plugin = plugin;
  }

  @Permission("platform-command-turbodrop")
  @Command(value = {"turbodrop player",
      "turbo player"}, description = "Nadaje turbodrop dla danego gracza")
  public void handleTurboDropPlayer(final @Sender CommandSender sender,
      final @Name("nickname") PlatformUser user,
      final @Name("multiplier") @Range(min = 1.2D, max = 7.5D) double multiplier,
      final @Name("time") String time) {
    final PlatformUserDropSettings dropSettings = user.getDropSettings();
    final long parsedTime = TimeHelper.timeFromString(time);
    dropSettings.setTurboDropTime(System.currentTimeMillis() + parsedTime);
    dropSettings.setTurboDropMultiplier(multiplier);
    user.sendMessage(
        this.plugin.getPlatformConfiguration().messagesWrapper.turboDropForPlayerHasBeenActivatedReceiver.replace(
                "{PLAYER_NAME}", sender.getName())
            .replace("{TIME}", TimeHelper.timeToString(parsedTime)));
    ChatHelper.sendMessage(sender,
        this.plugin.getPlatformConfiguration().messagesWrapper.turboDropForPlayerHasBeenActivatedSender.replace(
                "{PLAYER_NAME}", user.getNickname())
            .replace("{TIME}", TimeHelper.timeToString(parsedTime)));
    this.plugin.getRedisAdapter().sendPacket(
        new PlatformUserSynchronizeSomeDropSettingsDataPacket(user.getUniqueId(),
            dropSettings.getTurboDropMultiplier(), dropSettings.isCobbleStone(),
            dropSettings.getTurboDropTime(), dropSettings.getCurrentXP(),
            dropSettings.getNeededXP(), dropSettings.getLevel()), "rhc_master_controller",
        "rhc_platform");
  }

  @Permission("platform-command-turbodrop")
  @Command(value = {"turbodrop global",
      "turbo global"}, description = "Nadaje turbodrop dla danego gracza")
  public void handleTurboDropGlobal(final @Sender CommandSender sender,
      final @Name("multiplier") @Range(min = 1.2D, max = 7.5D) double multiplier,
      final @Name("time") String time) {
    final long parsedTime = TimeHelper.timeFromString(time);
    ChatHelper.sendMessage(sender,
        this.plugin.getPlatformConfiguration().messagesWrapper.turboDropGlobalHasBeenActivated.replace(
            "{TIME}", TimeHelper.timeToString(parsedTime)));
    this.plugin.getRedisAdapter().sendPacket(
        new PlatformDropSettingsUpdateTurboDropPacket(multiplier,
            System.currentTimeMillis() + parsedTime), "rhc_master_controller", "rhc_platform");
  }

  @Permission("platform-command-turbodrop")
  @Command(value = {"turbodrop", "turbo"}, description = "Wyświetla użycie komendy od turbodropa")
  public void handleTurboDropUsage(final @Sender CommandSender sender) {
    ChatHelper.sendMessage(sender,
        this.plugin.getPlatformConfiguration().messagesWrapper.turboDropCommandUsage);
  }
}
