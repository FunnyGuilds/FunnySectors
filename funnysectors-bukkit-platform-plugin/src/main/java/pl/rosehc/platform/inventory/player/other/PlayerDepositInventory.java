package pl.rosehc.platform.inventory.player.other;

import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;
import me.vaperion.blade.exception.BladeExitMessage;
import org.bukkit.entity.Player;
import pl.rosehc.adapter.builder.ItemStackBuilder;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.adapter.inventory.BukkitInventory;
import pl.rosehc.adapter.inventory.BukkitInventoryElement;
import pl.rosehc.controller.wrapper.platform.PlatformUserDepositItemTypeWrapper;
import pl.rosehc.controller.wrapper.platform.gui.deposit.PlatformDepositItemSpigotGuiElementWrapper;
import pl.rosehc.controller.wrapper.platform.gui.deposit.PlatformDepositWithdrawAllSpigotGuiElementWrapper;
import pl.rosehc.controller.wrapper.spigot.SpigotGuiElementWrapper;
import pl.rosehc.controller.wrapper.spigot.SpigotGuiWrapper;
import pl.rosehc.platform.PlatformPlugin;
import pl.rosehc.platform.deposit.DepositHelper;
import pl.rosehc.platform.deposit.DepositItemType;
import pl.rosehc.platform.user.PlatformUser;

public final class PlayerDepositInventory {

  private final Player player;
  private final BukkitInventory inventory;

  public PlayerDepositInventory(final Player player) {
    this.player = player;
    final SpigotGuiWrapper depositGuiWrapper = PlatformPlugin.getInstance()
        .getPlatformConfiguration().inventoryMap.get("deposit");
    if (depositGuiWrapper == null) {
      throw new BladeExitMessage(ChatHelper.colored(
          PlatformPlugin.getInstance().getPlatformConfiguration().messagesWrapper.guiNotFound));
    }

    this.inventory = new BukkitInventory(ChatHelper.colored(depositGuiWrapper.inventoryName),
        depositGuiWrapper.inventorySize);
    final PlatformUser user = PlatformPlugin.getInstance().getPlatformUserFactory()
        .findUserByUniqueId(player.getUniqueId()).orElseThrow(() -> new BladeExitMessage(
            ChatHelper.colored(PlatformPlugin.getInstance()
                .getPlatformConfiguration().messagesWrapper.playerNotFound.replace("{PLAYER_NAME}",
                    player.getName()))));
    final SpigotGuiElementWrapper fillElement = depositGuiWrapper.fillElement;
    if (fillElement != null) {
      this.inventory.fillWith(fillElement.asItemStack());
    }

    for (final Entry<String, SpigotGuiElementWrapper> entry : depositGuiWrapper.elements.entrySet()) {
      if (!(entry.getValue() instanceof PlatformDepositItemSpigotGuiElementWrapper)) {
        this.inventory.setElement(entry.getValue().slot,
            new BukkitInventoryElement(entry.getValue().asItemStack()));
      }
    }

    for (final DepositItemType type : DepositItemType.values()) {
      final SpigotGuiElementWrapper element = depositGuiWrapper.elements.get("item" + type.name());
      final Integer limit = PlatformPlugin.getInstance().getPlatformConfiguration().limitMap.get(
          PlatformUserDepositItemTypeWrapper.fromOriginal(type));
      if (!(element instanceof PlatformDepositItemSpigotGuiElementWrapper) || Objects.isNull(
          limit)) {
        throw new BladeExitMessage(
            PlatformPlugin.getInstance().getPlatformConfiguration().messagesWrapper.guiNotFound);
      }

      final PlatformDepositItemSpigotGuiElementWrapper depositItemElement = (PlatformDepositItemSpigotGuiElementWrapper) element;
      this.inventory.setElement(element.slot, new BukkitInventoryElement(
          new ItemStackBuilder(type.itemStack()).withName(depositItemElement.name).withLore(
              depositItemElement.lore.stream().map(
                      content -> content.replace("{LIMIT}", String.valueOf(limit))
                          .replace("{AMOUNT_IN_DEPOSIT}",
                              String.valueOf(user.getItemAmountInDeposit(type))))
                  .collect(Collectors.toList())).build(), event -> {
        if (DepositHelper.withdraw(player, user, type)) {
          final PlayerDepositInventory inventory = new PlayerDepositInventory(player);
          inventory.open();
          return;
        }

        player.closeInventory();
      }));
    }

    final SpigotGuiElementWrapper withdrawAllElement = depositGuiWrapper.elements.get(
        "withdraw_all");
    if (withdrawAllElement instanceof PlatformDepositWithdrawAllSpigotGuiElementWrapper) {
      this.inventory.setElement(withdrawAllElement.slot,
          new BukkitInventoryElement(withdrawAllElement.asItemStack(), event -> {
            if (DepositHelper.withdraw(player, user, DepositItemType.values())) {
              final PlayerDepositInventory inventory = new PlayerDepositInventory(player);
              inventory.open();
            }
          }));
    }
  }

  public void open() {
    if (this.player.isOnline()) {
      this.inventory.openInventory(this.player);
    }
  }
}
