package pl.rosehc.platform.inventory.player.kit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.rosehc.adapter.builder.ItemStackBuilder;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.adapter.helper.ItemHelper;
import pl.rosehc.adapter.helper.TimeHelper;
import pl.rosehc.adapter.inventory.BukkitInventory;
import pl.rosehc.adapter.inventory.BukkitInventoryElement;
import pl.rosehc.controller.packet.platform.user.PlatformUserReceiveKitPacket;
import pl.rosehc.controller.wrapper.platform.gui.kit.PlatformKitPreviewGivenItemGuiElementWrapper;
import pl.rosehc.controller.wrapper.platform.gui.kit.PlatformKitPreviewPickupGuiElementWrapper;
import pl.rosehc.controller.wrapper.spigot.SpigotGuiElementWrapper;
import pl.rosehc.controller.wrapper.spigot.SpigotGuiWrapper;
import pl.rosehc.platform.PlatformPlugin;
import pl.rosehc.platform.kit.Kit;
import pl.rosehc.platform.user.PlatformUser;

public final class PlayerKitPreviewInventory {

  private final Player player;
  private final BukkitInventory inventory;

  public PlayerKitPreviewInventory(final Player player, final Kit kit, final PlatformUser user) {
    this.player = player;
    final SpigotGuiWrapper kitPreviewGuiWrapper = PlatformPlugin.getInstance()
        .getPlatformConfiguration().inventoryMap.get("kit_preview_" + kit.getName());
    if (kitPreviewGuiWrapper == null) {
      ChatHelper.sendMessage(player,
          PlatformPlugin.getInstance().getPlatformConfiguration().messagesWrapper.guiNotFound);
      throw new IllegalStateException();
    }

    final SpigotGuiElementWrapper pickupElementWrapper = kitPreviewGuiWrapper.elements.get(
        "pickup");
    if (!(pickupElementWrapper instanceof PlatformKitPreviewPickupGuiElementWrapper)) {
      ChatHelper.sendMessage(player,
          PlatformPlugin.getInstance().getPlatformConfiguration().messagesWrapper.guiNotFound);
      throw new IllegalStateException();
    }

    this.inventory = new BukkitInventory(
        ChatHelper.colored(kitPreviewGuiWrapper.inventoryName.replace("{KIT_NAME}", kit.getName())),
        kitPreviewGuiWrapper.inventorySize);
    final SpigotGuiElementWrapper fillElement = kitPreviewGuiWrapper.fillElement;
    if (fillElement != null) {
      this.inventory.fillWith(fillElement.asItemStack());
    }

    for (final Entry<String, SpigotGuiElementWrapper> entry : kitPreviewGuiWrapper.elements.entrySet()) {
      final SpigotGuiElementWrapper element = entry.getValue();
      if (!(element instanceof PlatformKitPreviewPickupGuiElementWrapper)
          && !(element instanceof PlatformKitPreviewGivenItemGuiElementWrapper) && !entry.getKey()
          .equalsIgnoreCase("back")) {
        this.inventory.setElement(element.slot, new BukkitInventoryElement(element.asItemStack()));
      }
    }

    for (final Entry<Integer, ItemStack> entry : kit.getItemStacks().entrySet()) {
      final SpigotGuiElementWrapper element = kitPreviewGuiWrapper.elements.get(
          "item" + entry.getKey());
      if (element instanceof PlatformKitPreviewGivenItemGuiElementWrapper) {
        this.inventory.setElement(element.slot, new BukkitInventoryElement(entry.getValue()));
      }
    }

    final SpigotGuiElementWrapper backElement = kitPreviewGuiWrapper.elements.get("back");
    if (backElement != null) {
      this.inventory.setElement(backElement.slot,
          new BukkitInventoryElement(backElement.asItemStack(),
              event -> player.performCommand("kit")));
    }

    final PlatformKitPreviewPickupGuiElementWrapper pickupElement = (PlatformKitPreviewPickupGuiElementWrapper) pickupElementWrapper;
    this.inventory.setElement(pickupElement.slot, new BukkitInventoryElement(
        new ItemStackBuilder(Material.matchMaterial(pickupElement.material), 1,
            kit.testNoPermission(player) ? pickupElement.noPermissionData
                : user.hasReceivedKit(kit) ? pickupElement.cannotPickupData
                    : pickupElement.canPickupData).withName(
                pickupElement.name.replace("{KIT_NAME}", kit.getName()))
            .withLore(this.wrapLore(user, kit, pickupElement)).build(), event -> {
      if (kit.testNoPermission(player)) {
        ChatHelper.sendMessage(player, PlatformPlugin.getInstance()
            .getPlatformConfiguration().messagesWrapper.kitNoPermission.replace("{PERMISSION_NAME}",
                kit.getPermission()));
        return;
      }

      if (user.hasReceivedKit(kit)) {
        ChatHelper.sendMessage(player, PlatformPlugin.getInstance()
            .getPlatformConfiguration().messagesWrapper.kitAlreadyReceived.replace("{TIME}",
                TimeHelper.timeToString(user.getKitLeftTime(kit))));
        return;
      }

      final long receivedTime = System.currentTimeMillis();
      user.receiveKit(kit, receivedTime);
      player.closeInventory();
      ItemHelper.addItems(player, new ArrayList<>(kit.getItemStacks().values()));
      ChatHelper.sendMessage(player, PlatformPlugin.getInstance()
          .getPlatformConfiguration().messagesWrapper.kitSuccessfullyReceived.replace("{KIT_NAME}",
              kit.getName()));
      PlatformPlugin.getInstance().getRedisAdapter().sendPacket(
          new PlatformUserReceiveKitPacket(user.getUniqueId(), kit.getName(), receivedTime),
          "rhc_master_controller", "rhc_platform");
    }));
  }

  public void open() {
    if (this.player.isOnline()) {
      this.inventory.openInventory(this.player);
    }
  }

  private List<String> wrapLore(final PlatformUser user, final Kit kit,
      final PlatformKitPreviewPickupGuiElementWrapper pickupElementWrapper) {
    if (kit.testNoPermission(this.player)) {
      return pickupElementWrapper.noPermissionLore;
    }

    return user.hasReceivedKit(kit) ? pickupElementWrapper.cannotPickupLore.stream().map(
            content -> content.replace("{TIME}", TimeHelper.timeToString(user.getKitLeftTime(kit))))
        .collect(Collectors.toList()) : pickupElementWrapper.canPickupLore;
  }
}
