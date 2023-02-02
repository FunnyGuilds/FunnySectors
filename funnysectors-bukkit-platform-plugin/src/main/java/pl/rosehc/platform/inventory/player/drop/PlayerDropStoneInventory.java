package pl.rosehc.platform.inventory.player.drop;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.rosehc.adapter.builder.ItemStackBuilder;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.adapter.inventory.BukkitInventory;
import pl.rosehc.adapter.inventory.BukkitInventoryElement;
import pl.rosehc.controller.packet.platform.user.PlatformUserDropSettingsAddDisabledDropPacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserDropSettingsRemoveDisabledDropPacket;
import pl.rosehc.controller.wrapper.platform.gui.drop.PlatformDropStoneDropCobbleStoneItemGuiElementWrapper;
import pl.rosehc.controller.wrapper.platform.gui.drop.PlatformDropStoneDropItemGuiElementWrapper;
import pl.rosehc.controller.wrapper.spigot.SpigotGuiElementWrapper;
import pl.rosehc.controller.wrapper.spigot.SpigotGuiWrapper;
import pl.rosehc.platform.PlatformPlugin;
import pl.rosehc.platform.drop.Drop;
import pl.rosehc.platform.user.PlatformUser;
import pl.rosehc.platform.user.subdata.PlatformUserDropSettings;

public final class PlayerDropStoneInventory {

  private final Player player;
  private final BukkitInventory inventory;

