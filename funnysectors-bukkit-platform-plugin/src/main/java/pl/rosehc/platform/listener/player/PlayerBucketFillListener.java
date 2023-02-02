package pl.rosehc.platform.listener.player;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketFillEvent;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.platform.PlatformPlugin;
import pl.rosehc.sectors.SectorsPlugin;
import pl.rosehc.sectors.sector.SectorType;

public final class PlayerBucketFillListener implements Listener {

  private final PlatformPlugin plugin;

  public PlayerBucketFillListener(final PlatformPlugin plugin) {
    this.plugin = plugin;
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onFill(final PlayerBucketFillEvent event) {
    final Player player = event.getPlayer();
    if (SectorsPlugin.getInstance().getSectorFactory().getCurrentSector().getType()
        .equals(SectorType.END) && this.plugin.getVanishingBlockFactory()
        .unVanish(event.getBlockClicked().getLocation(), Material.STATIONARY_WATER)) {
      ChatHelper.sendMessage(player,
          this.plugin.getPlatformConfiguration().messagesWrapper.endWaterHasBeenSuccessfullyFilled);
    }
  }
}
