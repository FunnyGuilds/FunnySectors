package pl.rosehc.platform.deposit;

import java.util.function.Supplier;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import pl.rosehc.platform.PlatformPlugin;

public enum DepositItemType {

  GOLDEN_HEADS(PlatformPlugin.getInstance()
      .getPlatformConfiguration().customItemsWrapper.goldenHeadWrapper::asItemStack),
  GOLDEN_APPLES(() -> new ItemStack(Material.GOLDEN_APPLE)),
  @SuppressWarnings("SpellCheckingInspection") ENDER_PEARLS(
      () -> new ItemStack(Material.ENDER_PEARL)),
  SNOWBALLS(() -> new ItemStack(Material.SNOW_BALL)),
  FISHING_RODS(() -> new ItemStack(Material.FISHING_ROD));

  private final Supplier<ItemStack> supplier;

  DepositItemType(final Supplier<ItemStack> supplier) {
    this.supplier = supplier;
  }

  public ItemStack itemStack() {
    return this.supplier.get();
  }
}
