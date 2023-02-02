package pl.rosehc.sectors.listener;

import java.util.Objects;
import java.util.Optional;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftHorse;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.projectiles.ProjectileSource;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.controller.ControllerPanicHelper;
import pl.rosehc.sectors.SectorsPlugin;
import pl.rosehc.sectors.helper.SectorHelper;
import pl.rosehc.sectors.sector.SectorType;
import pl.rosehc.sectors.sector.user.SectorUser;

public final class SectorProtectionListener implements Listener {

  private final SectorsPlugin plugin;

  public SectorProtectionListener(final SectorsPlugin plugin) {
    this.plugin = plugin;
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onBreak(final BlockBreakEvent event) {
    final Player player = event.getPlayer();
    final SectorUser user = this.plugin.getSectorUserFactory().findUserByPlayer(player);
    if (Objects.isNull(user) || user.isRedirecting() || ControllerPanicHelper.isInPanic()) {
      event.setCancelled(true);
      return;
    }

    if (!player.isOp()) {
      if (this.plugin.getSectorFactory().getCurrentSector().getType().equals(SectorType.SPAWN)
          || this.plugin.getSectorFactory().getCurrentSector().getType().equals(SectorType.END)) {
        event.setCancelled(true);
        ChatHelper.sendMessage(player,
            this.plugin.getSectorFactory().getCurrentSector().getType().equals(SectorType.SPAWN)
                ? this.plugin.getSectorsConfiguration().messagesWrapper.cannotBreakBlocksOnSpawn
                : this.plugin.getSectorsConfiguration().messagesWrapper.cannotBreakBlocksInEnd);
        return;
      }

      if (SectorHelper.isNearSector(event.getBlock().getLocation(), 20)) {
        event.setCancelled(true);
        ChatHelper.sendMessage(player,
            this.plugin.getSectorsConfiguration().messagesWrapper.cannotBreakBlocksNearSector);
      }
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlace(final BlockPlaceEvent event) {
    final Player player = event.getPlayer();
    final SectorUser user = this.plugin.getSectorUserFactory().findUserByPlayer(player);
    if (Objects.isNull(user) || user.isRedirecting() || ControllerPanicHelper.isInPanic()) {
      event.setCancelled(true);
      return;
    }

    if (!player.isOp()) {
      if (this.plugin.getSectorFactory().getCurrentSector().getType().equals(SectorType.SPAWN)
          || this.plugin.getSectorFactory().getCurrentSector().getType().equals(SectorType.END)) {
        event.setCancelled(true);
        ChatHelper.sendMessage(player,
            this.plugin.getSectorFactory().getCurrentSector().getType().equals(SectorType.SPAWN)
                ? this.plugin.getSectorsConfiguration().messagesWrapper.cannotPlaceBlocksOnSpawn
                : this.plugin.getSectorsConfiguration().messagesWrapper.cannotPlaceBlocksInEnd);
        return;
      }

      if (SectorHelper.isNearSector(event.getBlock().getLocation(), 20)) {
        event.setCancelled(true);
        ChatHelper.sendMessage(player,
            this.plugin.getSectorsConfiguration().messagesWrapper.cannotPlaceBlocksNearSector);
      }
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onFill(final PlayerBucketFillEvent event) {
    final Player player = event.getPlayer();
    final SectorUser user = this.plugin.getSectorUserFactory().findUserByPlayer(player);
    if (Objects.isNull(user) || user.isRedirecting() || ControllerPanicHelper.isInPanic()) {
      event.setCancelled(true);
      return;
    }

    if (!player.isOp()) {
      if (this.plugin.getSectorFactory().getCurrentSector().getType().equals(SectorType.SPAWN)) {
        event.setCancelled(true);
        ChatHelper.sendMessage(player,
            this.plugin.getSectorsConfiguration().messagesWrapper.cannotFillTheBucketOnSpawn);
        return;
      }

      if (SectorHelper.isNearSector(event.getBlockClicked().getLocation(), 8)) {
        event.setCancelled(true);
        ChatHelper.sendMessage(player,
            this.plugin.getSectorsConfiguration().messagesWrapper.cannotFillTheBucketNearSector);
      }
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEmpty(final PlayerBucketEmptyEvent event) {
    final Player player = event.getPlayer();
    final SectorUser user = this.plugin.getSectorUserFactory().findUserByPlayer(player);
    if (Objects.isNull(user) || user.isRedirecting() || ControllerPanicHelper.isInPanic()) {
      event.setCancelled(true);
      return;
    }

    if (!player.isOp()) {
      if (this.plugin.getSectorFactory().getCurrentSector().getType().equals(SectorType.SPAWN)) {
        event.setCancelled(true);
        ChatHelper.sendMessage(player,
            this.plugin.getSectorsConfiguration().messagesWrapper.cannotEmptyTheBucketOnSpawn);
        return;
      }

      if (SectorHelper.isNearSector(event.getBlockClicked().getLocation(), 8)) {
        event.setCancelled(true);
        ChatHelper.sendMessage(player,
            this.plugin.getSectorsConfiguration().messagesWrapper.cannotEmptyTheBucketNearSector);
      }
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEntityDamageByEntity(final EntityDamageByEntityEvent event) {
    if (event.getEntity() instanceof Player) {
      this.findAttacker(event).ifPresent(attacker -> {
        final SectorUser userOne = this.plugin.getSectorUserFactory().findUserByPlayer(attacker);
        final SectorUser userTwo = this.plugin.getSectorUserFactory()
            .findUserByPlayer((Player) event.getEntity());
        if ((Objects.isNull(userOne) || userOne.isRedirecting()) || (Objects.isNull(userTwo)
            || userTwo.isRedirecting()) || ControllerPanicHelper.isInPanic()) {
          event.setCancelled(true);
        }
      });
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onChange(final FoodLevelChangeEvent event) {
    if (!(event.getEntity() instanceof Player) || !this.plugin.getSectorFactory().getCurrentSector()
        .getType().equals(SectorType.SPAWN)) {
      return;
    }

    final Player player = (Player) event.getEntity();
    if (Objects.isNull(player.getItemInHand()) || !player.getItemInHand().getType().isEdible()) {
      event.setCancelled(true);
      player.setFoodLevel(20);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onDamage(final EntityDamageEvent event) {
    if (event.getEntity() instanceof Player) {
      final SectorUser user = this.plugin.getSectorUserFactory()
          .findUserByPlayer((Player) event.getEntity());
      if ((Objects.isNull(user) || user.isRedirecting()) || ControllerPanicHelper.isInPanic()
          || this.plugin.getSectorFactory().getCurrentSector().getType().equals(SectorType.SPAWN)) {
        event.setCancelled(true);
      }
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onCreate(final VehicleCreateEvent event) {
    final Vehicle vehicle = event.getVehicle();
    if (vehicle instanceof Horse && ((CraftHorse) vehicle).getHandle().getVariant() == -1) {
      return;
    }

    if (SectorHelper.isNearSector(vehicle.getLocation(), 20)) {
      this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, vehicle::remove);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onMove(final VehicleMoveEvent event) {
    final Vehicle vehicle = event.getVehicle();
    if (vehicle instanceof Horse && ((CraftHorse) vehicle).getHandle().getVariant() == -1) {
      return;
    }

    if (SectorHelper.isNearSector(vehicle.getLocation(), 20)) {
      this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, vehicle::remove);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEnter(final VehicleEnterEvent event) {
    final Vehicle vehicle = event.getVehicle();
    if (vehicle instanceof Horse && ((CraftHorse) vehicle).getHandle().getVariant() == -1) {
      return;
    }

    if (SectorHelper.isNearSector(vehicle.getLocation(), 20)) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onLaunch(final ProjectileLaunchEvent event) {
    if (event.getEntity().getShooter() instanceof Player) {
      final SectorUser user = this.plugin.getSectorUserFactory()
          .findUserByPlayer((Player) event.getEntity().getShooter());
      if (Objects.isNull(user) || user.isRedirecting() || ControllerPanicHelper.isInPanic()) {
        event.setCancelled(true);
      }
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onDrop(final PlayerDropItemEvent event) {
    final SectorUser user = this.plugin.getSectorUserFactory().findUserByPlayer(event.getPlayer());
    if (Objects.isNull(user) || user.isRedirecting() || this.plugin.getSectorFactory()
        .getCurrentSector().getType().equals(SectorType.SPAWN) || SectorHelper.isNearSector(
        event.getPlayer().getLocation(), 20) || ControllerPanicHelper.isInPanic()) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPickup(final PlayerPickupItemEvent event) {
    final SectorUser user = this.plugin.getSectorUserFactory().findUserByPlayer(event.getPlayer());
    if (Objects.isNull(user) || user.isRedirecting() || this.plugin.getSectorFactory()
        .getCurrentSector().getType().equals(SectorType.SPAWN)
        || ControllerPanicHelper.isInPanic()) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onInteract(final PlayerInteractEvent event) {
    final SectorUser user = this.plugin.getSectorUserFactory().findUserByPlayer(event.getPlayer());
    if (Objects.isNull(user) || user.isRedirecting() || ControllerPanicHelper.isInPanic()) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onInteractEntity(final PlayerInteractEntityEvent event) {
    final SectorUser user = this.plugin.getSectorUserFactory().findUserByPlayer(event.getPlayer());
    if (Objects.isNull(user) || user.isRedirecting() || ControllerPanicHelper.isInPanic()) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onInteractAtEntity(final PlayerInteractAtEntityEvent event) {
    final SectorUser user = this.plugin.getSectorUserFactory().findUserByPlayer(event.getPlayer());
    if (Objects.isNull(user) || user.isRedirecting() || ControllerPanicHelper.isInPanic()) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onConsume(final PlayerItemConsumeEvent event) {
    final SectorUser user = this.plugin.getSectorUserFactory().findUserByPlayer(event.getPlayer());
    if (Objects.isNull(user) || user.isRedirecting() || ControllerPanicHelper.isInPanic()) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onCommand(final PlayerCommandPreprocessEvent event) {
    final SectorUser user = this.plugin.getSectorUserFactory().findUserByPlayer(event.getPlayer());
    if (Objects.isNull(user) || user.isRedirecting() || ControllerPanicHelper.isInPanic()) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onManipulate(final PlayerArmorStandManipulateEvent event) {
    final SectorUser user = this.plugin.getSectorUserFactory().findUserByPlayer(event.getPlayer());
    if (Objects.isNull(user) || user.isRedirecting() || ControllerPanicHelper.isInPanic()) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onOpen(final InventoryOpenEvent event) {
    final SectorUser user = this.plugin.getSectorUserFactory()
        .findUserByPlayer((Player) event.getPlayer());
    if (Objects.isNull(user) || user.isRedirecting() || ControllerPanicHelper.isInPanic()) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onClick(final InventoryClickEvent event) {
    final SectorUser user = this.plugin.getSectorUserFactory()
        .findUserByPlayer((Player) event.getWhoClicked());
    if (Objects.isNull(user) || user.isRedirecting() || ControllerPanicHelper.isInPanic()) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onDrag(final InventoryDragEvent event) {
    final SectorUser user = this.plugin.getSectorUserFactory()
        .findUserByPlayer((Player) event.getWhoClicked());
    if (Objects.isNull(user) || user.isRedirecting() || ControllerPanicHelper.isInPanic()) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onInteract(final InventoryInteractEvent event) {
    final SectorUser user = this.plugin.getSectorUserFactory()
        .findUserByPlayer((Player) event.getWhoClicked());
    if (Objects.isNull(user) || user.isRedirecting() || ControllerPanicHelper.isInPanic()) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onExplode(final EntityExplodeEvent event) {
    if (!this.plugin.getSectorFactory().getCurrentSector().getType().equals(SectorType.SPAWN)) {
      event.blockList().removeIf(block -> SectorHelper.isNearSector(block.getLocation(), 51));
    } else {
      event.blockList().clear();
    }
  }

  private Optional<Player> findAttacker(EntityDamageByEntityEvent event) {
    final Entity attacker = event.getDamager();
    if (attacker instanceof Projectile) {
      final ProjectileSource shooter = ((Projectile) attacker).getShooter();
      return Optional.of(shooter).filter(Player.class::isInstance).map(Player.class::cast);
    }

    return Optional.of(attacker).filter(Player.class::isInstance).map(Player.class::cast);
  }
}
