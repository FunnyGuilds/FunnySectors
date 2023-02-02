package pl.rosehc.platform.drop.fortune;

import java.util.HashSet;
import java.util.Set;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import pl.rosehc.adapter.helper.NumberHelper;

public final class DropFortuneHelper {

  private static final Set<DropFortuneEntry> DROP_FORTUNE_ENTRY_SET = new HashSet<>();

  static {
    DROP_FORTUNE_ENTRY_SET.add(new DropFortuneEntry(50D, 1, 1));
    DROP_FORTUNE_ENTRY_SET.add(new DropFortuneEntry(30D, 2, 2));
    DROP_FORTUNE_ENTRY_SET.add(new DropFortuneEntry(20D, 3, 3));
  }

  private DropFortuneHelper() {
  }

  public static int recalculateDropAmount(final ItemStack tool, final int amount) {
    DropFortuneEntry greatestEntry = null;
    for (DropFortuneEntry entry : DROP_FORTUNE_ENTRY_SET) {
      if (tool.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS) >= entry.getLevel() && (
          greatestEntry == null || entry.getLevel() > greatestEntry.getLevel()) && (
          entry.getChance() >= 100D || entry.getChance() >= NumberHelper.range(0D, 100D))) {
        greatestEntry = entry;
      }
    }

    return greatestEntry != null ? amount + greatestEntry.getIncremental() : amount;
  }
}
