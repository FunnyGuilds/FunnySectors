package pl.rosehc.controller.packet;

import java.util.Objects;
import java.util.stream.Collectors;
import org.bukkit.entity.Player;
import pl.rosehc.adapter.helper.ItemHelper;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.discord.DiscordRewardGivePacket;
import pl.rosehc.platform.PlatformConfiguration.CustomItemsWrapper.RewardItemWrapper;
import pl.rosehc.platform.PlatformPlugin;

public final class DiscordPacketHandler implements PacketHandler {

  private final PlatformPlugin plugin;

  public DiscordPacketHandler(final PlatformPlugin plugin) {
    this.plugin = plugin;
  }

  public void handle(final DiscordRewardGivePacket packet) {
    final Player player = this.plugin.getServer().getPlayer(packet.getUniqueId());
    if (Objects.nonNull(player)) {
      this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin,
          () -> ItemHelper.addItems(player,
              this.plugin.getPlatformConfiguration().customItemsWrapper.rewardItemWrapperList.stream()
                  .map(RewardItemWrapper::asItemStack).collect(Collectors.toList())));
    }
  }
}
