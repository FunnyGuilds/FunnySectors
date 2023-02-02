package pl.rosehc.platform.inventory.player.other;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import pl.rosehc.platform.PlatformPlugin;

public final class PlayerAdminItemsInventory {

  private final Player player;
  private final Inventory inventory;

  public PlayerAdminItemsInventory(final Player player) {
    this.player = player;
    this.inventory = Bukkit.createInventory(player, 54, ChatColor.GREEN + "Itemy admina");
    this.inventory.addItem(PlatformPlugin.getInstance()
        .getPlatformConfiguration().customItemsWrapper.goldenHeadWrapper.asItemStack());
    this.inventory.addItem(PlatformPlugin.getInstance()
        .getPlatformConfiguration().customItemsWrapper.crowbarWrapper.asItemStack(64));
    this.inventory.addItem(PlatformPlugin.getInstance()
        .getPlatformConfiguration().customItemsWrapper.cobbleXWrapper.asItemStack(64));
  }

  public void open() {
    if (this.player.isOnline()) {
      this.player.openInventory(this.inventory);
    }
  }
}
