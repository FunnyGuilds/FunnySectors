package pl.rosehc.adapter.inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

/**
 * @author stevimeister on 15/12/2020
 **/
public final class BukkitInventory implements InventoryHolder {

  private final Map<Integer, BukkitInventoryElement> elements;
  private final Inventory inventory;
  private Consumer<InventoryClickEvent> clickAction;
  private Consumer<InventoryOpenEvent> openAction;
  private Consumer<InventoryCloseEvent> closeAction;
  private boolean cancellable = true;

  public BukkitInventory(final String name, final int size) {
    this.elements = new HashMap<>(size);
    this.inventory = Bukkit.createInventory(this, size, name);
  }

  public BukkitInventory(final String name, final InventoryType type) {
    this.elements = new HashMap<>(type.getDefaultSize());
    this.inventory = Bukkit.createInventory(this, type, name);
  }

  @Override
  public Inventory getInventory() {
    return this.inventory;
  }

  public Consumer<InventoryClickEvent> getClickAction() {
    return this.clickAction;
  }

  public void setClickAction(final Consumer<InventoryClickEvent> clickAction) {
    this.clickAction = clickAction;
  }

  public Consumer<InventoryOpenEvent> getOpenAction() {
    return this.openAction;
  }

  public void setOpenAction(final Consumer<InventoryOpenEvent> openAction) {
    this.openAction = openAction;
  }

  public Consumer<InventoryCloseEvent> getCloseAction() {
    return this.closeAction;
  }

  public void setCloseAction(final Consumer<InventoryCloseEvent> closeAction) {
    this.closeAction = closeAction;
  }

  public BukkitInventoryElement findElement(int index) {
    return this.elements.get(index);
  }

  public void setElement(final int index, final BukkitInventoryElement element) {
    this.elements.put(index, element);
  }

  public void addElement(final BukkitInventoryElement element) {
    for (int i = 0; i < this.inventory.getSize(); i++) {
      if (!this.elements.containsKey(i)) {
        this.setElement(i, element);
        return;
      }
    }
  }

  public void fillWith(final ItemStack itemStack) {
    for (int i = 0; i < this.inventory.getSize(); i++) {
      this.inventory.setItem(i, itemStack);
    }
  }

  public void openInventory(final Player player) {
    for (Map.Entry<Integer, BukkitInventoryElement> entry : this.elements.entrySet()) {
      this.inventory.setItem(entry.getKey(), entry.getValue().getItemStack());
    }

    player.openInventory(this.inventory);
  }

  public boolean isCancellable() {
    return this.cancellable;
  }

  public void setCancellable(final boolean cancellable) {
    this.cancellable = cancellable;
  }
}

