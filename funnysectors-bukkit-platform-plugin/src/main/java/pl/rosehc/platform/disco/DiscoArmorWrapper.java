package pl.rosehc.platform.disco;

import java.util.Arrays;
import java.util.List;
import net.minecraft.server.v1_8_R3.ItemStack;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityEquipment;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

public final class DiscoArmorWrapper {

  private final ItemStack itemStack;
  private final int slotID;

  private DiscoArmorWrapper(final ItemStack itemStack, final int slotID) {
    this.itemStack = itemStack;
    this.slotID = slotID;
  }

  public static List<DiscoArmorWrapper> wrap(final PlayerInventory inventory) {
    return Arrays.asList(wrap(inventory.getHelmet(), 3), wrap(inventory.getChestplate(), 2),
        wrap(inventory.getLeggings(), 1), wrap(inventory.getBoots(), 0));
  }

  public static DiscoArmorWrapper wrap(final org.bukkit.inventory.ItemStack itemStack,
      final int slotID) {
    return new DiscoArmorWrapper(CraftItemStack.asNMSCopy(itemStack), slotID);
  }

  public PacketPlayOutEntityEquipment unwrap(final Player player, final int slot) {
    return new PacketPlayOutEntityEquipment(player.getEntityId(),
        slot != 0 ? slot - this.slotID : this.slotID, this.itemStack);
  }
}
