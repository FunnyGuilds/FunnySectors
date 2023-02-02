package pl.rosehc.platform.inventory.player.other;

import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import me.vaperion.blade.exception.BladeExitMessage;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.adapter.helper.ItemHelper;
import pl.rosehc.adapter.inventory.BukkitInventory;
import pl.rosehc.adapter.inventory.BukkitInventoryElement;
import pl.rosehc.controller.packet.platform.safe.PlatformSafeOwnerUpdatePacket;
import pl.rosehc.controller.wrapper.platform.gui.other.PlatformCrowbarTakeoverSpigotGuiElementWrapper;
import pl.rosehc.controller.wrapper.spigot.SpigotGuiElementWrapper;
import pl.rosehc.controller.wrapper.spigot.SpigotGuiWrapper;
import pl.rosehc.platform.PlatformPlugin;
import pl.rosehc.platform.safe.Safe;
import pl.rosehc.platform.user.PlatformUser;

public final class PlayerCrowbarTakeoverInventory {

  private final Player player;
  private final BukkitInventory inventory;

  public PlayerCrowbarTakeoverInventory(final Player player) {
    this.player = player;
    final SpigotGuiWrapper takeoverGuiWrapper = PlatformPlugin.getInstance()
        .getPlatformConfiguration().inventoryMap.get("crowbar_takeover");
    if (takeoverGuiWrapper == null) {
      ChatHelper.sendMessage(player,
          PlatformPlugin.getInstance().getPlatformConfiguration().messagesWrapper.guiNotFound);
      throw new UnsupportedOperationException("Takeover gui not configured.");
    }

    final SpigotGuiElementWrapper takeoverElement = takeoverGuiWrapper.elements.get("takeover");
    if (!(takeoverElement instanceof PlatformCrowbarTakeoverSpigotGuiElementWrapper)) {
      ChatHelper.sendMessage(player,
          PlatformPlugin.getInstance().getPlatformConfiguration().messagesWrapper.guiNotFound);
      throw new UnsupportedOperationException("Takeover element not configured.");
    }

    this.inventory = new BukkitInventory(ChatHelper.colored(takeoverGuiWrapper.inventoryName),
        takeoverGuiWrapper.inventorySize);
    final PlatformUser user = PlatformPlugin.getInstance().getPlatformUserFactory()
        .findUserByUniqueId(player.getUniqueId()).orElseThrow(() -> new BladeExitMessage(
            ChatHelper.colored(PlatformPlugin.getInstance()
                .getPlatformConfiguration().messagesWrapper.playerNotFound.replace("{PLAYER_NAME}",
                    player.getName()))));
    final SpigotGuiElementWrapper fillElement = takeoverGuiWrapper.fillElement;
    if (fillElement != null) {
      final ItemStack itemStack = fillElement.asItemStack();
      for (int i = 0; i < this.inventory.getInventory().getSize(); i++) {
        this.inventory.setElement(i,
            new BukkitInventoryElement(itemStack, event -> event.setCancelled(true)));
      }
    }

    for (final Entry<String, SpigotGuiElementWrapper> entry : takeoverGuiWrapper.elements.entrySet()) {
      if (!(entry.getValue() instanceof PlatformCrowbarTakeoverSpigotGuiElementWrapper)) {
        this.inventory.setElement(entry.getValue().slot,
            new BukkitInventoryElement(entry.getValue().asItemStack(),
                event -> event.setCancelled(true)));
      }
    }

    this.inventory.setCancellable(false);
    this.inventory.setElement(takeoverElement.slot,
        new BukkitInventoryElement(new ItemStack(Material.AIR), event -> {
          ItemStack currentItem = event.getInventory().getItem(takeoverElement.slot);
          if (Objects.isNull(currentItem)) {
            currentItem = event.getCurrentItem();
          }

          event.setCancelled(Objects.isNull(currentItem));
          if (!event.isCancelled() && (currentItem.hasItemMeta() && currentItem.getItemMeta()
              .hasDisplayName() && currentItem.getItemMeta().getDisplayName().equals(
              ChatHelper.colored(PlatformPlugin.getInstance()
                  .getPlatformConfiguration().customItemsWrapper.safeItemWrapper.name)))) {
            final NBTTagCompound tag = CraftItemStack.asNMSCopy(currentItem).getTag();
            if (Objects.isNull(tag) || !tag.hasKey("SafeUniqueId")) {
              ChatHelper.sendMessage(player, PlatformPlugin.getInstance()
                  .getPlatformConfiguration().messagesWrapper.onlySafeCanBeTakenOverByCrowbar);
              return;
            }

            final UUID safeUniqueId = UUID.fromString(tag.getString("SafeUniqueId"));
            final Optional<Safe> safeOptional = PlatformPlugin.getInstance().getSafeFactory()
                .findSafe(safeUniqueId);
            if (!safeOptional.isPresent()) {
              ChatHelper.sendMessage(player, PlatformPlugin.getInstance()
                  .getPlatformConfiguration().messagesWrapper.safeNotFoundInDatabase);
              return;
            }

            final Safe safe = safeOptional.get();
            if (safe.getOwnerUniqueId().equals(player.getUniqueId()) && safe.getOwnerNickname()
                .equals(player.getName())) {
              ChatHelper.sendMessage(player, PlatformPlugin.getInstance()
                  .getPlatformConfiguration().messagesWrapper.youCannotTakeoverYourOwnSafe);
              return;
            }

            safe.setOwnerUniqueId(player.getUniqueId());
            safe.setOwnerNickname(player.getName());
            ChatHelper.sendMessage(player, ChatHelper.colored(PlatformPlugin.getInstance()
                .getPlatformConfiguration().messagesWrapper.safeHaveBeenSuccessfullyTakenOver.replace(
                    "{SAFE_UNIQUE_ID}", safe.getUniqueId().toString())));
            PlatformPlugin.getInstance().getRedisAdapter().sendPacket(
                new PlatformSafeOwnerUpdatePacket(safeUniqueId, safe.getOwnerUniqueId(),
                    safe.getOwnerNickname()), "rhc_master_controller", "rhc_platform");
            event.setCurrentItem(PlatformPlugin.getInstance()
                .getPlatformConfiguration().customItemsWrapper.safeItemWrapper.asItemStack(safe));
          }
        }));
    this.inventory.setCloseAction(event -> {
      final ItemStack item = event.getInventory().getItem(takeoverElement.slot);
      if (Objects.nonNull(item)) {
        ItemHelper.addItem(player, item);
      }
    });
  }

  public void open() {
    if (this.player.isOnline()) {
      this.inventory.openInventory(this.player);
    }
  }
}
