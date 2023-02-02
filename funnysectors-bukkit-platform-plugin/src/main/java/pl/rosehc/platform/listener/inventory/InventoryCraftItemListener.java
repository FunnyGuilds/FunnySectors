package pl.rosehc.platform.listener.inventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.platform.PlatformPlugin;
import pl.rosehc.platform.user.PlatformUser;

public final class InventoryCraftItemListener implements Listener {

  private static final List<Material> BLOCKED_ARMOR_LIST = Arrays.asList(
      Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE,
      Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS
  );
  private final PlatformPlugin plugin;

  public InventoryCraftItemListener(final PlatformPlugin plugin) {
    this.plugin = plugin;
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onCraftItem(final CraftItemEvent event) {
    final Player player = (Player) event.getWhoClicked();
    final Recipe recipe = event.getRecipe();
    if (this.plugin.getPlatformUserFactory().findUserByUniqueId(event.getWhoClicked().getUniqueId())
        .filter(PlatformUser::isInCombat).isPresent()) {
      event.setCancelled(true);
      ChatHelper.sendMessage(player,
          this.plugin.getPlatformConfiguration().messagesWrapper.cannotCraftItemsInCombat);
      return;
    }

    if (recipe.getResult().getType().equals(Material.GOLDEN_APPLE)
        && recipe.getResult().getData().getData() == 1) {
      event.setCancelled(true);
      ChatHelper.sendMessage(player,
          this.plugin.getPlatformConfiguration().messagesWrapper.cannotCraftEnchantedGoldenApples);
      return;
    }

    if (BLOCKED_ARMOR_LIST.contains(recipe.getResult().getType())) {
      event.setCancelled(true);
      return;
    }

    if (recipe.getResult().getType().equals(Material.ARMOR_STAND) || recipe.getResult().getType()
        .equals(Material.ITEM_FRAME)) {
      event.setCancelled(true);
      return;
    }

    this.plugin.getCraftingRecipeFactory().findCraftingRecipe(recipe.getResult())
        .ifPresent(craftingRecipe -> {
          final CraftingInventory inventory = event.getInventory();
          final ItemStack[] matrix = inventory.getMatrix();
          final Map<Integer, ItemStack> ingredientMap = craftingRecipe.getIngredientMap();
          final List<ItemStack> matchedIngredientList = new ArrayList<>();
          for (int slot = 0; slot < matrix.length; slot++) {
            final ItemStack matrixItemStack = matrix[slot];
            if (Objects.isNull(matrixItemStack) || matrixItemStack.getType() == Material.AIR
                || matrixItemStack.getAmount() < 1) {
              continue;
            }

            final ItemStack ingredient = ingredientMap.get(slot);
            if (Objects.nonNull(ingredient) && matrixItemStack.isSimilar(ingredient)
                && matrixItemStack.getAmount() >= ingredient.getAmount()) {
              final ItemStack matrixItemStackClone = matrixItemStack.clone();
              matrixItemStackClone.setAmount(ingredient.getAmount());
              matchedIngredientList.add(matrixItemStackClone);
            }
          }

          if (matchedIngredientList.size() < ingredientMap.size()) {
            event.setCancelled(true);
            return;
          }

          final ItemStack result = craftingRecipe.getResult().clone();
          result.setAmount(event.getInventory().getResult().getAmount());
          inventory.removeItem(matchedIngredientList.toArray(new ItemStack[0]));
          inventory.setResult(result);
        });
  }
}
