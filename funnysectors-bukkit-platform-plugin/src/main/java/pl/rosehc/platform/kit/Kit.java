package pl.rosehc.platform.kit;

import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class Kit {

  private final String name;
  private final String permission;
  private final Map<Integer, ItemStack> itemStacks;
  private final long time;

  public Kit(final String name, final String permission, final Map<Integer, ItemStack> itemStacks,
      final long time) {
    this.name = name;
    this.permission = permission;
    this.itemStacks = itemStacks;
    this.time = time;
  }

  public String getName() {
    return this.name;
  }

  public String getPermission() {
    return this.permission;
  }

  public Map<Integer, ItemStack> getItemStacks() {
    return this.itemStacks;
  }

  public long getTime() {
    return this.time;
  }

  public boolean testNoPermission(final Player player) {
    if (this.permission == null || this.permission.trim().isEmpty()) {
      return false;
    }

    return !player.hasPermission(this.permission);
  }
}
