package pl.rosehc.platform.user.task;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Objects;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.rosehc.platform.PlatformPlugin;

public final class PlatformUserRemoveDiamondArmorTask implements Runnable {

  private static final Map<Material, Material> ARMOR_REPLACEMENT_MAP = ImmutableMap.of(
      Material.DIAMOND_HELMET, Material.IRON_HELMET,
      Material.DIAMOND_CHESTPLATE, Material.IRON_CHESTPLATE,
      Material.DIAMOND_LEGGINGS, Material.IRON_LEGGINGS,
      Material.DIAMOND_BOOTS, Material.IRON_BOOTS
  );
  private final PlatformPlugin plugin;

  public PlatformUserRemoveDiamondArmorTask(final PlatformPlugin plugin) {
    this.plugin = plugin;
    this.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(this.plugin, this, 20L, 20L);
  }

  @Override
  public void run() {
    for (final Player player : this.plugin.getServer().getOnlinePlayers()) {
      final ItemStack[] armorContents = player.getInventory().getArmorContents();
      boolean anyModified = false;
      for (int slot = 0; slot < armorContents.length; slot++) {
        final ItemStack armorContent = armorContents[slot];
        if (Objects.isNull(armorContent)) {
          continue;
        }

        final Material replacementMaterial = ARMOR_REPLACEMENT_MAP.get(armorContent.getType());
        if (Objects.nonNull(replacementMaterial)) {
          final ItemStack replacementContent = new ItemStack(replacementMaterial);
          armorContents[slot] = replacementContent;
          anyModified = true;
        }
      }

      if (anyModified) {
        player.getInventory().setArmorContents(armorContents);
      }
    }
  }
}
