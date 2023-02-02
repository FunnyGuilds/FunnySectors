package pl.rosehc.platform.deposit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.adapter.helper.ItemHelper;
import pl.rosehc.controller.packet.platform.user.PlatformUserAddDepositLimitsPacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserRemoveDepositLimitsPacket;
import pl.rosehc.controller.wrapper.platform.PlatformUserDepositItemTypeWrapper;
import pl.rosehc.platform.PlatformPlugin;
import pl.rosehc.platform.user.PlatformUser;
import pl.rosehc.sectors.SectorsPlugin;

public final class DepositHelper {

  private DepositHelper() {
  }

  public static boolean withdraw(final Player player, final PlatformUser user,
      final DepositItemType... types) {
    final Map<PlatformUserDepositItemTypeWrapper, Integer> removedDepositLimitMap = new HashMap<>();
    final List<ItemStack> withdrawnItemList = new ArrayList<>();
    for (final DepositItemType type : types) {
      final PlatformUserDepositItemTypeWrapper wrappedType = PlatformUserDepositItemTypeWrapper.fromOriginal(
          type);
      final Integer limit = PlatformPlugin.getInstance().getPlatformConfiguration().limitMap.get(
          wrappedType);
      if (Objects.isNull(limit)) {
        continue;
      }

      final ItemStack itemStack = type.itemStack();
      final int inventoryAmount = ItemHelper.countItemAmount(player,
          itemStack), depositAmount = user.getItemAmountInDeposit(type);
      if (inventoryAmount >= limit || depositAmount <= 0) {
        continue;
      }

      itemStack.setAmount(Math.min(limit - inventoryAmount, depositAmount));
      removedDepositLimitMap.put(wrappedType, itemStack.getAmount());
      withdrawnItemList.add(itemStack);
      user.removeItemFromDeposit(type, itemStack.getAmount());
    }

    if (withdrawnItemList.isEmpty() && removedDepositLimitMap.isEmpty()) {
      ChatHelper.sendMessage(player, PlatformPlugin.getInstance()
          .getPlatformConfiguration().messagesWrapper.depositNoItemsToWithdrawFound);
      return false;
    }

    ItemHelper.addItems(player, withdrawnItemList);
    PlatformPlugin.getInstance().getRedisAdapter().sendPacket(
        new PlatformUserRemoveDepositLimitsPacket(player.getUniqueId(), removedDepositLimitMap),
        "rhc_master_controller", "rhc_platform");
    return true;
  }

  public static void limit(final Player player) {
    if (!SectorsPlugin.getInstance().getSectorUserFactory().findUserByUniqueId(player.getUniqueId())
        .filter(user -> !user.isRedirecting()).isPresent()) {
      return;
    }

    PlatformPlugin.getInstance().getPlatformUserFactory().findUserByUniqueId(player.getUniqueId())
        .ifPresent(user -> {
          final Map<PlatformUserDepositItemTypeWrapper, Integer> addedDepositLimitMap = new HashMap<>();
          for (final DepositItemType type : DepositItemType.values()) {
            final PlatformUserDepositItemTypeWrapper wrappedType = PlatformUserDepositItemTypeWrapper.fromOriginal(
                type);
            final Integer limit = PlatformPlugin.getInstance()
                .getPlatformConfiguration().limitMap.get(wrappedType);
            if (Objects.isNull(limit)) {
              continue;
            }

            final ItemStack itemStack = type.itemStack();
            final int amount = ItemHelper.countItemAmount(player, itemStack);
            if (amount > limit) {
              final int difference = amount - limit;
              ItemHelper.removeItem(player, itemStack, difference);
              user.addItemToDeposit(type, difference);
              addedDepositLimitMap.put(wrappedType, difference);
            }
          }

          if (!addedDepositLimitMap.isEmpty()) {
            PlatformPlugin.getInstance().getRedisAdapter().sendPacket(
                new PlatformUserAddDepositLimitsPacket(player.getUniqueId(), addedDepositLimitMap),
                "rhc_master_controller", "rhc_platform");
            ChatHelper.sendMessage(player, PlatformPlugin.getInstance()
                .getPlatformConfiguration().messagesWrapper.depositLimitReached);
          }
        });
  }
}
