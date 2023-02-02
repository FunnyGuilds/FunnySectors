package pl.rosehc.platform.inventory.player.other;

import java.util.Map.Entry;
import me.vaperion.blade.exception.BladeExitMessage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import pl.rosehc.adapter.builder.ItemStackBuilder;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.adapter.helper.SerializeHelper;
import pl.rosehc.adapter.inventory.BukkitInventory;
import pl.rosehc.adapter.inventory.BukkitInventoryElement;
import pl.rosehc.controller.packet.platform.user.PlatformUserSetHomePacket;
import pl.rosehc.controller.wrapper.platform.gui.other.PlatformHomeSpigotGuiElementWrapper;
import pl.rosehc.controller.wrapper.spigot.SpigotGuiElementWrapper;
import pl.rosehc.controller.wrapper.spigot.SpigotGuiWrapper;
import pl.rosehc.platform.PlatformPlugin;
import pl.rosehc.platform.user.PlatformUser;
import pl.rosehc.sectors.SectorsPlugin;
import pl.rosehc.sectors.helper.SectorHelper;
import pl.rosehc.sectors.sector.SectorType;

public final class PlayerHomeInventory {

  private final Player player;
  private final BukkitInventory inventory;

  public PlayerHomeInventory(final Player player) {
    this.player = player;
    final SpigotGuiWrapper homeGuiWrapper = PlatformPlugin.getInstance()
        .getPlatformConfiguration().inventoryMap.get("home");
    if (homeGuiWrapper == null) {
      throw new BladeExitMessage(ChatHelper.colored(
          PlatformPlugin.getInstance().getPlatformConfiguration().messagesWrapper.guiNotFound));
    }

    this.inventory = new BukkitInventory(ChatHelper.colored(homeGuiWrapper.inventoryName),
        homeGuiWrapper.inventorySize);
    final PlatformUser user = PlatformPlugin.getInstance().getPlatformUserFactory()
        .findUserByUniqueId(player.getUniqueId()).orElseThrow(() -> new BladeExitMessage(
            ChatHelper.colored(PlatformPlugin.getInstance()
                .getPlatformConfiguration().messagesWrapper.playerNotFound.replace("{PLAYER_NAME}",
                    player.getName()))));
    final SpigotGuiElementWrapper fillElement = homeGuiWrapper.fillElement;
    if (fillElement != null) {
      this.inventory.fillWith(fillElement.asItemStack());
    }

    for (final Entry<String, SpigotGuiElementWrapper> entry : homeGuiWrapper.elements.entrySet()) {
      if (!(entry.getValue() instanceof PlatformHomeSpigotGuiElementWrapper)) {
        this.inventory.setElement(entry.getValue().slot,
            new BukkitInventoryElement(entry.getValue().asItemStack()));
      }
    }

    for (int index = 0; index < 4; index++) {
      final int homeIndex = index;
      final SpigotGuiElementWrapper element = homeGuiWrapper.elements.get("home" + (homeIndex + 1));
      if (!(element instanceof PlatformHomeSpigotGuiElementWrapper)) {
        continue;
      }

      final PlatformHomeSpigotGuiElementWrapper homeElement = (PlatformHomeSpigotGuiElementWrapper) element;
      this.inventory.setElement(homeElement.slot, new BukkitInventoryElement(
          new ItemStackBuilder(Material.matchMaterial(homeElement.material), 1,
              !player.hasPermission("platform-home-" + (homeIndex + 1))
                  ? homeElement.noPermissionData
                  : user.doesNotHaveHome(index) ? homeElement.notSetData
                      : homeElement.alreadySetData).withName(homeElement.name).withLore(
                  !player.hasPermission("platform-home-" + (homeIndex + 1))
                      ? homeElement.noPermissionLore
                      : user.doesNotHaveHome(index) ? homeElement.notSetLore
                          : homeElement.alreadySetLore)
              .build(), event -> {
        if (!this.player.hasPermission("platform-home-" + (homeIndex + 1))) {
          ChatHelper.sendMessage(this.player, PlatformPlugin.getInstance()
              .getPlatformConfiguration().messagesWrapper.homeNoPermission);
          return;
        }

        if (event.isRightClick()) {
          final Location location = player.getLocation();
          if (SectorHelper.getAt(location)
              .filter(sector -> !sector.getType().equals(SectorType.GAME)).isPresent()
              || !SectorsPlugin.getInstance().getSectorFactory().getCurrentSector().getType()
              .equals(SectorType.GAME)) {
            ChatHelper.sendMessage(player, PlatformPlugin.getInstance()
                .getPlatformConfiguration().messagesWrapper.homeCannotBeSet);
            return;
          }

          user.setHome(location, homeIndex);
          PlatformPlugin.getInstance().getRedisAdapter().sendPacket(
              new PlatformUserSetHomePacket(player.getUniqueId(),
                  SerializeHelper.serializeLocation(location), homeIndex), "rhc_master_controller",
              "rhc_platform");
          ChatHelper.sendMessage(player, PlatformPlugin.getInstance()
              .getPlatformConfiguration().messagesWrapper.homeSuccessfullySet.replace(
                  "{HOME_IDENTIFIER}", String.valueOf(homeIndex + 1)));
          final PlayerHomeInventory inventory = new PlayerHomeInventory(player);
          inventory.open();
        } else if (event.isLeftClick()) {
          if (user.doesNotHaveHome(homeIndex)) {
            ChatHelper.sendMessage(this.player,
                PlatformPlugin.getInstance().getPlatformConfiguration().messagesWrapper.homeNotSet);
            return;
          }

          PlatformPlugin.getInstance().getTimerTaskFactory()
              .addTimer(player, user.getHomes()[homeIndex], 5);
          player.closeInventory();
        }
      }));
    }
  }

  public void open() {
    if (this.player.isOnline()) {
      this.inventory.openInventory(this.player);
    }
  }
}
