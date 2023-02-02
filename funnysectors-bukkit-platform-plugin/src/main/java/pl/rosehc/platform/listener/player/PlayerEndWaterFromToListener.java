package pl.rosehc.platform.listener.player;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import pl.rosehc.platform.PlatformPlugin;

public final class PlayerEndWaterFromToListener implements Listener {

  private final PlatformPlugin plugin;

  public PlayerEndWaterFromToListener(final PlatformPlugin plugin) {
    this.plugin = plugin;
  }

  @EventHandler
  public void onFromTo(final BlockFromToEvent event) {
    final Block fromBlock = event.getBlock();
    if ((fromBlock.getType().equals(Material.STATIONARY_WATER) || fromBlock.getType()
        .equals(Material.WATER)) && this.plugin.getVanishingBlockFactory()
        .isVanished(fromBlock.getLocation(), Material.STATIONARY_WATER)) {
      event.setCancelled(true);
      return;
    }

    final Block toBlock = event.getToBlock();
    if ((toBlock.getType().equals(Material.STATIONARY_WATER) || toBlock.getType()
        .equals(Material.WATER)) && this.plugin.getVanishingBlockFactory()
        .isVanished(toBlock.getLocation(), Material.STATIONARY_WATER)) {
      event.setCancelled(true);
    }
  }
}
