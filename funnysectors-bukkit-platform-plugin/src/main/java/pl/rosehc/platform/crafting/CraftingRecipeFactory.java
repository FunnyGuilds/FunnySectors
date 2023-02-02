package pl.rosehc.platform.crafting;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import pl.rosehc.platform.PlatformConfiguration;
import pl.rosehc.platform.PlatformPlugin;

public final class CraftingRecipeFactory {

  private final Set<CraftingRecipe> recipeSet;

  public CraftingRecipeFactory(final PlatformConfiguration platformConfiguration) {
    this.recipeSet = ConcurrentHashMap.newKeySet();
    this.recipeSet.addAll(
        platformConfiguration.customItemsWrapper.customCraftingWrapperList.stream().map(
                wrapper -> new CraftingRecipe(wrapper.resultWrapper.asItemStack(),
                    wrapper.ingredientWrappersMap.entrySet().stream()
                        .map(entry -> new SimpleEntry<>(entry.getKey(), entry.getValue().asItemStack()))
                        .collect(Collectors.toMap(Entry::getKey, Entry::getValue))))
            .collect(Collectors.toSet()));
    this.registerRecipes();
  }

  public Optional<CraftingRecipe> findCraftingRecipe(final ItemStack result) {
    for (final CraftingRecipe recipe : this.recipeSet) {
      if (recipe.getResult().isSimilar(result)) {
        return Optional.of(recipe);
      }
    }

    return Optional.empty();
  }

  public Set<CraftingRecipe> getRecipeSet() {
    return this.recipeSet;
  }

  public void registerRecipes() {
    final Future<Void> future = Bukkit.getServer().getScheduler()
        .callSyncMethod(PlatformPlugin.getInstance(), () -> {
          Bukkit.resetRecipes();
          for (final CraftingRecipe recipe : recipeSet) {
            Bukkit.addRecipe(recipe.shape());
          }

          return null;
        });
    try {
      future.get();
    } catch (final InterruptedException | ExecutionException ex) {
      PlatformPlugin.getInstance().getLogger()
          .log(Level.WARNING, "Nie można było przeładować craftingów.", ex);
    }
  }
}
