package pl.rosehc.platform.cobblex;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import pl.rosehc.adapter.builder.ItemStackBuilder;
import pl.rosehc.adapter.helper.NumberHelper;
import pl.rosehc.controller.wrapper.spigot.SpigotGuiEnchantmentWrapper;
import pl.rosehc.platform.PlatformConfiguration.CobbleXItemWrapper;

public final class CobbleXItem {

  private final ItemStack itemStack;
  private final int minAmount, maxAmount;

  private CobbleXItem(final ItemStack itemStack, final int minAmount, final int maxAmount) {
    this.itemStack = itemStack;
    this.minAmount = minAmount;
    this.maxAmount = maxAmount;
  }

  public static CobbleXItem create(final CobbleXItemWrapper wrapper) {
    final ItemStackBuilder builder = new ItemStackBuilder(Material.matchMaterial(wrapper.material),
        1, wrapper.data);
    if (wrapper.enchantments != null && !wrapper.enchantments.isEmpty()) {
      final Map<Enchantment, Integer> enchantmentMap = new HashMap<>();
      for (final SpigotGuiEnchantmentWrapper enchantment : wrapper.enchantments) {
        enchantmentMap.put(Enchantment.getByName(enchantment.enchantmentName),
            enchantment.enchantmentLevel);
      }

      builder.withEnchantments(enchantmentMap);
    }

    if (wrapper.name != null) {
      builder.withName(wrapper.name);
    }
    if (wrapper.lore != null && !wrapper.lore.isEmpty()) {
      builder.withLore(wrapper.lore);
    }

    return new CobbleXItem(builder.build(), wrapper.minAmount, wrapper.maxAmount);
  }

  public ItemStack asItemStack() {
    final ItemStack itemStack = this.itemStack.clone();
    if (this.minAmount > 1 && this.maxAmount > 1) {
      itemStack.setAmount(NumberHelper.range(this.minAmount, this.maxAmount));
    }

    return itemStack;
  }

  public ItemStack getOriginalItemStack() {
    return this.itemStack;
  }
}
