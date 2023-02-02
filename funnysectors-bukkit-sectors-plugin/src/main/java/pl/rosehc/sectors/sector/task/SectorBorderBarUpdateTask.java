package pl.rosehc.sectors.sector.task;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.bossbar.BossBarBuilder;
import pl.rosehc.bossbar.BossBarPlugin;
import pl.rosehc.bossbar.user.UserBar;
import pl.rosehc.bossbar.user.UserBarConstants;
import pl.rosehc.bossbar.user.UserBarType;
import pl.rosehc.sectors.SectorsPlugin;
import pl.rosehc.sectors.helper.SectorHelper;

/**
 * @author stevimeister on 26/09/2021
 **/
public final class SectorBorderBarUpdateTask implements Runnable {

  private final SectorsPlugin plugin;

  public SectorBorderBarUpdateTask(final SectorsPlugin plugin) {
    this.plugin = plugin;
    this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(this.plugin, this, 10L, 10L);
  }

  @Override
  public void run() {
    this.plugin.getServer().getOnlinePlayers().forEach(this::sendNotification);
  }

  private void sendNotification(final Player player) {
    final UserBar userBar = BossBarPlugin.getInstance().getUserBarFactory().getUserBar(player);
    final boolean isNearSector = SectorHelper.isNearSector(player.getLocation(), 50);
    if (!isNearSector) {
      userBar.removeBossBar(UserBarType.SECTOR);
      return;
    }

    final String formattedDistance = String.valueOf(
        SectorHelper.getDistanceToNearestSector(player.getLocation()));
    if (userBar.hasBossBar(UserBarType.SECTOR)) {
      userBar.updateBossBar(UserBarType.SECTOR, ChatHelper.colored(
              this.plugin.getSectorsConfiguration().messagesWrapper.nearSectorBossBarTitle.replace(
                  "{DISTANCE}", formattedDistance)),
          SectorHelper.getDistanceToNearestSector(player.getLocation()) / 50F);
      return;
    }

    userBar.addBossBar(UserBarType.SECTOR, BossBarBuilder.add(UserBarConstants.SECTOR_UUID)
        .color(this.plugin.getSectorsConfiguration().borderBarColorWrapper.toOriginal())
        .progress(SectorHelper.getDistanceToNearestSector(player.getLocation()) / 50F)
        .style(this.plugin.getSectorsConfiguration().borderBarStyleWrapper.toOriginal()).title(
            TextComponent.fromLegacyText(ChatHelper.colored(
                this.plugin.getSectorsConfiguration().messagesWrapper.nearSectorBossBarTitle.replace(
                    "{DISTANCE}", formattedDistance)))));
  }
}