package pl.rosehc.adapter.inventory;

import java.util.Objects;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import pl.rosehc.adapter.AdapterPlugin;

/**
 * @author stevimeister on 15/12/2020
 **/
public final class BukkitInventoryListeners implements Listener {

  public BukkitInventoryListeners(final AdapterPlugin plugin) {
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onOpen(final InventoryOpenEvent event) {
    if (event.getInventory().getHolder() instanceof BukkitInventory) {
      final BukkitInventory inventory = (BukkitInventory) event.getInventory().getHolder();
      if (Objects.isNull(inventory.getOpenAction())) {
        return;
      }

      inventory.getOpenAction().accept(event);
    }
  }

  @EventHandler
  public void onClose(final InventoryCloseEvent event) {
    if (event.getInventory().getHolder() instanceof BukkitInventory) {
      final BukkitInventory inventory = (BukkitInventory) event.getInventory().getHolder();
      if (Objects.isNull(inventory.getCloseAction())) {
        return;
      }

      inventory.getCloseAction().accept(event);
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onClick(final InventoryClickEvent event) {
    if (event.isCancelled()) {
      return;
    }

    if (event.getInventory().getHolder() instanceof BukkitInventory) {
      final BukkitInventory inventory = (BukkitInventory) event.getInventory().getHolder();
      final BukkitInventoryElement element = inventory.findElement(event.getRawSlot());

      event.setCancelled(inventory.isCancellable());
      if (Objects.nonNull(inventory.getClickAction())) {
        inventory.getClickAction().accept(event);
      }

      if (Objects.isNull(element) || Objects.isNull(element.getAction())) {
        return;
      }

      element.getAction().accept(event);
    }
  }
}
