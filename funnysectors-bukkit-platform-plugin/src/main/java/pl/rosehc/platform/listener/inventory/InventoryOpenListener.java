package pl.rosehc.platform.listener.inventory;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.platform.PlatformPlugin;
import pl.rosehc.platform.user.PlatformUser;

public final class InventoryOpenListener implements Listener {

  private final PlatformPlugin plugin;

  public InventoryOpenListener(final PlatformPlugin plugin) {
    this.plugin = plugin;
  }

  @EventHandler(ignoreCancelled = true)
  public void onOpen(final InventoryOpenEvent event) {
    if (event.getInventory().getType().equals(InventoryType.WORKBENCH)
        && this.plugin.getPlatformUserFactory().findUserByUniqueId(event.getPlayer().getUniqueId())
        .filter(PlatformUser::isInCombat).isPresent()) {
      event.setCancelled(true);
      ChatHelper.sendMessage(event.getPlayer(),
          this.plugin.getPlatformConfiguration().messagesWrapper.cannotCraftItemsInCombat);
    }
  }
}
