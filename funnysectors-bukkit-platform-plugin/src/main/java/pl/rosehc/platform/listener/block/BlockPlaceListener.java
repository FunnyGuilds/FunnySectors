package pl.rosehc.platform.listener.block;

import java.util.Objects;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import pl.rosehc.actionbar.PrioritizedActionBarConstants;
import pl.rosehc.actionbar.PrioritizedActionBarPlugin;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.adapter.helper.ItemHelper;
import pl.rosehc.adapter.helper.NumberHelper;
import pl.rosehc.platform.PlatformConfiguration.AntiGriefSettingsWrapper;
import pl.rosehc.platform.PlatformPlugin;
import pl.rosehc.platform.cobblex.CobbleXItem;
import pl.rosehc.platform.vanishingblock.VanishingBlock;
import pl.rosehc.platform.vanishingblock.antigrief.AntiGriefBlockPlaceEvent;
import pl.rosehc.sectors.SectorsPlugin;
import pl.rosehc.sectors.sector.SectorType;

public final class BlockPlaceListener implements Listener {

  private final PlatformPlugin plugin;

  public BlockPlaceListener(final PlatformPlugin plugin) {
    this.plugin = plugin;
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlace(final BlockPlaceEvent event) {
    final Player player = event.getPlayer();
    final Block blockPlaced = event.getBlockPlaced();
    final ItemStack itemInHand = event.getItemInHand();
    if (Objects.nonNull(itemInHand) && itemInHand.isSimilar(
        this.plugin.getPlatformConfiguration().customItemsWrapper.cobbleXWrapper.asItemStack())) {
      event.setCancelled(true);
      ItemHelper.subtractItem(player, itemInHand);
      this.plugin.getCobbleXItemFactory().executeLocked(cobbleXItemList -> {
        final CobbleXItem cobbleXItem = !cobbleXItemList.isEmpty() ? cobbleXItemList.get(
            NumberHelper.range(0, cobbleXItemList.size())) : null;
        if (Objects.nonNull(cobbleXItem)) {
          ItemHelper.addItem(player, cobbleXItem.asItemStack());
        }
      });
      return;
    }

    if (Objects.nonNull(itemInHand) && itemInHand.isSimilar(
        this.plugin.getPlatformConfiguration().customItemsWrapper.generatorItemWrapper.asItemStack())) {
      ChatHelper.sendMessage(player,
          this.plugin.getPlatformConfiguration().messagesWrapper.stoneGeneratorHasBeenSuccessfullyCreated);
      return;
    }

    if (!SectorsPlugin.getInstance().getSectorFactory().getCurrentSector().getType()
        .equals(SectorType.GAME) && !SectorsPlugin.getInstance().getSectorFactory()
        .getCurrentSector().getType().equals(SectorType.END)) {
      return;
    }

    final AntiGriefSettingsWrapper antiGriefSettingsWrapper = this.plugin.getPlatformConfiguration().antiGriefSettingsWrapper;
    if (!player.isSneaking() && blockPlaced.getY() > antiGriefSettingsWrapper.minY
        && !antiGriefSettingsWrapper.ignoredAntiGriefBlockList.contains(
        blockPlaced.getType().name())) {
      final AntiGriefBlockPlaceEvent antiGriefBlockPlaceEvent = new AntiGriefBlockPlaceEvent(
          blockPlaced, player);
      this.plugin.getServer().getPluginManager().callEvent(antiGriefBlockPlaceEvent);
      if (antiGriefBlockPlaceEvent.isCancelled()) {
        return;
      }

      this.plugin.getVanishingBlockFactory().vanish(
          new VanishingBlock(blockPlaced.getLocation(), blockPlaced.getType(),
              System.currentTimeMillis() + antiGriefSettingsWrapper.parsedRemovalTime));
      PrioritizedActionBarPlugin.getInstance().getPrioritizedActionBarFactory()
          .updateActionBar(player.getUniqueId(), ChatHelper.colored(
                  this.plugin.getPlatformConfiguration().messagesWrapper.antiGriefBlockWillBeVanishedSoon),
              PrioritizedActionBarConstants.ANTI_GRIEF_ACTION_BAR_PRIORITY);
    }
  }
}
