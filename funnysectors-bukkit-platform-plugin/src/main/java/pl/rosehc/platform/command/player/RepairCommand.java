package pl.rosehc.platform.command.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Permission;
import me.vaperion.blade.annotation.Sender;
import me.vaperion.blade.exception.BladeExitMessage;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.platform.PlatformPlugin;

public final class RepairCommand {

  private final PlatformPlugin plugin;

  public RepairCommand(final PlatformPlugin plugin) {
    this.plugin = plugin;
  }

  private static boolean repair(final List<ItemStack> itemStackList) {
    boolean repaired = false;
    for (final ItemStack itemStack : itemStackList) {
      if (isRepairable(itemStack)) {
        itemStack.setDurability((short) 0);
        repaired = true;
      }
    }

    return repaired;
  }

  private static boolean isRepairable(ItemStack itemStack) {
    return Objects.nonNull(itemStack) && !itemStack.getType().isBlock() && !itemStack.getType()
        .isFlammable() && !itemStack.getType().isEdible() && !itemStack.getType().isBurnable()
        && !itemStack.getType().isTransparent() && !itemStack.getType().equals(Material.AIR)
        && itemStack.getDurability() >= 2 && (itemStack.getType() != Material.DIAMOND_PICKAXE
        || itemStack.getEnchantmentLevel(Enchantment.DIG_SPEED) < 6) && !itemStack.getType()
        .equals(Material.SKULL_ITEM);
  }

  @Permission("platform-command-repair-all")
  @Command(value = "repair all", description = "Naprawia wszystkie przedmioty w EQ")
  public void handleRepairAll(final @Sender Player player) {
    final List<ItemStack> itemsToRepairList = new ArrayList<>(
        Arrays.asList(player.getInventory().getContents()));
    itemsToRepairList.addAll(Arrays.asList(player.getInventory().getArmorContents()));
    if (repair(itemsToRepairList)) {
      ChatHelper.sendMessage(player,
          this.plugin.getPlatformConfiguration().messagesWrapper.repairAllSuccessfullyRepaired);
      player.playSound(player.getLocation(), Sound.ANVIL_USE, 50F, 50F);
      return;
    }

    throw new BladeExitMessage(ChatHelper.colored(
        this.plugin.getPlatformConfiguration().messagesWrapper.repairAllNoItemsFound));
  }

  @Command(value = "repair", description = "Naprawia dany przedmiot w ręce")
  public void handleRepair(final @Sender Player player) {
    final ItemStack itemInHand = player.getItemInHand();
    if (!repair(Collections.singletonList(itemInHand))) {
      throw new BladeExitMessage(ChatHelper.colored(
          this.plugin.getPlatformConfiguration().messagesWrapper.repairSingleCannotRepairThisItem));
    }

    ChatHelper.sendMessage(player,
        this.plugin.getPlatformConfiguration().messagesWrapper.repairSingleSuccessfullyRepaired);
    player.playSound(player.getLocation(), Sound.ANVIL_USE, 50F, 50F);
  }
}
