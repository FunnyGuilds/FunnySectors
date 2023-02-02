package pl.rosehc.platform.listener.player;

import java.util.Objects;
import java.util.UUID;
import net.minecraft.server.v1_8_R3.EntityArmorStand;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.adapter.helper.ItemHelper;
import pl.rosehc.adapter.redis.callback.Callback;
import pl.rosehc.adapter.redis.callback.CallbackPacket;
import pl.rosehc.controller.ControllerPanicHelper;
import pl.rosehc.controller.packet.platform.safe.PlatformSafeLastOpenedTimeUpdatePacket;
import pl.rosehc.controller.packet.platform.safe.PlatformSafeModifierUpdatePacket;
import pl.rosehc.controller.packet.platform.safe.request.PlatformSafeModificationRequestPacket;
import pl.rosehc.controller.packet.platform.safe.request.PlatformSafeModificationResponsePacket;
import pl.rosehc.platform.PlatformConfiguration.CustomItemType;
import pl.rosehc.platform.PlatformPlugin;
import pl.rosehc.platform.inventory.player.other.PlayerCrowbarTakeoverInventory;
import pl.rosehc.platform.inventory.player.other.PlayerSafeInventory;
import pl.rosehc.platform.magiccase.MagicCase;
import pl.rosehc.platform.magiccase.MagicCaseUpdateTask;
import pl.rosehc.platform.user.event.PlatformUserUseCustomItemEvent;
import pl.rosehc.sectors.SectorsPlugin;
import pl.rosehc.sectors.sector.user.SectorUser;

public final class PlayerInteractListener implements Listener {

  private final PlatformPlugin plugin;

  public PlayerInteractListener(final PlatformPlugin plugin) {
    this.plugin = plugin;
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onInteract(final PlayerInteractEvent event) {
    if ((!event.getAction().equals(Action.RIGHT_CLICK_AIR) && !event.getAction()
        .equals(Action.RIGHT_CLICK_BLOCK)) || !event.hasItem()) {
      return;
    }

    if (this.plugin.getPlatformConfiguration().serverFreezeState) {
      return;
    }

    final Player player = event.getPlayer();
    final SectorUser user = SectorsPlugin.getInstance().getSectorUserFactory()
        .findUserByPlayer(player);
    if (Objects.isNull(user) || user.isRedirecting() || ControllerPanicHelper.isInPanic()) {
      return;
    }

    final ItemStack item = event.getItem();
    if (item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta()
        .getDisplayName().equals(ChatHelper.colored(
            this.plugin.getPlatformConfiguration().customItemsWrapper.safeItemWrapper.name))) {
      event.setCancelled(true);
      final NBTTagCompound tag = CraftItemStack.asNMSCopy(item).getTag();
      if (!Objects.isNull(tag) && tag.hasKey("SafeUniqueId")) {
        final UUID safeUniqueId = UUID.fromString(tag.getString("SafeUniqueId"));
        this.plugin.getSafeFactory().findSafe(safeUniqueId).ifPresent(safe -> {
          if (!player.hasPermission("platform-safe-bypass") && (
              !safe.getOwnerUniqueId().equals(player.getUniqueId()) || !safe.getOwnerNickname()
                  .equals(player.getName()))) {
            ChatHelper.sendMessage(player,
                this.plugin.getPlatformConfiguration().messagesWrapper.safeIsNotYours);
            return;
          }

          this.plugin.getRedisAdapter().sendPacket(
              new PlatformSafeModificationRequestPacket(safeUniqueId, player.getUniqueId(),
                  SectorsPlugin.getInstance().getSectorFactory().getCurrentSector().getName()),
              new Callback() {

                @Override
                public void done(final CallbackPacket packet) {
                  final PlatformSafeModificationResponsePacket responsePacket = (PlatformSafeModificationResponsePacket) packet;
                  if (player.isOnline()) {
                    if (Objects.nonNull(safe.getModifierUuid())) {
                      safe.setModifierUuid(null);
                      ChatHelper.sendMessage(player,
                          plugin.getPlatformConfiguration().messagesWrapper.safeIsBeingSaved);
                      plugin.getRedisAdapter()
                          .sendPacket(new PlatformSafeModifierUpdatePacket(safeUniqueId, null),
                              "rhc_master_controller");
                      return;
                    }

                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                      final int itemSlot = player.getInventory().first(item);
                      if (itemSlot == -1) {
                        safe.setModifierUuid(null);
                        ChatHelper.sendMessage(player,
                            plugin.getPlatformConfiguration().messagesWrapper.noSafeInInventory);
                        plugin.getRedisAdapter()
                            .sendPacket(new PlatformSafeModifierUpdatePacket(safeUniqueId, null),
                                "rhc_master_controller");
                        return;
                      }

                      safe.setLastOpenedTime(System.currentTimeMillis());
                      safe.setModifierUuid(player.getUniqueId());
                      player.getInventory().setItem(itemSlot,
                          plugin.getPlatformConfiguration().customItemsWrapper.safeItemWrapper.asItemStack(
                              safe));
                      plugin.getRedisAdapter().sendPacket(
                          new PlatformSafeLastOpenedTimeUpdatePacket(safeUniqueId,
                              safe.getLastOpenedTime()), "rhc_master_controller", "rhc_platform");
                      plugin.getRedisAdapter().sendPacket(
                          new PlatformSafeModifierUpdatePacket(safe.getUniqueId(),
                              player.getUniqueId()), "rhc_platform");
                      final PlayerSafeInventory inventory = new PlayerSafeInventory(player, safe);
                      inventory.open();
                    });
                  }
                }

                @Override
                public void error(final String message) {
                  if (player.isOnline()) {
                    ChatHelper.sendMessage(player, message);
                  }
                }
              }, "rhc_master_controller");
        });
      }
      return;
    }

