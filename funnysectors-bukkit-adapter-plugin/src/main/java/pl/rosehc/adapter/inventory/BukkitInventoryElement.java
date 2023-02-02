package pl.rosehc.adapter.inventory;

import java.util.function.Consumer;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author stevimeister on 15/12/2020
 **/
public final class BukkitInventoryElement {

  private final ItemStack itemStack;
  private Consumer<InventoryClickEvent> action;

  public BukkitInventoryElement(final ItemStack itemStack) {
    this.itemStack = itemStack;
  }

  public BukkitInventoryElement(final ItemStack itemStack,
      final Consumer<InventoryClickEvent> action) {
    this(itemStack);
    this.action = action;
  }

  ItemStack getItemStack() {
    return this.itemStack;
  }

  public Consumer<InventoryClickEvent> getAction() {
    return this.action;
  }
}