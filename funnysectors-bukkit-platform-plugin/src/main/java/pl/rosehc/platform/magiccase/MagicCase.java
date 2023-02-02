package pl.rosehc.platform.magiccase;

import net.minecraft.server.v1_8_R3.EntityArmorStand;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

public final class MagicCase {

  private final Player player;
  private final EntityArmorStand armorStand;
  private final Location location;
  private final long leftTime;

  private BukkitTask updateTask;
  private ItemStack droppedItemStack;
  private boolean alreadyDropped;

  public MagicCase(final Player player, final EntityArmorStand armorStand, final Location location,
      final long leftTime) {
    this.player = player;
    this.armorStand = armorStand;
    this.location = location;
    this.leftTime = leftTime;
  }

  public Player getPlayer() {
    return this.player;
  }

  public EntityArmorStand getArmorStand() {
    return this.armorStand;
  }

  public Location getLocation() {
    return this.location;
  }

  public long getLeftTime() {
    return this.leftTime;
  }

  public BukkitTask getUpdateTask() {
    return this.updateTask;
  }

  public void setUpdateTask(final BukkitTask updateTask) {
    this.updateTask = updateTask;
  }

  public ItemStack getDroppedItemStack() {
    return this.droppedItemStack;
  }

  public void setDroppedItemStack(final ItemStack droppedItemStack) {
    this.droppedItemStack = droppedItemStack;
    this.alreadyDropped = true;
  }

  public boolean isAlreadyDropped() {
    return this.alreadyDropped;
  }
}