    if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && item.isSimilar(
        this.plugin.getPlatformConfiguration().customItemsWrapper.magicCaseWrapper.asItemStack())) {
      if (this.plugin.getMagicCaseFactory().isCurrentlyOpeningMagicCase(player.getUniqueId())) {
        event.setCancelled(true);
        ChatHelper.sendMessage(player,
            this.plugin.getPlatformConfiguration().messagesWrapper.magicCaseIsAlreadyBeingOpened);
        return;
      }

      event.setCancelled(true);
      ItemHelper.subtractItem(player, item);
      final Location location = event.getClickedBlock().getLocation().clone().add(0.5D, 0D, 0.5D)
          .subtract(0D, 0.2D, 0D);
      final EntityArmorStand armorStand = new EntityArmorStand(
          ((CraftWorld) location.getWorld()).getHandle(), location.getX(), location.getY(),
          location.getZ());
      armorStand.setEquipment(4, CraftItemStack.asNMSCopy(item));
      armorStand.setInvisible(true);
      armorStand.setGravity(false);
      armorStand.setArms(false);
      armorStand.setCustomNameVisible(true);
      armorStand.setCustomName(ChatHelper.colored(
          this.plugin.getPlatformConfiguration().messagesWrapper.magicCaseItemLeftTimeInfo.replace(
              "{TIME}", "Ładowanie...")));
      ((CraftPlayer) player).getHandle().playerConnection.sendPacket(
          new PacketPlayOutSpawnEntityLiving(armorStand));
      ((CraftPlayer) player).getHandle().playerConnection.sendPacket(
          new PacketPlayOutEntityEquipment(armorStand.getId(), 4, armorStand.getEquipment(4)));
      final MagicCase magicCase = new MagicCase(
          event.getPlayer(),
          armorStand,
          location,
          System.currentTimeMillis() + 4500L
      );
      magicCase.setUpdateTask(this.plugin.getServer().getScheduler()
          .runTaskTimer(this.plugin, new MagicCaseUpdateTask(this.plugin, magicCase), 1L, 1L));
      this.plugin.getMagicCaseFactory().addMagicCase(event.getPlayer().getUniqueId(), magicCase);
      return;
    }

    if (item.isSimilar(
        this.plugin.getPlatformConfiguration().customItemsWrapper.goldenHeadWrapper.asItemStack())) {
      event.setCancelled(true);
      ItemHelper.subtractItem(player, item);
      this.plugin.getServer().getPluginManager()
          .callEvent(new PlatformUserUseCustomItemEvent(player, CustomItemType.GHEAD));
      this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin,
          () -> this.plugin.getPlatformConfiguration().customItemsWrapper.goldenHeadWrapper.potionEffectWrapperList.forEach(
              wrapper -> player.addPotionEffect(wrapper.asPotionEffect(), true)));
      return;
    }

    if (item.isSimilar(
        this.plugin.getPlatformConfiguration().customItemsWrapper.crowbarWrapper.asItemStack())) {
      event.setCancelled(true);
      ItemHelper.subtractItem(player, item);
      final PlayerCrowbarTakeoverInventory inventory = new PlayerCrowbarTakeoverInventory(player);
      inventory.open();
    }
  }

//  @EventHandler
//  public void onInteract(final PlayerInteractEvent event) {
//    if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && event.hasItem() && event.getItem()
//        .getType().equals(Material.SKULL_ITEM)) {
//      final Location location = event.getClickedBlock().getLocation().clone();
//      location.setX((double) location.getBlockX() + 0.5D);
//      location.setY((double) location.getBlockY() + 0.5D);
//      location.setZ((double) location.getBlockZ() + 0.5D);
//      final ArmorStand armorStand = (ArmorStand) location.getWorld()
//          .spawnEntity(location, EntityType.ARMOR_STAND);
//      armorStand.setHelmet(event.getItem());
//      armorStand.setVisible(false);
//      armorStand.setCustomNameVisible(true);
//      armorStand.setGravity(false);
//      armorStand.setArms(false);
//      armorStand.setCustomName(ChatHelper.colored(
//          this.plugin.getPlatformConfiguration().messagesWrapper.magicCaseItemLeftTimeInfo.replace(
//              "{TIME}", "Ładowanie...")));
//      final MagicCase magicCase = new MagicCase(
//          event.getPlayer(),
//          armorStand,
//          System.currentTimeMillis() + 3500L
//      );
//      magicCase.setUpdateTask(Bukkit.getScheduler()
//          .runTaskTimer(this.plugin, new MagicCaseUpdateTask(this.plugin, magicCase), 1L, 1L));
//      this.plugin.getMagicCaseFactory().addMagicCase(event.getPlayer().getUniqueId(), magicCase);
//      event.setCancelled(true);
//    }
//  }
}
