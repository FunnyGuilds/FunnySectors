package pl.rosehc.platform.command.player;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Sender;
import me.vaperion.blade.exception.BladeExitMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.adapter.helper.ItemHelper;
import pl.rosehc.adapter.helper.NumberHelper;
import pl.rosehc.platform.PlatformPlugin;

public final class CobbleXCommand {

  private static final ItemStack COBBLE_STONE_ITEM_STACK = new ItemStack(Material.COBBLESTONE, 576);
  private final PlatformPlugin plugin;

  public CobbleXCommand(final PlatformPlugin plugin) {
    this.plugin = plugin;
  }

  @Command(value = {"cobblex", "cx"}, description = "Tworzy nowego CobbleX dla gracza")
  public void handleCobbleX(final @Sender Player player) {
    final int cobbleStoneAmount = ItemHelper.countItemAmount(player, COBBLE_STONE_ITEM_STACK);
    if (cobbleStoneAmount < COBBLE_STONE_ITEM_STACK.getAmount()) {
      throw new BladeExitMessage(ChatHelper.colored(
          this.plugin.getPlatformConfiguration().messagesWrapper.cobbleXNoNeededItems.replace(
              "{AMOUNT_LEFT}",
              String.valueOf(COBBLE_STONE_ITEM_STACK.getAmount() - cobbleStoneAmount))));
    }

    ItemHelper.removeItem(player, COBBLE_STONE_ITEM_STACK, COBBLE_STONE_ITEM_STACK.getAmount());
    ItemHelper.addItem(player,
        this.plugin.getPlatformConfiguration().customItemsWrapper.cobbleXWrapper.asItemStack(
            NumberHelper.range(1, 3)));
    ChatHelper.sendMessage(player,
        this.plugin.getPlatformConfiguration().messagesWrapper.cobbleXSuccessfullyCreated);
  }
}
