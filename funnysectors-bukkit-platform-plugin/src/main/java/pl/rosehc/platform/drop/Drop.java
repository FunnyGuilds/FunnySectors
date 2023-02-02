package pl.rosehc.platform.drop;

import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.rosehc.platform.PlatformConfiguration.DropSettingsWrapper;
import pl.rosehc.platform.PlatformPlugin;
import pl.rosehc.platform.drop.fortune.DropFortuneHelper;
import pl.rosehc.platform.user.PlatformUser;
import pl.rosehc.platform.user.event.PlatformUserDropChanceEvent;

public final class Drop {

  private final String name;
  private final Material material;
  private final double chance;
  private final boolean fortune;
  private final short data;
  private final int minAmount, maxAmount;
  private final int minY, maxY;
  private final float exp;

  public Drop(final String name, final Material material, final double chance,
      final boolean fortune, final short data, final int minAmount, final int maxAmount,
      final int minY, final int maxY, final float exp) {
    this.name = name;
    this.material = material;
    this.chance = chance;
    this.fortune = fortune;
    this.data = data;
    this.minAmount = minAmount;
    this.maxAmount = maxAmount;
    this.minY = minY;
    this.maxY = maxY;
    this.exp = exp;
  }

  public double calculateChance(final Player player, final PlatformUser user) {
    final DropSettingsWrapper dropSettingsWrapper = PlatformPlugin.getInstance()
        .getPlatformConfiguration().dropSettingsWrapper;
    double chance = this.chance;
    if (user.getDropSettings().getTurboDropTime() >= System.currentTimeMillis()) {
      chance *= user.getDropSettings().getTurboDropMultiplier();
    } else if (dropSettingsWrapper.turboDropTime >= System.currentTimeMillis()) {
      chance *= dropSettingsWrapper.turboDropMultiplier;
    }

    for (final Entry<String, Double> entry : dropSettingsWrapper.dropMultipliersPerPermissionsMap.entrySet()) {
      if (player.hasPermission(entry.getKey())) {
        chance *= entry.getValue();
      }
    }

    final PlatformUserDropChanceEvent dropChanceEvent = new PlatformUserDropChanceEvent(user, this,
        chance);
    Bukkit.getPluginManager().callEvent(dropChanceEvent);
    return dropChanceEvent.getChance();
  }

  public ItemStack toItemStack(final ItemStack tool) {
    final ItemStack itemStack = new ItemStack(this.material,
        ThreadLocalRandom.current().nextInt(this.minAmount, this.maxAmount), this.data);
    if (this.fortune && tool.containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS)) {
      itemStack.setAmount(DropFortuneHelper.recalculateDropAmount(tool, itemStack.getAmount()));
    }

    return itemStack;
  }

  public String getName() {
    return this.name;
  }

  public Material getMaterial() {
    return this.material;
  }

  public double getChance() {
    return this.chance;
  }

  public short getData() {
    return this.data;
  }

  public int getMinAmount() {
    return this.minAmount;
  }

  public int getMaxAmount() {
    return this.maxAmount;
  }

  public int getMinY() {
    return this.minY;
  }

  public int getMaxY() {
    return this.maxY;
  }

  public float getExp() {
    return this.exp;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }

    if (object == null || this.getClass() != object.getClass()) {
      return false;
    }

    Drop drop = (Drop) object;
    return this.name.equals(drop.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.name);
  }
}
