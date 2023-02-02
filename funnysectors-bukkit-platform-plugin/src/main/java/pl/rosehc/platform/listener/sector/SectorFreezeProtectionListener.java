package pl.rosehc.platform.listener.sector;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.spigotmc.event.entity.EntityDismountEvent;
import pl.rosehc.platform.PlatformPlugin;
import pl.rosehc.platform.user.PlatformUserFreezeEntityHelper;

public final class SectorFreezeProtectionListener implements Listener {

  private final PlatformPlugin plugin;

  public SectorFreezeProtectionListener(final PlatformPlugin plugin) {
    this.plugin = plugin;
  }

  @EventHandler
  public void onDismount(final EntityDismountEvent event) {
    if (event.getDismounted().getType().equals(EntityType.HORSE)
        && this.plugin.getPlatformConfiguration().serverFreezeState) {
      if (event.getEntity() instanceof Player && event.getEntity()
          .hasPermission("platform-freeze-bypass")) {
        PlatformUserFreezeEntityHelper.removeAndDisMountEntity((Player) event.getEntity());
        return;
      }

      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onJoin(final PlayerJoinEvent event) {
    if (this.plugin.getPlatformConfiguration().serverFreezeState) {
      this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin,
          () -> PlatformUserFreezeEntityHelper.spawnAndMountEntity(event.getPlayer()), 5L);
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onDamage(final EntityDamageEvent event) {
    if (this.plugin.getPlatformConfiguration().serverFreezeState) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onDamageByEntity(final EntityDamageByEntityEvent event) {
    if (this.plugin.getPlatformConfiguration().serverFreezeState) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onFoodChange(final FoodLevelChangeEvent event) {
    if (this.plugin.getPlatformConfiguration().serverFreezeState) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onBlockBreak(final BlockBreakEvent event) {
    if (this.plugin.getPlatformConfiguration().serverFreezeState && !event.getPlayer()
        .hasPermission("platform-freeze-bypass")) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onBlockPlace(final BlockPlaceEvent event) {
    if (this.plugin.getPlatformConfiguration().serverFreezeState && !event.getPlayer()
        .hasPermission("platform-freeze-bypass")) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onFill(final PlayerBucketFillEvent event) {
    if (this.plugin.getPlatformConfiguration().serverFreezeState && !event.getPlayer()
        .hasPermission("platform-freeze-bypass")) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onEmpty(final PlayerBucketEmptyEvent event) {
    if (this.plugin.getPlatformConfiguration().serverFreezeState && !event.getPlayer()
        .hasPermission("platform-freeze-bypass")) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onDrop(final PlayerDropItemEvent event) {
    if (this.plugin.getPlatformConfiguration().serverFreezeState && !event.getPlayer()
        .hasPermission("platform-freeze-bypass")) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onPickupItem(final PlayerPickupItemEvent event) {
    if (this.plugin.getPlatformConfiguration().serverFreezeState) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onCommandPreprocess(final PlayerCommandPreprocessEvent event) {
    if (this.plugin.getPlatformConfiguration().serverFreezeState && !event.getPlayer()
        .hasPermission("platform-freeze-bypass")) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onOpen(final InventoryOpenEvent event) {
    if (this.plugin.getPlatformConfiguration().serverFreezeState && !event.getPlayer()
        .hasPermission("platform-freeze-bypass")) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onChat(final AsyncPlayerChatEvent event) {
    if (this.plugin.getPlatformConfiguration().serverFreezeState && !event.getPlayer()
        .hasPermission("platform-freeze-bypass")) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onQuit(final PlayerQuitEvent event) {
    PlatformUserFreezeEntityHelper.removeAndDisMountEntity(event.getPlayer());
  }
}
