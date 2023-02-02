package pl.rosehc.platform.inventory.player.drop;

import java.util.Map.Entry;
import java.util.Objects;
import org.bukkit.entity.Player;
import pl.rosehc.adapter.builder.ItemStackBuilder;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.adapter.inventory.BukkitInventory;
import pl.rosehc.adapter.inventory.BukkitInventoryElement;
import pl.rosehc.controller.wrapper.platform.gui.drop.PlatformDropPreviewItemGuiElementWrapper;
import pl.rosehc.controller.wrapper.spigot.SpigotGuiElementWrapper;
import pl.rosehc.controller.wrapper.spigot.SpigotGuiWrapper;
import pl.rosehc.platform.PlatformPlugin;
import pl.rosehc.platform.magiccase.MagicCaseItem;

public final class PlayerDropMagicCaseInventory {

  private final Player player;
  private final BukkitInventory inventory;

  public PlayerDropMagicCaseInventory(final Player player) {
    this.player = player;
    final SpigotGuiWrapper dropMagicCaseGuiWrapper = PlatformPlugin.getInstance()
        .getPlatformConfiguration().inventoryMap.get("drop_magicCase");
    if (dropMagicCaseGuiWrapper == null) {
      ChatHelper.sendMessage(player,
          PlatformPlugin.getInstance().getPlatformConfiguration().messagesWrapper.guiNotFound);
      throw new IllegalStateException("MagicCase drop gui not configured.");
    }

    this.inventory = new BukkitInventory(ChatHelper.colored(dropMagicCaseGuiWrapper.inventoryName),
        dropMagicCaseGuiWrapper.inventorySize);
    final SpigotGuiElementWrapper fillElement = dropMagicCaseGuiWrapper.fillElement;
    if (fillElement != null) {
      this.inventory.fillWith(fillElement.asItemStack());
    }

    for (final Entry<String, SpigotGuiElementWrapper> entry : dropMagicCaseGuiWrapper.elements.entrySet()) {
      final SpigotGuiElementWrapper element = entry.getValue();
      if (!(element instanceof PlatformDropPreviewItemGuiElementWrapper) && !entry.getKey()
          .equalsIgnoreCase("back")) {
        this.inventory.setElement(element.slot, new BukkitInventoryElement(element.asItemStack()));
      }
    }

    PlatformPlugin.getInstance().getCobbleXItemFactory().executeLocked(cobbleXItemList -> {
      for (int i = 0; i < cobbleXItemList.size(); i++) {
        final SpigotGuiElementWrapper element = dropMagicCaseGuiWrapper.elements.get("item" + i);
        if (!(element instanceof PlatformDropPreviewItemGuiElementWrapper)) {
          continue;
        }

        final PlatformDropPreviewItemGuiElementWrapper dropPreviewItemElement = (PlatformDropPreviewItemGuiElementWrapper) element;
        final ItemStackBuilder builder = new ItemStackBuilder(
            cobbleXItemList.get(i).getOriginalItemStack().clone());
        if (Objects.nonNull(dropPreviewItemElement.name)) {
          builder.withName(dropPreviewItemElement.name);
        }

        if (Objects.nonNull(dropPreviewItemElement.lore)) {
          builder.withLore(dropPreviewItemElement.lore);
        }

        this.inventory.setElement(dropPreviewItemElement.slot,
            new BukkitInventoryElement(builder.build()));
      }
    });

    int index = 0;
    for (final MagicCaseItem magicCaseItem : PlatformPlugin.getInstance().getMagicCaseFactory()
        .getMagicCaseItemSet()) {
      final SpigotGuiElementWrapper element = dropMagicCaseGuiWrapper.elements.get(
          "item" + (index++));
      if (!(element instanceof PlatformDropPreviewItemGuiElementWrapper)) {
        continue;
      }

      final PlatformDropPreviewItemGuiElementWrapper dropPreviewItemElement = (PlatformDropPreviewItemGuiElementWrapper) element;
      final ItemStackBuilder builder = new ItemStackBuilder(
          magicCaseItem.getOriginalItemStack().clone());
      if (Objects.nonNull(dropPreviewItemElement.name)) {
        builder.withName(dropPreviewItemElement.name);
      }

      if (Objects.nonNull(dropPreviewItemElement.lore)) {
        builder.withLore(dropPreviewItemElement.lore);
      }

      this.inventory.setElement(dropPreviewItemElement.slot,
          new BukkitInventoryElement(builder.build()));
    }

    final SpigotGuiElementWrapper backElement = dropMagicCaseGuiWrapper.elements.get("back");
    if (backElement != null) {
      this.inventory.setElement(backElement.slot,
          new BukkitInventoryElement(backElement.asItemStack(),
              event -> player.performCommand("drop")));
    }
  }

  public void open() {
    if (this.player.isOnline()) {
      this.inventory.openInventory(this.player);
    }
  }
}
