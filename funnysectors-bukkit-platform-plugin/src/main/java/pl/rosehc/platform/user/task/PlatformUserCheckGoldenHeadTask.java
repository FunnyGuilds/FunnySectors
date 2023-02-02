package pl.rosehc.platform.user.task;

import java.util.Objects;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.adapter.helper.ItemHelper;
import pl.rosehc.platform.PlatformPlugin;

public final class PlatformUserCheckGoldenHeadTask implements Runnable {

  private final PlatformPlugin plugin;

  public PlatformUserCheckGoldenHeadTask(final PlatformPlugin plugin) {
    this.plugin = plugin;
    this.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(this.plugin, this, 20L, 20L);
  }

  @Override
  public void run() {
    final ItemStack goldenHeadItemStack = this.plugin.getPlatformConfiguration().customItemsWrapper.goldenHeadWrapper.asItemStack();
    for (final Player player : this.plugin.getServer().getOnlinePlayers()) {
      final ItemStack helmet = player.getInventory().getHelmet();
      if (Objects.nonNull(helmet) && helmet.isSimilar(goldenHeadItemStack)) {
        ItemHelper.addItem(player, helmet);
        ChatHelper.sendMessage(player,
            this.plugin.getPlatformConfiguration().messagesWrapper.cannotEquipGoldenHead);
        player.getInventory().setHelmet(null);
      }
    }
  }
}
