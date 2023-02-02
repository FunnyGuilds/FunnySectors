package pl.rosehc.platform.command.player;

import java.util.HashSet;
import java.util.Set;
import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Sender;
import me.vaperion.blade.exception.BladeExitMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.adapter.helper.ItemHelper;
import pl.rosehc.platform.PlatformPlugin;

public final class BlocksCommand {

  private static final Set<BlockEntry> ENTRY_SET = new HashSet<BlockEntry>() {{
    this.add(new BlockEntry(new ItemStack(Material.GOLD_INGOT), Material.GOLD_BLOCK));
    this.add(new BlockEntry(new ItemStack(Material.IRON_INGOT), Material.IRON_BLOCK));
    this.add(new BlockEntry(new ItemStack(Material.DIAMOND), Material.DIAMOND_BLOCK));
    this.add(new BlockEntry(new ItemStack(Material.EMERALD), Material.EMERALD_BLOCK));
    this.add(new BlockEntry(new ItemStack(Material.COAL), Material.COAL_BLOCK));
  }};
  private final PlatformPlugin plugin;

  public BlocksCommand(final PlatformPlugin plugin) {
    this.plugin = plugin;
  }

  private static boolean replace(final Player player, final ItemStack from, final Material to) {
    final int amount = ItemHelper.countItemAmount(player, from);
    if (amount < 9) {
      return false;
    }

    final int amountInBlocks = amount / 9;
    ItemHelper.removeItem(player, from, amountInBlocks * 9);
    ItemHelper.addItem(player, new ItemStack(to, amountInBlocks));
    return true;
  }

  @Command(value = {"blocks", "bloki"}, description = "Przemienia wszystkie sztabki w EQ na bloki.")
  public void handleBlocks(final @Sender Player player) {
    boolean anyReplaced = false;
    for (final BlockEntry entry : ENTRY_SET) {
      anyReplaced |= replace(player, entry.from, entry.to);
    }

    if (!anyReplaced) {
      throw new BladeExitMessage(ChatHelper.colored(
          this.plugin.getPlatformConfiguration().messagesWrapper.blocksCannotBeConverted));
    }

    ChatHelper.sendMessage(player,
        this.plugin.getPlatformConfiguration().messagesWrapper.blocksSuccessfullyConverted);
  }

  private static final class BlockEntry {

    private final ItemStack from;
    private final Material to;

    private BlockEntry(final ItemStack from, final Material to) {
      this.from = from;
      this.to = to;
    }
  }
}
