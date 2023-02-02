package pl.rosehc.platform.inventory.player.other;

import java.util.Map.Entry;
import me.vaperion.blade.exception.BladeExitMessage;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.rosehc.adapter.builder.ItemStackBuilder;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.adapter.inventory.BukkitInventory;
import pl.rosehc.adapter.inventory.BukkitInventoryElement;
import pl.rosehc.controller.packet.platform.user.PlatformUserSelectedDiscoEffectTypeUpdatePacket;
import pl.rosehc.controller.wrapper.platform.gui.other.PlatformChatSettingSpigotGuiElementWrapper;
import pl.rosehc.controller.wrapper.platform.gui.other.PlatformDiscoEffectTypeSpigotGuiElementWrapper;
import pl.rosehc.controller.wrapper.spigot.SpigotGuiElementWrapper;
import pl.rosehc.controller.wrapper.spigot.SpigotGuiWrapper;
import pl.rosehc.platform.PlatformPlugin;
import pl.rosehc.platform.disco.DiscoEffectType;
import pl.rosehc.platform.user.PlatformUser;

public final class PlayerDiscoInventory {

  private final Player player;
  private final BukkitInventory inventory;

  public PlayerDiscoInventory(final Player player) {
    this.player = player;
    final SpigotGuiWrapper discoGuiWrapper = PlatformPlugin.getInstance()
        .getPlatformConfiguration().inventoryMap.get("disco");
    if (discoGuiWrapper == null) {
      throw new BladeExitMessage(ChatHelper.colored(
          PlatformPlugin.getInstance().getPlatformConfiguration().messagesWrapper.guiNotFound));
    }

    this.inventory = new BukkitInventory(ChatHelper.colored(discoGuiWrapper.inventoryName),
        discoGuiWrapper.inventorySize);
    final PlatformUser user = PlatformPlugin.getInstance().getPlatformUserFactory()
        .findUserByUniqueId(player.getUniqueId()).orElseThrow(() -> new BladeExitMessage(
            ChatHelper.colored(PlatformPlugin.getInstance()
                .getPlatformConfiguration().messagesWrapper.playerNotFound.replace("{PLAYER_NAME}",
                    player.getName()))));
    final SpigotGuiElementWrapper fillElement = discoGuiWrapper.fillElement;
    if (fillElement != null) {
      this.inventory.fillWith(fillElement.asItemStack());
    }

    for (final Entry<String, SpigotGuiElementWrapper> entry : discoGuiWrapper.elements.entrySet()) {
      if (!(entry.getValue() instanceof PlatformChatSettingSpigotGuiElementWrapper)) {
        this.inventory.setElement(entry.getValue().slot,
            new BukkitInventoryElement(entry.getValue().asItemStack()));
      }
    }

    for (final DiscoEffectType type : DiscoEffectType.values()) {
      final SpigotGuiElementWrapper element = discoGuiWrapper.elements.get(type.name());
      if (!(element instanceof PlatformDiscoEffectTypeSpigotGuiElementWrapper)) {
        continue;
      }

      final PlatformDiscoEffectTypeSpigotGuiElementWrapper effectTypeElement = (PlatformDiscoEffectTypeSpigotGuiElementWrapper) element;
      final ItemStackBuilder builder = new ItemStackBuilder(
          new ItemStack(Material.matchMaterial(effectTypeElement.material))).withName(
          effectTypeElement.name).withLore(
          user.getSelectedDiscoEffectType() != null && user.getSelectedDiscoEffectType()
              .equals(type) ? effectTypeElement.selectedLore : effectTypeElement.notSelectedLore);
      if (element.material.contains("LEATHER") && effectTypeElement.leatherArmorColor != null
          && effectTypeElement.leatherArmorColor.startsWith("#")
          && effectTypeElement.leatherArmorColor.length() >= 2) {
        String hexColorCode = effectTypeElement.leatherArmorColor.substring(1);
        builder.withColor(Color.fromRGB(Integer.parseInt(hexColorCode, 16)));
      }

      this.inventory.setElement(element.slot, new BukkitInventoryElement(builder.build(), event -> {
        user.setSelectedDiscoEffectType(type);
        PlatformPlugin.getInstance().getRedisAdapter().sendPacket(
            new PlatformUserSelectedDiscoEffectTypeUpdatePacket(user.getUniqueId(),
                user.getSelectedDiscoEffectType().name()), "rhc_master_controller", "rhc_platform");
        final PlayerDiscoInventory newInventory = new PlayerDiscoInventory(player);
        newInventory.open();
      }));
    }
  }

  public void open() {
    if (this.player.isOnline()) {
      this.inventory.openInventory(this.player);
    }
  }
}
