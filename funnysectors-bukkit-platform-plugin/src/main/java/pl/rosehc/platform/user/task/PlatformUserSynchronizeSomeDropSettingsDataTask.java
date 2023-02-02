package pl.rosehc.platform.user.task;

import org.bukkit.entity.Player;
import pl.rosehc.controller.packet.platform.user.PlatformUserSynchronizeSomeDropSettingsDataPacket;
import pl.rosehc.platform.PlatformPlugin;
import pl.rosehc.platform.user.subdata.PlatformUserDropSettings;

public final class PlatformUserSynchronizeSomeDropSettingsDataTask implements Runnable {

  private final PlatformPlugin plugin;

  public PlatformUserSynchronizeSomeDropSettingsDataTask(final PlatformPlugin plugin) {
    this.plugin = plugin;
    this.plugin.getServer().getScheduler()
        .runTaskTimerAsynchronously(this.plugin, this, 1200L, 1200L);
  }

  @Override
  public void run() {
    for (final Player player : this.plugin.getServer().getOnlinePlayers()) {
      this.plugin.getPlatformUserFactory().findUserByUniqueId(player.getUniqueId())
          .ifPresent(user -> {
            final PlatformUserDropSettings dropSettings = user.getDropSettings();
            this.plugin.getRedisAdapter().sendPacket(
                new PlatformUserSynchronizeSomeDropSettingsDataPacket(user.getUniqueId(),
                    dropSettings.getTurboDropMultiplier(), dropSettings.isCobbleStone(),
                    dropSettings.getTurboDropTime(), dropSettings.getCurrentXP(),
                    dropSettings.getNeededXP(), dropSettings.getLevel()), "rhc_master_controller",
                "rhc_platform");
          });
    }
  }
}
