package pl.rosehc.platform.user.task;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.adapter.helper.TimeHelper;
import pl.rosehc.bossbar.BossBarBuilder;
import pl.rosehc.bossbar.BossBarPlugin;
import pl.rosehc.bossbar.user.UserBar;
import pl.rosehc.bossbar.user.UserBarConstants;
import pl.rosehc.bossbar.user.UserBarType;
import pl.rosehc.platform.PlatformConfiguration.SpecialBossBarWrapper;
import pl.rosehc.platform.PlatformPlugin;

public final class PlatformUserSpecialBarUpdateTask implements Runnable {

  private final PlatformPlugin plugin;

  public PlatformUserSpecialBarUpdateTask(final PlatformPlugin plugin) {
    this.plugin = plugin;
    this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(this.plugin, this, 20L, 20L);
  }

  @Override
  public void run() {
    final SpecialBossBarWrapper specialBossBarWrapper = this.plugin.getPlatformConfiguration().specialBossBarWrapper;
    if (Objects.isNull(specialBossBarWrapper)
        || specialBossBarWrapper.expiryTime < System.currentTimeMillis()) {
      return;
    }

    final long delta = specialBossBarWrapper.expiryTime - System.currentTimeMillis();
    final float progress = Math.min(
        TimeUnit.MILLISECONDS.toSeconds(delta) / specialBossBarWrapper.expiryMaxBars, 1F);
    for (final Player player : this.plugin.getServer().getOnlinePlayers()) {
      final UserBar userBar = BossBarPlugin.getInstance().getUserBarFactory().getUserBar(player);
      if (!userBar.hasBossBar(UserBarType.SPECIAL_BAR)) {
        userBar.addBossBar(UserBarType.SPECIAL_BAR,
            BossBarBuilder.add(UserBarConstants.SPECIAL_BAR_UUID)
                .color(specialBossBarWrapper.barColorWrapper.toOriginal())
                .style(specialBossBarWrapper.barStyleWrapper.toOriginal()).progress(progress).title(
                    TextComponent.fromLegacyText(ChatHelper.colored(
                        specialBossBarWrapper.title.replace("{TIME}",
                            TimeHelper.timeToString(delta))))));
      } else {
        userBar.updateBossBar(UserBarType.SPECIAL_BAR, ChatHelper.colored(
                specialBossBarWrapper.title.replace("{TIME}", TimeHelper.timeToString(delta))),
            progress);
      }
    }
  }
}
