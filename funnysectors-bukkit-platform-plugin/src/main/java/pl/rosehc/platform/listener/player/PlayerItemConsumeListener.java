package pl.rosehc.platform.listener.player;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.platform.PlatformPlugin;

public final class PlayerItemConsumeListener implements Listener {

  private final PlatformPlugin plugin;

  public PlayerItemConsumeListener(final PlatformPlugin plugin) {
    this.plugin = plugin;
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onConsume(final PlayerItemConsumeEvent event) {
    final Player player = event.getPlayer();
    final ItemStack item = event.getItem();
    if (item.getType().equals(Material.GOLDEN_APPLE)) {
      if (item.getData().getData() == 1) {
        event.setCancelled(true);
        ChatHelper.sendMessage(player,
            this.plugin.getPlatformConfiguration().messagesWrapper.cannotEatEnchantedGoldenApples);
        return;
      }

      this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin,
          () -> this.plugin.getPlatformConfiguration().customItemsWrapper.goldenAppleEffectWrapperList.forEach(
              wrapper -> player.addPotionEffect(wrapper.asPotionEffect(), true)));
    }
  }
}
