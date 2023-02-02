package pl.rosehc.platform.inventory.player.kit;

import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import me.vaperion.blade.exception.BladeExitMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import pl.rosehc.adapter.builder.ItemStackBuilder;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.adapter.helper.TimeHelper;
import pl.rosehc.adapter.inventory.BukkitInventory;
import pl.rosehc.adapter.inventory.BukkitInventoryElement;
import pl.rosehc.controller.wrapper.platform.gui.kit.PlatformKitListPreviewGuiElementWrapper;
import pl.rosehc.controller.wrapper.spigot.SpigotGuiElementWrapper;
import pl.rosehc.controller.wrapper.spigot.SpigotGuiWrapper;
import pl.rosehc.platform.PlatformPlugin;
import pl.rosehc.platform.kit.Kit;
import pl.rosehc.platform.user.PlatformUser;

public final class PlayerKitListInventory {

  private final Player player;
  private final BukkitInventory inventory;

  public PlayerKitListInventory(final Player player) {
    this.player = player;
    final SpigotGuiWrapper kitListGuiWrapper = PlatformPlugin.getInstance()
        .getPlatformConfiguration().inventoryMap.get("kit_list");
    if (kitListGuiWrapper == null) {
      throw new BladeExitMessage(ChatHelper.colored(
          PlatformPlugin.getInstance().getPlatformConfiguration().messagesWrapper.guiNotFound));
    }

    this.inventory = new BukkitInventory(ChatHelper.colored(kitListGuiWrapper.inventoryName),
        kitListGuiWrapper.inventorySize);
    final PlatformUser user = PlatformPlugin.getInstance().getPlatformUserFactory()
        .findUserByUniqueId(player.getUniqueId()).orElseThrow(() -> new BladeExitMessage(
            PlatformPlugin.getInstance()
                .getPlatformConfiguration().messagesWrapper.playerNotFound.replace("{PLAYER_NAME}",
                    player.getName())));
    final SpigotGuiElementWrapper fillElement = kitListGuiWrapper.fillElement;
    if (fillElement != null) {
      this.inventory.fillWith(fillElement.asItemStack());
    }

    for (final Entry<String, SpigotGuiElementWrapper> entry : kitListGuiWrapper.elements.entrySet()) {
      if (!(entry.getValue() instanceof PlatformKitListPreviewGuiElementWrapper)) {
        this.inventory.setElement(entry.getValue().slot,
            new BukkitInventoryElement(entry.getValue().asItemStack()));
      }
    }

    for (final Kit kit : PlatformPlugin.getInstance().getKitFactory().getKitMap().values()) {
      final SpigotGuiElementWrapper element = kitListGuiWrapper.elements.get("kit" + kit.getName());
      if (!(element instanceof PlatformKitListPreviewGuiElementWrapper)) {
        continue;
      }

      final PlatformKitListPreviewGuiElementWrapper kitListPreviewElement = (PlatformKitListPreviewGuiElementWrapper) element;
      this.inventory.setElement(kitListPreviewElement.slot, new BukkitInventoryElement(
          new ItemStackBuilder(Material.matchMaterial(kitListPreviewElement.material), 1,
              kitListPreviewElement.data).withName(
                  kitListPreviewElement.name.replace("{KIT_NAME}", kit.getName()))
              .withLore(this.wrapLore(user, kit, kitListPreviewElement)).build(), event -> {
        final PlayerKitPreviewInventory kitPreviewInventory = new PlayerKitPreviewInventory(player,
            kit, user);
        kitPreviewInventory.open();
      }));
    }
  }

  public void open() {
    if (this.player.isOnline()) {
      this.inventory.openInventory(this.player);
    }
  }

  private List<String> wrapLore(final PlatformUser user, final Kit kit,
      final PlatformKitListPreviewGuiElementWrapper kitListPreviewElement) {
    final List<String> lore;
    if (!kit.testNoPermission(this.player)) {
      lore = user.hasReceivedKit(kit) ? kitListPreviewElement.cannotPickupLore.stream().map(
              content -> content.replace("{TIME}", TimeHelper.timeToString(user.getKitLeftTime(kit))))
          .collect(Collectors.toList()) : kitListPreviewElement.canPickupLore;
    } else {
      lore = kitListPreviewElement.noPermissionLore;
    }

    return lore.stream().map(content -> content.replace("{KIT_NAME}", kit.getName()))
        .collect(Collectors.toList());
  }
}
