package pl.rosehc.platform.inventory.player.other;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.adapter.helper.ItemHelper;
import pl.rosehc.adapter.helper.SerializeHelper;
import pl.rosehc.adapter.inventory.BukkitInventory;
import pl.rosehc.controller.packet.platform.safe.PlatformSafeContentsModifyPacket;
import pl.rosehc.platform.PlatformPlugin;
import pl.rosehc.platform.safe.Safe;

public final class PlayerSafeInventory {

  private final Player player;
  private final BukkitInventory inventory;

  public PlayerSafeInventory(final Player player, final Safe safe) {
    this.player = player;
    this.inventory = new BukkitInventory(ChatHelper.colored(PlatformPlugin.getInstance()
        .getPlatformConfiguration().customItemsWrapper.safeItemWrapper.inventoryName.replace(
            "{SAFE_UNIQUE_ID}", safe.getUniqueId().toString())
        .replace("{SAFE_OWNER_NICKNAME}", safe.getOwnerNickname())), 54);
    this.inventory.setCancellable(false);
    this.inventory.getInventory()
        .setContents(Objects.nonNull(safe.getContents()) ? safe.getContents() : new ItemStack[54]);
    this.inventory.setCloseAction(event -> {
      final List<ItemStack> reAddedSafeList = new ArrayList<>();
      final ItemStack[] items = event.getInventory().getContents();
      final String safeItemName = ChatHelper.colored(PlatformPlugin.getInstance()
          .getPlatformConfiguration().customItemsWrapper.safeItemWrapper.name);
      for (int i = 0; i < items.length; i++) {
        final ItemStack item = items[i];
        if (Objects.nonNull(item) && item.getType().equals(Material.CHEST) && item.hasItemMeta()
            && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName()
            .equals(safeItemName)) {
          final NBTTagCompound tag = CraftItemStack.asNMSCopy(item).getTag();
          if (Objects.nonNull(tag) && tag.hasKey("SafeUniqueId")) {
            reAddedSafeList.add(item);
            items[i] = null;
          }
        }
      }

      safe.setContents(items);
      ItemHelper.addItems(player, reAddedSafeList);
      PlatformPlugin.getInstance().getServer().getScheduler()
          .runTaskAsynchronously(PlatformPlugin.getInstance(),
              () -> PlatformPlugin.getInstance().getRedisAdapter().sendPacket(
                  new PlatformSafeContentsModifyPacket(safe.getUniqueId(),
                      SerializeHelper.serializeBukkitObjectToBytes(safe.getContents())),
                  "rhc_master_controller", "rhc_platform"));
    });
  }

  public void open() {
    if (this.player.isOnline()) {
      this.inventory.openInventory(this.player);
    }
  }
}
