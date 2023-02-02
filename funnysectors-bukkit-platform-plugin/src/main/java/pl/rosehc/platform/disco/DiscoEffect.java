package pl.rosehc.platform.disco;

import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import pl.rosehc.adapter.builder.ItemStackBuilder;
import pl.rosehc.platform.PlatformPlugin;

public abstract class DiscoEffect {

  private List<DiscoArmorWrapper> wrapperList = new ArrayList<>();

  public List<DiscoArmorWrapper> getWrapperList() {
    return this.wrapperList;
  }

  protected abstract List<Color> calculateColors();

  public void update() {
    final List<Color> colors = this.calculateColors();
    if (colors.isEmpty()) {
      PlatformPlugin.getInstance().getLogger()
          .log(Level.WARNING, "Cannot update disco effect by name: {}",
              this.getClass().getSimpleName());
      return;
    }

    final Color firstColor = colors.get(0);
    this.wrapperList = Arrays.asList(DiscoArmorWrapper.wrap(
        new ItemStackBuilder(new ItemStack(Material.LEATHER_HELMET)).withColor(firstColor).build(),
        4), DiscoArmorWrapper.wrap(
        new ItemStackBuilder(new ItemStack(Material.LEATHER_CHESTPLATE)).withColor(
            Iterables.get(colors, 1, firstColor)).build(), 3), DiscoArmorWrapper.wrap(
        new ItemStackBuilder(new ItemStack(Material.LEATHER_LEGGINGS)).withColor(
            Iterables.get(colors, 2, firstColor)).build(), 2), DiscoArmorWrapper.wrap(
        new ItemStackBuilder(new ItemStack(Material.LEATHER_BOOTS)).withColor(
            Iterables.get(colors, 3, firstColor)).build(), 1));
  }
}
