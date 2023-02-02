package pl.rosehc.adapter.helper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * @author stevimeister on 01/10/2021
 **/
public final class ItemHelper {

  private ItemHelper() {
  }

  public static void removeItem(final Player player, final ItemStack firstItemStack,
      final int amount) {
    final Function<ItemStack, Boolean> comparatorFunc =
        !firstItemStack.hasItemMeta() ? secondItemStack ->
            Objects.equals(secondItemStack.getType(), firstItemStack.getType())
                && secondItemStack.getDurability() == firstItemStack.getDurability()
            : secondItemStack -> secondItemStack.isSimilar(firstItemStack);
    final PlayerInventory inventory = player.getInventory();
    final ItemStack[] itemStacks = inventory.getContents();
    int removed = amount;
    for (int slot = 0; slot < itemStacks.length; slot++) {
      final ItemStack secondItemStack = itemStacks[slot];
      if (Objects.isNull(secondItemStack) || !comparatorFunc.apply(secondItemStack)) {
        continue;
      }

      int contentAmount = secondItemStack.getAmount();
      if (contentAmount <= removed) {
        inventory.clear(slot);
        removed -= contentAmount;
      } else {
        secondItemStack.setAmount(contentAmount - removed);
        removed = 0;
      }

      if (removed <= 0) {
        break;
      }
    }
  }

  public static void addItem(final Player player, final ItemStack itemStack) {
    addItems(player, Collections.singletonList(itemStack));
  }

  public static void addItems(final Player player, final List<ItemStack> itemStacks) {
    if (!itemStacks.isEmpty()) {
      for (final ItemStack droppedItemStack : player.getInventory()
          .addItem(itemStacks.toArray(new ItemStack[0])).values()) {
        player.getWorld().dropItem(player.getLocation(), droppedItemStack);
      }
    }
  }

  public static void subtractItem(final Player player, final ItemStack itemStack) {
    if (itemStack.getAmount() > 1) {
      itemStack.setAmount(itemStack.getAmount() - 1);
      return;
    }

    player.getInventory().removeItem(itemStack);
  }

  public static ItemStack parseItem(final String string) {
    final String[] split = string.split(" ");
    final String[] materialSplit = split[1].split(":");
    final Material material = Material.matchMaterial(materialSplit[0]);
    if (Objects.isNull(material)) {
      return new ItemStack(Material.AIR);
    }

    final ItemStack itemStack = new ItemStack(material, Integer.parseInt(split[0], 1),
        (short) (materialSplit.length > 1 ? Integer.parseInt(materialSplit[1]) : 0));
    final ItemMeta itemMeta = itemStack.getItemMeta();
    for (int i = 2; i < split.length; i++) {
      final String[] attributeSplit = split[i].split(":");
      final String[] attributeValue = Arrays.copyOfRange(attributeSplit, 1, attributeSplit.length);
      final String attributeName = attributeSplit[0];

      if (attributeName.equalsIgnoreCase("name")) {
        itemMeta.setDisplayName(
            ChatHelper.colored(StringUtils.replace(String.join(":", attributeValue), "_", " ")));
      }

      if (attributeName.equalsIgnoreCase("lore")) {
        itemMeta.setLore(Arrays.stream(String.join(":", attributeValue).split("#"))
            .map(s -> StringUtils.replace(ChatHelper.colored(s), "_", " "))
            .collect(Collectors.toList()));
      }

      if (attributeName.equalsIgnoreCase("enchantment")) {
        final Enchantment enchantment = Enchantment.getByName(attributeValue[0]);
        if (Objects.isNull(enchantment)) {
          continue;
        }

        itemMeta.addEnchant(enchantment, Integer.parseInt(attributeValue[1], 1), true);
      }
    }

    itemStack.setItemMeta(itemMeta);
    return itemStack;
  }

  public static boolean hasItem(final Player player, final ItemStack firstItemStack,
      final int needed) {
    return countItemAmount(player, firstItemStack) >= needed;
  }

  public static int countItemAmount(final Player player, final ItemStack firstItemStack) {
    final Function<ItemStack, Boolean> comparatorFunc =
        !firstItemStack.hasItemMeta() ? secondItemStack ->
            Objects.equals(secondItemStack.getType(), firstItemStack.getType())
                && secondItemStack.getDurability() == firstItemStack.getDurability()
            : secondItemStack -> secondItemStack.isSimilar(firstItemStack);
    int amount = 0;
    for (final ItemStack secondItemStack : player.getInventory().getContents()) {
      if (secondItemStack != null && comparatorFunc.apply(secondItemStack)) {
        amount += secondItemStack.getAmount();
      }
    }

    return amount;
  }

  public static boolean isValid(final ItemStack itemStack) {
    if (Objects.isNull(itemStack)) {
      return false;
    }

    return !itemStack.getType().isBlock() && !itemStack.getType().isFlammable() &&
        !itemStack.getType().isEdible() && !itemStack.getType().isBurnable() &&
        !itemStack.getType().isTransparent() && itemStack.getType() != Material.AIR
        && itemStack.getDurability() > 1;
  }
}

