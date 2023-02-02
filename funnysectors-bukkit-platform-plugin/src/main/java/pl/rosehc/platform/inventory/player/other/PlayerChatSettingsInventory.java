package pl.rosehc.platform.inventory.player.other;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import me.vaperion.blade.exception.BladeExitMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import pl.rosehc.adapter.builder.ItemStackBuilder;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.adapter.inventory.BukkitInventory;
import pl.rosehc.adapter.inventory.BukkitInventoryElement;
import pl.rosehc.controller.packet.platform.user.PlatformUserSynchronizeChatSettingsPacket;
import pl.rosehc.controller.wrapper.platform.gui.other.PlatformChatSettingSpigotGuiElementWrapper;
import pl.rosehc.controller.wrapper.spigot.SpigotGuiElementWrapper;
import pl.rosehc.controller.wrapper.spigot.SpigotGuiWrapper;
import pl.rosehc.platform.PlatformPlugin;
import pl.rosehc.platform.user.PlatformUser;
import pl.rosehc.platform.user.subdata.PlatformUserChatSettings;

public final class PlayerChatSettingsInventory {

  private static final Set<PlayerChatSettingsInventoryEntry> ENTRY_SET = new HashSet<PlayerChatSettingsInventoryEntry>() {{
    this.add(new PlayerChatSettingsInventoryEntry("global", PlatformUserChatSettings::isGlobal,
        platformUserChatSettings -> platformUserChatSettings.setGlobal(
            !platformUserChatSettings.isGlobal())));
    this.add(new PlayerChatSettingsInventoryEntry("itemShop", PlatformUserChatSettings::isItemShop,
        platformUserChatSettings -> platformUserChatSettings.setItemShop(
            !platformUserChatSettings.isItemShop())));
    this.add(new PlayerChatSettingsInventoryEntry("kills", PlatformUserChatSettings::isKills,
        platformUserChatSettings -> platformUserChatSettings.setKills(
            !platformUserChatSettings.isKills())));
    this.add(new PlayerChatSettingsInventoryEntry("deaths", PlatformUserChatSettings::isDeaths,
        platformUserChatSettings -> platformUserChatSettings.setDeaths(
            !platformUserChatSettings.isDeaths())));
    this.add(new PlayerChatSettingsInventoryEntry("cases", PlatformUserChatSettings::isCases,
        platformUserChatSettings -> platformUserChatSettings.setCases(
            !platformUserChatSettings.isCases())));
    this.add(new PlayerChatSettingsInventoryEntry("achievements",
        PlatformUserChatSettings::isAchievements,
        platformUserChatSettings -> platformUserChatSettings.setAchievements(
            !platformUserChatSettings.isAchievements())));
    this.add(new PlayerChatSettingsInventoryEntry("rewards", PlatformUserChatSettings::isRewards,
        platformUserChatSettings -> platformUserChatSettings.setRewards(
            !platformUserChatSettings.isRewards())));
    this.add(new PlayerChatSettingsInventoryEntry("privateMessages",
        PlatformUserChatSettings::isPrivateMessages,
        platformUserChatSettings -> platformUserChatSettings.setPrivateMessages(
            !platformUserChatSettings.isPrivateMessages())));
  }};
  private final Player player;
  private final BukkitInventory inventory;

  public PlayerChatSettingsInventory(final Player player) {
    this.player = player;
    final SpigotGuiWrapper chatSettingsGuiWrapper = PlatformPlugin.getInstance()
        .getPlatformConfiguration().inventoryMap.get("chat_settings");
    if (chatSettingsGuiWrapper == null) {
      throw new BladeExitMessage(ChatHelper.colored(
          PlatformPlugin.getInstance().getPlatformConfiguration().messagesWrapper.guiNotFound));
    }

    this.inventory = new BukkitInventory(ChatHelper.colored(chatSettingsGuiWrapper.inventoryName),
        chatSettingsGuiWrapper.inventorySize);
    final PlatformUser user = PlatformPlugin.getInstance().getPlatformUserFactory()
        .findUserByUniqueId(player.getUniqueId()).orElseThrow(() -> new BladeExitMessage(
            ChatHelper.colored(PlatformPlugin.getInstance()
                .getPlatformConfiguration().messagesWrapper.playerNotFound.replace("{PLAYER_NAME}",
                    player.getName()))));
    final SpigotGuiElementWrapper fillElement = chatSettingsGuiWrapper.fillElement;
    if (fillElement != null) {
      this.inventory.fillWith(fillElement.asItemStack());
    }

    for (final Entry<String, SpigotGuiElementWrapper> entry : chatSettingsGuiWrapper.elements.entrySet()) {
      if (!(entry.getValue() instanceof PlatformChatSettingSpigotGuiElementWrapper)) {
        this.inventory.setElement(entry.getValue().slot,
            new BukkitInventoryElement(entry.getValue().asItemStack()));
      }
    }

    final PlatformUserChatSettings chatSettings = user.getChatSettings();
    for (final PlayerChatSettingsInventoryEntry entry : ENTRY_SET) {
      final SpigotGuiElementWrapper element = chatSettingsGuiWrapper.elements.get(entry.name);
      if (!(element instanceof PlatformChatSettingSpigotGuiElementWrapper)) {
        continue;
      }

      final PlatformChatSettingSpigotGuiElementWrapper chatSettingElement = (PlatformChatSettingSpigotGuiElementWrapper) element;
      this.inventory.setElement(element.slot, new BukkitInventoryElement(
          new ItemStackBuilder(Material.matchMaterial(chatSettingElement.material), 1,
              entry.isEnabledPredicate.test(chatSettings) ? chatSettingElement.enabledData
                  : chatSettingElement.disabledData).withName(chatSettingElement.name).withLore(
              entry.isEnabledPredicate.test(chatSettings) ? chatSettingElement.enabledLore
                  : chatSettingElement.disabledLore).build(), event -> {
        entry.switcherAction.accept(chatSettings);
        PlatformPlugin.getInstance().getRedisAdapter().sendPacket(
            new PlatformUserSynchronizeChatSettingsPacket(user.getUniqueId(),
                chatSettings.isGlobal(), chatSettings.isItemShop(), chatSettings.isKills(),
                chatSettings.isDeaths(), chatSettings.isCases(), chatSettings.isAchievements(),
                chatSettings.isRewards(), chatSettings.isPrivateMessages()),
            "rhc_master_controller", "rhc_platform");
        final PlayerChatSettingsInventory newInventory = new PlayerChatSettingsInventory(player);
        newInventory.open();
      }));
    }
  }

  public void open() {
    if (this.player.isOnline()) {
      this.inventory.openInventory(this.player);
    }
  }

  private static class PlayerChatSettingsInventoryEntry {

    private final String name;
    private final Predicate<PlatformUserChatSettings> isEnabledPredicate;
    private final Consumer<PlatformUserChatSettings> switcherAction;

    private PlayerChatSettingsInventoryEntry(final String name,
        final Predicate<PlatformUserChatSettings> isEnabledPredicate,
        final Consumer<PlatformUserChatSettings> switcherAction) {
      this.name = name;
      this.isEnabledPredicate = isEnabledPredicate;
      this.switcherAction = switcherAction;
    }
  }
}
