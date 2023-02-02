package pl.rosehc.platform.listener.player;

import java.util.stream.Collectors;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import pl.rosehc.adapter.helper.ItemHelper;
import pl.rosehc.platform.PlatformConfiguration.CustomItemsWrapper.CustomItemWrapper;
import pl.rosehc.platform.PlatformPlugin;
import pl.rosehc.sectors.SectorsPlugin;
import pl.rosehc.sectors.helper.SectorHelper;
import pl.rosehc.sectors.sector.SectorType;

public final class PlayerRespawnListener implements Listener {

  private final PlatformPlugin plugin;

  public PlayerRespawnListener(final PlatformPlugin plugin) {
    this.plugin = plugin;
  }

  @EventHandler
  public void onRespawn(final PlayerRespawnEvent event) {
    final Player player = event.getPlayer();
    final PlayerInventory inventory = player.getInventory();
    inventory.clear();
    inventory.setArmorContents(new ItemStack[4]);
    inventory.setHeldItemSlot(0);
    ItemHelper.addItems(player,
        this.plugin.getPlatformConfiguration().customItemsWrapper.respawnItemList.stream()
            .map(CustomItemWrapper::asItemStack).collect(Collectors.toList()));
    player.setHealth(player.getMaxHealth());
    player.setFoodLevel(20);
    player.setExp(0F);
    player.setFireTicks(0);
    player.setFoodLevel(20);
    player.setLevel(0);
    event.setRespawnLocation(SectorHelper.getRandomSector(SectorType.SPAWN).isPresent()
        ? this.plugin.getPlatformConfiguration().spawnLocationWrapper.unwrap()
        : SectorsPlugin.getInstance().getSectorFactory().getCurrentSector().random());
    this.plugin.getServer().getPluginManager().callEvent(
        new PlayerTeleportEvent(player, player.getLocation(), event.getRespawnLocation(),
            TeleportCause.PLUGIN));
  }
}
