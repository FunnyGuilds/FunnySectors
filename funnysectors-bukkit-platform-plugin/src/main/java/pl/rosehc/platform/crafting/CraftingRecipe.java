package pl.rosehc.platform.crafting;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public final class CraftingRecipe {

  private static final List<Character> SYMBOLS = Arrays.asList('a', 'b', 'c', 'd', 'e', 'f', 'g',
      'h', 'i');
  private static final String[] PARTITIONED_SYMBOLS = Lists.partition(SYMBOLS, 3).stream()
      .map(list -> list.stream().map(String::valueOf).collect(Collectors.joining()))
      .toArray(String[]::new);

  private final ItemStack result;
  private final Map<Integer, ItemStack> ingredientMap;

  public CraftingRecipe(final ItemStack result, final Map<Integer, ItemStack> ingredientMap) {
    this.result = result;
    this.ingredientMap = ingredientMap;
  }

  public ItemStack getResult() {
    return this.result;
  }

  public Map<Integer, ItemStack> getIngredientMap() {
    return this.ingredientMap;
  }

  public ShapedRecipe shape() {
    final ShapedRecipe recipe = new ShapedRecipe(this.result);
    recipe.shape(PARTITIONED_SYMBOLS);
    for (int i = 0; i < SYMBOLS.size(); i++) {
      final ItemStack ingredient = this.ingredientMap.get(i);
      if (Objects.nonNull(ingredient)) {
        recipe.setIngredient(SYMBOLS.get(i), ingredient.getType());
      }
    }

    return recipe;
  }
}
