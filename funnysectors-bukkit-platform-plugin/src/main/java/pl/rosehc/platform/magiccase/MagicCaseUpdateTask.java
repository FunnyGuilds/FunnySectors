package pl.rosehc.platform.magiccase;

import net.minecraft.server.v1_8_R3.EntityArmorStand;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedSoundEffect;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.CraftSound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.adapter.helper.ItemHelper;
import pl.rosehc.adapter.helper.TimeHelper;
import pl.rosehc.platform.PlatformPlugin;

public final class MagicCaseUpdateTask implements Runnable {

  private final PlatformPlugin plugin;
  private final MagicCase magicCase;
  private float yaw;

  public MagicCaseUpdateTask(final PlatformPlugin plugin, final MagicCase magicCase) {
    this.plugin = plugin;
    this.magicCase = magicCase;
  }

  @Override
  public void run() {
    final Player player = this.magicCase.getPlayer();
    if (!player.isOnline()) {
      this.plugin.getMagicCaseFactory().removeMagicCase(player.getUniqueId());
      return;
    }

    final EntityArmorStand armorStand = this.magicCase.getArmorStand();
    final Location location = this.magicCase.getLocation();
    final PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;
    if (this.magicCase.getLeftTime() <= System.currentTimeMillis()) {
      playerConnection.sendPacket(new PacketPlayOutEntityDestroy(armorStand.getId()));
      playerConnection.sendPacket(
          new PacketPlayOutNamedSoundEffect(CraftSound.getSound(Sound.ITEM_PICKUP), location.getX(),
              player.getLocation().getY(), location.getZ(), 1F, 0.5F));
      for (int i = 0; i < 50; i++) {
        playerConnection.sendPacket(
            new PacketPlayOutWorldParticles(EnumParticle.EXPLOSION_NORMAL, true,
                (float) location.getX(), (float) (location.getY() + 2D), (float) location.getZ(),
                0F, 0F, 0F, 1F, 0));
      }

      this.plugin.getMagicCaseFactory().removeMagicCase(player.getUniqueId());
      this.plugin.getServer().getScheduler()
          .scheduleSyncDelayedTask(PlatformPlugin.getInstance(), () -> {
            final ItemStack droppedItemStack = this.magicCase.getDroppedItemStack();
            if (droppedItemStack != null) {
              ItemHelper.addItem(player, droppedItemStack);
            }
          });
      return;
    }

    if (!this.magicCase.isAlreadyDropped()) {
      if (this.magicCase.getLeftTime() - System.currentTimeMillis() <= 1100L) {
        final MagicCaseItem magicCaseItem = this.plugin.getMagicCaseFactory()
            .findRandomMagicCaseItem();
        final ItemStack droppedItemStack =
            magicCaseItem != null ? magicCaseItem.asItemStack() : null;
        this.magicCase.setDroppedItemStack(droppedItemStack);
        armorStand.setCustomName(ChatHelper.colored(droppedItemStack != null
            ? this.plugin.getPlatformConfiguration().messagesWrapper.magicCaseItemDroppedInfo.replace(
                "{ITEM_NAME}",
                droppedItemStack.hasItemMeta() && droppedItemStack.getItemMeta().hasDisplayName()
                    ? droppedItemStack.getItemMeta().getDisplayName()
                    : droppedItemStack.getType().name())
            .replace("{ITEM_AMOUNT}", String.valueOf(droppedItemStack.getAmount()))
            : this.plugin.getPlatformConfiguration().messagesWrapper.magicCaseItemNotDroppedInfo));
        playerConnection.sendPacket(
            new PacketPlayOutEntityMetadata(armorStand.getId(), armorStand.getDataWatcher(),
                false));
        return;
      }

      armorStand.setCustomName(ChatHelper.colored(
          this.plugin.getPlatformConfiguration().messagesWrapper.magicCaseItemLeftTimeInfo.replace(
              "{TIME}",
              TimeHelper.timeToString(this.magicCase.getLeftTime() - System.currentTimeMillis()))));
    }

    armorStand.locX = location.getX();
    armorStand.locY = location.getY();
    armorStand.locZ = location.getZ();
    armorStand.yaw = this.yaw % 360F;
    armorStand.pitch = location.getPitch();
    this.yaw += 5.2D;
    playerConnection.sendPacket(new PacketPlayOutEntityTeleport(armorStand));
    playerConnection.sendPacket(
        new PacketPlayOutEntityMetadata(armorStand.getId(), armorStand.getDataWatcher(), false));
    playerConnection.sendPacket(
        new PacketPlayOutEntityEquipment(armorStand.getId(), 4, armorStand.getEquipment(4)));
  }
}
