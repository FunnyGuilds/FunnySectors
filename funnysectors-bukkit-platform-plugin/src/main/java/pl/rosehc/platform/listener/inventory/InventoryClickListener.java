package pl.rosehc.platform.listener.inventory;

import java.util.Objects;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.ItemStack;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.adapter.helper.ItemHelper;
import pl.rosehc.platform.PlatformPlugin;

public final class InventoryClickListener implements Listener {

  private final PlatformPlugin plugin;

  public InventoryClickListener(final PlatformPlugin plugin) {
    this.plugin = plugin;
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onClick(final InventoryClickEvent event) {
    if (event.getAction().equals(InventoryAction.NOTHING) || !event.getSlotType()
        .equals(SlotType.ARMOR)) {
      return;
    }

    final Player player = (Player) event.getWhoClicked();
    final ItemStack goldenHeadItemStack = this.plugin.getPlatformConfiguration().customItemsWrapper.goldenHeadWrapper.asItemStack();
    final ItemStack cursorItemStack = event.getCursor();
    boolean isGoldenHead = (Objects.nonNull(event.getCurrentItem()) && event.getCurrentItem()
        .isSimilar(goldenHeadItemStack)) || (Objects.nonNull(cursorItemStack)
        && cursorItemStack.isSimilar(goldenHeadItemStack));
    if (event.getAction().equals(InventoryAction.HOTBAR_SWAP) || event.getAction()
        .equals(InventoryAction.HOTBAR_MOVE_AND_READD)) {
      //noinspection SpellCheckingInspection
      final ItemStack hotbarItemStack = player.getInventory().getItem(event.getHotbarButton());
      isGoldenHead =
          (Objects.nonNull(hotbarItemStack) && hotbarItemStack.isSimilar(goldenHeadItemStack))
              || isGoldenHead;
    }

    if (isGoldenHead) {
      event.setCancelled(true);
      event.setCursor(null);
      if (Objects.nonNull(cursorItemStack)) {
        this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin,
            () -> ItemHelper.addItem(player, cursorItemStack));
      }

      ChatHelper.sendMessage(player,
          this.plugin.getPlatformConfiguration().messagesWrapper.cannotEquipGoldenHead);
    }
  }
}