  public PlayerDropStoneInventory(final Player player, final PlatformUser user) {
    this.player = player;
    final SpigotGuiWrapper dropStoneGuiWrapper = PlatformPlugin.getInstance()
        .getPlatformConfiguration().inventoryMap.get("drop_stone");
    if (dropStoneGuiWrapper == null) {
      ChatHelper.sendMessage(player,
          PlatformPlugin.getInstance().getPlatformConfiguration().messagesWrapper.guiNotFound);
      throw new IllegalStateException("Stone drop gui not configured.");
    }

    this.inventory = new BukkitInventory(ChatHelper.colored(dropStoneGuiWrapper.inventoryName),
        dropStoneGuiWrapper.inventorySize);
    final SpigotGuiElementWrapper fillElement = dropStoneGuiWrapper.fillElement;
    if (fillElement != null) {
      this.inventory.fillWith(fillElement.asItemStack());
    }

    for (final Entry<String, SpigotGuiElementWrapper> entry : dropStoneGuiWrapper.elements.entrySet()) {
      final SpigotGuiElementWrapper element = entry.getValue();
      if (!(element instanceof PlatformDropStoneDropItemGuiElementWrapper)
          && !(element instanceof PlatformDropStoneDropCobbleStoneItemGuiElementWrapper)
          && !entry.getKey().equalsIgnoreCase("back") && !entry.getKey()
          .equalsIgnoreCase("enable_all") && !entry.getKey().equalsIgnoreCase("disable_all")) {
        this.inventory.setElement(element.slot, new BukkitInventoryElement(element.asItemStack()));
      }
    }

    final PlatformUserDropSettings dropSettings = user.getDropSettings();
    final Collection<Drop> dropCollection = PlatformPlugin.getInstance().getDropFactory()
        .getDropMap().values();
    for (final Drop drop : dropCollection) {
      final SpigotGuiElementWrapper element = dropStoneGuiWrapper.elements.get(
          "item" + drop.getName());
      if (!(element instanceof PlatformDropStoneDropItemGuiElementWrapper)) {
        continue;
      }

      final PlatformDropStoneDropItemGuiElementWrapper dropStoneDropItemElement = (PlatformDropStoneDropItemGuiElementWrapper) element;
      this.inventory.setElement(dropStoneDropItemElement.slot, new BukkitInventoryElement(
          new ItemStackBuilder(new ItemStack(drop.getMaterial(), 1, drop.getData())).withName(
              dropStoneDropItemElement.name.replace("{DROP_NAME}", drop.getName())).withLore(
              (!dropSettings.getDisabledDropSet().contains(drop)
                  ? dropStoneDropItemElement.enabledLore
                  : dropStoneDropItemElement.disabledLore).stream().map(
                      content -> content.replace("{CHANCE}", String.format("%.2f", drop.getChance()))
                          .replace("{EXP}", String.format("%.2f", drop.getExp()))
                          .replace("{MIN_AMOUNT}", String.valueOf(drop.getMinAmount()))
                          .replace("{MAX_AMOUNT}", String.valueOf(drop.getMaxAmount()))
                          .replace("{MIN_Y}", String.valueOf(drop.getMinY()))
                          .replace("{MAX_Y}", String.valueOf(drop.getMaxY())))
                  .collect(Collectors.toList())).build(), event -> {
        if (!dropSettings.getDisabledDropSet().add(drop)) {
          dropSettings.getDisabledDropSet().remove(drop);
          PlatformPlugin.getInstance().getRedisAdapter().sendPacket(
              new PlatformUserDropSettingsRemoveDisabledDropPacket(user.getUniqueId(),
                  drop.getName()), "rhc_master_controller", "rhc_platform");
        } else {
          PlatformPlugin.getInstance().getRedisAdapter().sendPacket(
              new PlatformUserDropSettingsAddDisabledDropPacket(user.getUniqueId(), drop.getName()),
              "rhc_master_controller", "rhc_platform");
        }

        final PlayerDropStoneInventory newInventory = new PlayerDropStoneInventory(player, user);
        newInventory.open();
      }));
    }

    final SpigotGuiElementWrapper cobbleStoneElementWrapper = dropStoneGuiWrapper.elements.get(
        "cobbleStone");
    if (cobbleStoneElementWrapper instanceof PlatformDropStoneDropCobbleStoneItemGuiElementWrapper) {
      final PlatformDropStoneDropCobbleStoneItemGuiElementWrapper cobbleStoneElement = (PlatformDropStoneDropCobbleStoneItemGuiElementWrapper) cobbleStoneElementWrapper;
      this.inventory.setElement(cobbleStoneElement.slot, new BukkitInventoryElement(
          new ItemStackBuilder(Material.COBBLESTONE, 1, (short) 0).withName(cobbleStoneElement.name)
              .withLore(dropSettings.isCobbleStone() ? cobbleStoneElement.enabledLore
                  : cobbleStoneElement.disabledLore).build(), event -> {
        dropSettings.setCobbleStone(!dropSettings.isCobbleStone());
        final PlayerDropStoneInventory newInventory = new PlayerDropStoneInventory(player, user);
        newInventory.open();
      }));
    }

    final SpigotGuiElementWrapper enableAllElement = dropStoneGuiWrapper.elements.get("enable_all");
    if (enableAllElement != null) {
      this.inventory.setElement(enableAllElement.slot,
          new BukkitInventoryElement(enableAllElement.asItemStack(), event -> {
            final Set<Drop> disabledDropSet = new HashSet<>(dropSettings.getDisabledDropSet());
            dropSettings.getDisabledDropSet().clear();
            for (final Drop drop : disabledDropSet) {
              PlatformPlugin.getInstance().getRedisAdapter().sendPacket(
                  new PlatformUserDropSettingsRemoveDisabledDropPacket(player.getUniqueId(),
                      drop.getName()), "rhc_master_controller", "rhc_platform");
            }

            final PlayerDropStoneInventory newInventory = new PlayerDropStoneInventory(player,
                user);
            newInventory.open();
          }));
    }

    final SpigotGuiElementWrapper disableAllElement = dropStoneGuiWrapper.elements.get(
        "disable_all");
    if (disableAllElement != null) {
      this.inventory.setElement(disableAllElement.slot,
          new BukkitInventoryElement(disableAllElement.asItemStack(), event -> {
            dropSettings.getDisabledDropSet().addAll(dropCollection);
            for (final Drop drop : dropCollection) {
              PlatformPlugin.getInstance().getRedisAdapter().sendPacket(
                  new PlatformUserDropSettingsAddDisabledDropPacket(player.getUniqueId(),
                      drop.getName()), "rhc_master_controller", "rhc_platform");
            }

            final PlayerDropStoneInventory newInventory = new PlayerDropStoneInventory(player,
                user);
            newInventory.open();
          }));
    }

    final SpigotGuiElementWrapper backElement = dropStoneGuiWrapper.elements.get("back");
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
