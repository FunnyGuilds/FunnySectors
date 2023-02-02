package pl.rosehc.controller.wrapper.spigot;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class SpigotGuiElementWrapper {

  public String material;
  public int slot;

  public ItemStack asItemStack() {
    return new ItemStack(Material.matchMaterial(this.material));
  }
}
