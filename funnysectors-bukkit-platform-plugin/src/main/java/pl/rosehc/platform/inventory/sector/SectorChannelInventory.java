package pl.rosehc.platform.inventory.sector;

import java.util.Map.Entry;
import java.util.stream.Collectors;
import me.vaperion.blade.exception.BladeExitMessage;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import pl.rosehc.adapter.builder.ItemStackBuilder;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.adapter.helper.EventCompletionStage;
import pl.rosehc.adapter.helper.TimeHelper;
import pl.rosehc.adapter.inventory.BukkitInventory;
import pl.rosehc.adapter.inventory.BukkitInventoryElement;
import pl.rosehc.controller.wrapper.platform.gui.other.PlatformChannelSpigotGuiElementWrapper;
import pl.rosehc.controller.wrapper.spigot.SpigotGuiElementWrapper;
import pl.rosehc.controller.wrapper.spigot.SpigotGuiWrapper;
import pl.rosehc.platform.PlatformPlugin;
import pl.rosehc.sectors.SectorsPlugin;
import pl.rosehc.sectors.helper.ConnectHelper;
import pl.rosehc.sectors.sector.Sector;
import pl.rosehc.sectors.sector.SectorConnectingEvent;
import pl.rosehc.sectors.sector.SectorType;
import pl.rosehc.sectors.sector.user.SectorUser;

public final class SectorChannelInventory {

  private final Player player;
  private final BukkitInventory inventory;

  public SectorChannelInventory(final Player player) {
    this.player = player;
    final SpigotGuiWrapper channelGuiWrapper = PlatformPlugin.getInstance()
        .getPlatformConfiguration().inventoryMap.get("channel");
    if (channelGuiWrapper == null) {
      throw new BladeExitMessage(
          PlatformPlugin.getInstance().getPlatformConfiguration().messagesWrapper.guiNotFound);
    }

    this.inventory = new BukkitInventory(ChatHelper.colored(channelGuiWrapper.inventoryName),
        channelGuiWrapper.inventorySize);
    final SpigotGuiElementWrapper fillElement = channelGuiWrapper.fillElement;
    if (fillElement != null) {
      this.inventory.fillWith(fillElement.asItemStack());
    }

    for (final Entry<String, SpigotGuiElementWrapper> entry : channelGuiWrapper.elements.entrySet()) {
      if (!(entry.getValue() instanceof PlatformChannelSpigotGuiElementWrapper)) {
        this.inventory.setElement(entry.getValue().slot,
            new BukkitInventoryElement(entry.getValue().asItemStack()));
      }
    }

    for (final Sector sector : SectorsPlugin.getInstance().getSectorFactory().getSectorMap()
        .values()) {
      if (sector.getType().equals(SectorType.SPAWN)) {
        final SpigotGuiElementWrapper element = channelGuiWrapper.elements.get(
            sector.getName().toLowerCase());
        if (!(element instanceof PlatformChannelSpigotGuiElementWrapper)) {
          continue;
        }

        final PlatformChannelSpigotGuiElementWrapper channelElement = (PlatformChannelSpigotGuiElementWrapper) element;
        this.inventory.setElement(channelElement.slot, new BukkitInventoryElement(
            new ItemStackBuilder(Material.matchMaterial(channelElement.material), 1,
                SectorsPlugin.getInstance().getSectorFactory().getCurrentSector().equals(sector)
                    ? channelElement.currentData
                    : !sector.getStatistics().isOnline() ? channelElement.disabledData
                        : sector.getStatistics().getLoad() >= 68.8D
                            || sector.getStatistics().getTps() <= 5.58D
                            ? channelElement.heavilyLoadedData
                            : channelElement.enabledData).withName(
                channelElement.name.replace("{SECTOR_NAME}", sector.getName())).withLore(
                (SectorsPlugin.getInstance().getSectorFactory().getCurrentSector().equals(sector)
                    ? channelElement.currentLore
                    : !sector.getStatistics().isOnline() ? channelElement.disabledLore
                        : sector.getStatistics().getLoad() >= 68.8D
                            || sector.getStatistics().getTps() <= 5.58D
                            ? channelElement.heavilyLoadedLore
                            : channelElement.enabledLore).stream().map(
                    content -> content.replace("{ONLINE_PLAYERS}",
                            String.valueOf(sector.getStatistics().getPlayers()))
                        .replace("{TPS}", this.format(sector.getStatistics().getTps()))
                        .replace("{LOAD}", String.format("%.2f", sector.getStatistics().getLoad()))
                        .replace("{LAST_UPDATE}",
                            sector.getStatistics().getLastUpdate() != 0L ? TimeHelper.timeToString(
                                System.currentTimeMillis() - sector.getStatistics().getLastUpdate())
                                : "< 1s")).collect(Collectors.toList())).build(), event -> {
          if (SectorsPlugin.getInstance().getSectorFactory().getCurrentSector().equals(sector)) {
            ChatHelper.sendMessage(player, PlatformPlugin.getInstance()
                .getPlatformConfiguration().messagesWrapper.channelIsACurrentSector);
            return;
          }

          if (!sector.getStatistics().isOnline()) {
            ChatHelper.sendMessage(player, SectorsPlugin.getInstance()
                .getSectorsConfiguration().messagesWrapper.sectorIsOffline);
            return;
          }

          if (sector.getStatistics().getLoad() >= 68.8D
              || sector.getStatistics().getTps() <= 5.58D) {
            ChatHelper.sendMessage(player, SectorsPlugin.getInstance()
                .getSectorsConfiguration().messagesWrapper.sectorIsHeavilyLoaded);
            return;
          }

          final SectorUser user = SectorsPlugin.getInstance().getSectorUserFactory()
              .findUserByPlayer(player);
          if (user != null) {
            final SectorConnectingEvent sectorConnectingEvent = new SectorConnectingEvent(player,
                sector, true, false);
            final EventCompletionStage completionStage = new EventCompletionStage(
                () -> ConnectHelper.connect(player, user, sectorConnectingEvent.getSector(),
                    player.getLocation()));
            sectorConnectingEvent.setCompletionStage(completionStage);
            Bukkit.getPluginManager().callEvent(sectorConnectingEvent);
            if (sectorConnectingEvent.isCancelled()) {
              return;
            }

            completionStage.postFire();
          }
        }));
      }
    }
  }

  public void open() {
    if (this.player.isOnline()) {
      this.inventory.openInventory(this.player);
    }
  }

  private String format(final double tps) {
    return (tps > 18D ? ChatColor.GREEN : (tps > 16D ? ChatColor.YELLOW : ChatColor.RED).toString())
        + (tps > 20D ? "*" : "") + Math.min(Math.round(tps * 100D) / 100D, 20D);
  }
}
