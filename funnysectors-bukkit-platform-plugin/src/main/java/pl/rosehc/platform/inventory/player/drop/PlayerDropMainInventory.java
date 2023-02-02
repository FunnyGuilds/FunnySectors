package pl.rosehc.platform.inventory.player.drop;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import me.vaperion.blade.exception.BladeExitMessage;
import org.bukkit.entity.Player;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.adapter.inventory.BukkitInventory;
import pl.rosehc.adapter.inventory.BukkitInventoryElement;
import pl.rosehc.controller.wrapper.spigot.SpigotGuiElementWrapper;
import pl.rosehc.controller.wrapper.spigot.SpigotGuiWrapper;
import pl.rosehc.platform.PlatformPlugin;
import pl.rosehc.platform.user.PlatformUser;

public final class PlayerDropMainInventory {

  private static final Map<String, BiConsumer<Player, PlatformUser>> INVENTORY_ACTION = new HashMap<String, BiConsumer<Player, PlatformUser>>() {{
    this.put("stone", (player, user) -> {
      final PlayerDropStoneInventory inventory = new PlayerDropStoneInventory(player, user);
      inventory.open();
    });
    this.put("magicCase", (player, user) -> {
      final PlayerDropMagicCaseInventory inventory = new PlayerDropMagicCaseInventory(player);
      inventory.open();
    });
    this.put("cobbleX", (player, ignored) -> {
      final PlayerDropCobbleXInventory inventory = new PlayerDropCobbleXInventory(player);
      inventory.open();
    });
  }};
  private final Player player;
  private final BukkitInventory inventory;

  public PlayerDropMainInventory(final Player player) {
    this.player = player;
    final SpigotGuiWrapper dropMainInventoryWrapper = PlatformPlugin.getInstance()
        .getPlatformConfiguration().inventoryMap.get("drop_main");
    if (dropMainInventoryWrapper == null) {
      throw new BladeExitMessage(ChatHelper.colored(
          PlatformPlugin.getInstance().getPlatformConfiguration().messagesWrapper.guiNotFound));
    }

    this.inventory = new BukkitInventory(ChatHelper.colored(dropMainInventoryWrapper.inventoryName),
        dropMainInventoryWrapper.inventorySize);
    final SpigotGuiElementWrapper fillElement = dropMainInventoryWrapper.fillElement;
    if (fillElement != null) {
      this.inventory.fillWith(fillElement.asItemStack());
    }

    for (final Entry<String, SpigotGuiElementWrapper> entry : dropMainInventoryWrapper.elements.entrySet()) {
      final SpigotGuiElementWrapper element = entry.getValue();
      if (!INVENTORY_ACTION.containsKey(entry.getKey())) {
        this.inventory.setElement(element.slot, new BukkitInventoryElement(element.asItemStack()));
      }
    }

    final PlatformUser user = PlatformPlugin.getInstance().getPlatformUserFactory()
        .findUserByUniqueId(player.getUniqueId()).orElseThrow(() -> new BladeExitMessage(
            ChatHelper.colored(PlatformPlugin.getInstance()
                .getPlatformConfiguration().messagesWrapper.playerNotFound.replace("{PLAYER_NAME}",
                    player.getName()))));
    for (final Entry<String, BiConsumer<Player, PlatformUser>> entry : INVENTORY_ACTION.entrySet()) {
      final SpigotGuiElementWrapper element = dropMainInventoryWrapper.elements.get(entry.getKey());
      if (element != null) {
        this.inventory.setElement(element.slot, new BukkitInventoryElement(element.asItemStack(),
            event -> entry.getValue().accept(player, user)));
      }
    }
  }

  public void open() {
    if (this.player.isOnline()) {
      this.inventory.openInventory(this.player);
    }
  }
}
