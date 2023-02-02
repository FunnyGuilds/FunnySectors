package pl.rosehc.platform.listener.player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import pl.rosehc.platform.PlatformConfiguration.EnchantmentBlockadesWrapper.EnchantmentBlockadeWrapper;
import pl.rosehc.platform.PlatformPlugin;

public final class PlayerEnchantItemListener implements Listener {

  private final PlatformPlugin plugin;

  public PlayerEnchantItemListener(final PlatformPlugin plugin) {
    this.plugin = plugin;
  }

  @EventHandler
  public void onEnchant(final EnchantItemEvent event) {
    final Map<Enchantment, Integer> enchantmentsToAddMap = new HashMap<>(event.getEnchantsToAdd());
    final List<EnchantmentBlockadeWrapper> blockedEnchantmentsOnItemList = this.plugin.getPlatformConfiguration().enchantmentBlockadesWrapper.blockedEnchantmentsOnItemsMap.get(
        event.getItem().getType().name());
    boolean alreadyModified = false;
    if (Objects.nonNull(blockedEnchantmentsOnItemList)
        && !blockedEnchantmentsOnItemList.isEmpty()) {
      for (final EnchantmentBlockadeWrapper wrapper : blockedEnchantmentsOnItemList) {
        final Enchantment enchantment = Enchantment.getByName(wrapper.enchantmentName);
        if (enchantmentsToAddMap.containsKey(enchantment)
            && enchantmentsToAddMap.get(enchantment) >= wrapper.maxEnchantmentLevel) {
          enchantmentsToAddMap.put(enchantment, wrapper.maxEnchantmentLevel);
          alreadyModified = true;
        }
      }
    }

    if (!alreadyModified) {
      final List<EnchantmentBlockadeWrapper> blockedEnchantmentGlobalList = this.plugin.getPlatformConfiguration().enchantmentBlockadesWrapper.blockedEnchantmentList;
      if (!blockedEnchantmentGlobalList.isEmpty()) {
        for (final EnchantmentBlockadeWrapper wrapper : blockedEnchantmentGlobalList) {
          final Enchantment enchantment = Enchantment.getByName(wrapper.enchantmentName);
          if (enchantmentsToAddMap.containsKey(enchantment)
              && enchantmentsToAddMap.get(enchantment) >= wrapper.maxEnchantmentLevel) {
            enchantmentsToAddMap.put(enchantment, wrapper.maxEnchantmentLevel);
          }
        }
      }
    }

    event.getEnchantsToAdd().clear();
    event.getEnchantsToAdd().putAll(enchantmentsToAddMap);
  }
}
