package pl.rosehc.platform.listener.player;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.adapter.helper.ItemHelper;
import pl.rosehc.platform.PlatformPlugin;
import pl.rosehc.platform.end.EndHelper;
import pl.rosehc.sectors.SectorsPlugin;
import pl.rosehc.sectors.sector.SectorType;

public final class PlayerProjectileLaunchListener implements Listener {

  @SuppressWarnings("SpellCheckingInspection")
  private static final ItemStack ENDER_PEARL_ITEM = new ItemStack(Material.ENDER_PEARL);
  private final PlatformPlugin plugin;

  public PlayerProjectileLaunchListener(final PlatformPlugin plugin) {
    this.plugin = plugin;
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onLaunch(final ProjectileLaunchEvent event) {
    if (event.getEntityType().equals(EntityType.ENDER_PEARL) && SectorsPlugin.getInstance()
        .getSectorFactory().getCurrentSector().getType().equals(SectorType.END) && event.getEntity()
        .getShooter() instanceof Player) {
      final Player player = (Player) event.getEntity().getShooter();
      if (EndHelper.canWaterSkill(player) && player.getLocation().getBlock().getType()
          .equals(Material.AIR)) {
        event.setCancelled(true);
        ChatHelper.sendMessage(player,
            this.plugin.getPlatformConfiguration().messagesWrapper.cannotUsePearlsOnEndShockwave);
        this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin,
            () -> ItemHelper.addItem(player, new ItemStack(Material.ENDER_PEARL)));
      }
    }
  }
}
