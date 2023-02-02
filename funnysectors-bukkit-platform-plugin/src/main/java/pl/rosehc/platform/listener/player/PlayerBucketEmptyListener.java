package pl.rosehc.platform.listener.player;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.platform.PlatformPlugin;
import pl.rosehc.platform.end.EndHelper;
import pl.rosehc.platform.vanishingblock.VanishingBlock;
import pl.rosehc.sectors.SectorsPlugin;
import pl.rosehc.sectors.sector.SectorType;

public final class PlayerBucketEmptyListener implements Listener {

  private final PlatformPlugin plugin;

  public PlayerBucketEmptyListener(final PlatformPlugin plugin) {
    this.plugin = plugin;
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onEmpty(final PlayerBucketEmptyEvent event) {
    final Player player = event.getPlayer();
    if (SectorsPlugin.getInstance().getSectorFactory().getCurrentSector().getType()
        .equals(SectorType.END) && event.getBucket().equals(Material.WATER_BUCKET)) {
      EndHelper.disableWaterSkill(player);
      ChatHelper.sendMessage(player,
          this.plugin.getPlatformConfiguration().messagesWrapper.endWaterWillBeRemovedSoon);
      this.plugin.getVanishingBlockFactory().vanish(new VanishingBlock(
          event.getBlockClicked().getRelative(event.getBlockFace()).getLocation(),
          Material.STATIONARY_WATER, System.currentTimeMillis() + 10_000L));
    }
  }
}
