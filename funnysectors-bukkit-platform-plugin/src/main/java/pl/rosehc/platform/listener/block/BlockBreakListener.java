package pl.rosehc.platform.listener.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import pl.rosehc.actionbar.PrioritizedActionBarConstants;
import pl.rosehc.actionbar.PrioritizedActionBarPlugin;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.adapter.helper.ItemHelper;
import pl.rosehc.platform.PlatformPlugin;
import pl.rosehc.platform.user.event.PlatformUserDropLevelUpEvent;
import pl.rosehc.platform.user.subdata.PlatformUserDropSettings;
import pl.rosehc.sectors.SectorsPlugin;
import pl.rosehc.sectors.sector.SectorType;

public final class BlockBreakListener implements Listener {

  private final PlatformPlugin plugin;

  public BlockBreakListener(final PlatformPlugin plugin) {
    this.plugin = plugin;
  }

  private static void recalculateDurability(final Player player, final ItemStack tool) {
    final short currentDurability = tool.getDurability(), maxDurability = tool.getType()
        .getMaxDurability();
    if (maxDurability > 0 && (!tool.hasItemMeta() || !tool.getItemMeta()
        .hasEnchant(Enchantment.DURABILITY)
        || (100D / (tool.getEnchantmentLevel(Enchantment.DURABILITY) + 1))
        >= ThreadLocalRandom.current().nextDouble(0D, 100D))) {
      if (currentDurability >= maxDurability) {
        player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1F, 1F);
        player.getInventory().setItemInHand(null);
        return;
      }

      tool.setDurability((short) (currentDurability + 1));
      player.updateInventory();
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onBreak(final BlockBreakEvent event) {
    final Block block = event.getBlock();
    final Location location = block.getLocation();
    final Material blockType = block.getType();
    if ((SectorsPlugin.getInstance().getSectorFactory().getCurrentSector().getType()
        .equals(SectorType.GAME) || SectorsPlugin.getInstance().getSectorFactory()
        .getCurrentSector().getType().equals(SectorType.END)) ||
        location.getBlockY() > this.plugin.getPlatformConfiguration().antiGriefSettingsWrapper.minY
            && this.plugin.getVanishingBlockFactory().isVanished(location, blockType)) {
      this.plugin.getVanishingBlockFactory().unVanish(location, blockType);
    }

    if (!blockType.equals(Material.STONE)) {
      return;
    }

    final Player player = event.getPlayer();
    final ItemStack tool = player.getItemInHand();
    if (Objects.isNull(tool) || !tool.getType().name().endsWith("PICKAXE")) {
      return;
    }

    if (block.getData() == 5) {
      if (tool.getType().equals(Material.GOLD_PICKAXE)) {
        block.setType(Material.AIR);
        ChatHelper.sendMessage(player,
            this.plugin.getPlatformConfiguration().messagesWrapper.stoneGeneratorHasBeenSuccessfullyDestroyed);
        ItemHelper.addItem(player,
            this.plugin.getPlatformConfiguration().customItemsWrapper.generatorItemWrapper.asItemStack());
        return;
      }

      this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
        block.setType(Material.STONE);
        block.setData((byte) 5);
      }, 30L);
    }

    this.plugin.getPlatformUserFactory().findUserByUniqueId(player.getUniqueId())
        .ifPresent(user -> {
          event.setCancelled(true);
          block.setType(Material.AIR);
          recalculateDurability(player, tool);
          final List<ItemStack> itemsToGiveList = new ArrayList<>();
          final PlatformUserDropSettings dropSettings = user.getDropSettings();
          this.plugin.getDropFactory().findRandomDrop(player, location, user)
              .ifPresent(drop -> {
                final ItemStack itemStack = drop.toItemStack(tool);
                final int currentXP = (int) (dropSettings.getCurrentXP() + drop.getExp());
                itemsToGiveList.add(itemStack);
                dropSettings.setCurrentXP(currentXP);
                player.giveExp(currentXP);
                PrioritizedActionBarPlugin.getInstance().getPrioritizedActionBarFactory()
                    .updateActionBar(player.getUniqueId(), ChatHelper.colored(
                            this.plugin.getPlatformConfiguration().messagesWrapper.dropItemDroppedActionBarInfo.replace(
                                    "{DROP_NAME}", drop.getName())
                                .replace("{AMOUNT}", String.valueOf(itemStack.getAmount()))
                                .replace("{XP}", String.format("%.2f", drop.getExp()))),
                        PrioritizedActionBarConstants.DROP_DROPPED_ITEM_INFO_ACTION_BAR_PRIORITY);
                if (dropSettings.getLevel() < 100
                    && dropSettings.getCurrentXP() >= dropSettings.getNeededXP()) {
                  final PlatformUserDropLevelUpEvent levelUpEvent = new PlatformUserDropLevelUpEvent(
                      user, dropSettings.getLevel(), dropSettings.getLevel() + 1);
                  this.plugin.getServer().getPluginManager().callEvent(levelUpEvent);
                  dropSettings.setNeededXP(
                      dropSettings.getLevel() + 1 < 100 ? dropSettings.getNeededXP() + (int) (
                          dropSettings.getNeededXP() * 0.1D) : 0);
                  dropSettings.setLevel(dropSettings.getLevel() + 1);
                  if (dropSettings.getLevel() >= 10) {
                    ChatHelper.sendTitle(player,
                        this.plugin.getPlatformConfiguration().messagesWrapper.youDidALevelUpTitle,
                        this.plugin.getPlatformConfiguration().messagesWrapper.youDidALevelUpSubTitle.replace(
                            "{LEVEL}", String.valueOf(dropSettings.getLevel())), 0, 20, 30);
                  }
                }
              });
          if (dropSettings.isCobbleStone()) {
            itemsToGiveList.add(new ItemStack(Material.COBBLESTONE));
          }

          ItemHelper.addItems(player, itemsToGiveList);
        });
  }
}
