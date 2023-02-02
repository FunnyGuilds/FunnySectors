package pl.rosehc.platform.listener.player;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import pl.rosehc.actionbar.PrioritizedActionBarConstants;
import pl.rosehc.actionbar.PrioritizedActionBarPlugin;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.adapter.helper.ItemHelper;
import pl.rosehc.controller.packet.platform.user.PlatformUserCombatTimeUpdatePacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserKickPacket;
import pl.rosehc.platform.PlatformConfiguration.CustomItemsWrapper.CustomItemWrapper;
import pl.rosehc.platform.PlatformPlugin;
import pl.rosehc.platform.user.PlatformUser;
import pl.rosehc.sectors.SectorsPlugin;
import pl.rosehc.sectors.data.SectorPlayerData;
import pl.rosehc.sectors.helper.ConnectHelper;
import pl.rosehc.sectors.helper.SectorHelper;
import pl.rosehc.sectors.sector.Sector;
import pl.rosehc.sectors.sector.SectorType;
import pl.rosehc.sectors.sector.user.SectorUser;

public final class PlayerDeathListener implements Listener {

  private final PlatformPlugin plugin;

  public PlayerDeathListener(final PlatformPlugin plugin) {
    this.plugin = plugin;
  }

  @EventHandler
  public void onDeath(final PlayerDeathEvent event) {
    final Player player = event.getEntity();
    final SectorUser user = SectorsPlugin.getInstance().getSectorUserFactory()
        .findUserByPlayer(player);
    if (Objects.isNull(user)) {
      this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
        player.spigot().respawn();
        player.kickPlayer(ChatHelper.colored(SectorsPlugin.getInstance()
            .getSectorsConfiguration().messagesWrapper.playerProfileNotFound));
      }, 2L);
      return;
    }

    final PlayerInventory inventory = player.getInventory();
    final Runnable deathKickRunnable = () -> {
      if (this.plugin.getPlatformConfiguration().deathKicksState) {
        this.plugin.getServer().getScheduler().runTaskLaterAsynchronously(this.plugin,
            () -> this.plugin.getRedisAdapter().sendPacket(
                new PlatformUserKickPacket(player.getUniqueId(),
                    this.plugin.getPlatformConfiguration().messagesWrapper.deathKickInfo.replace(
                        "{SECTOR_NAME}",
                        SectorsPlugin.getInstance().getSectorFactory().getCurrentSector()
                            .getName())), "rhc_platform_" + user.getProxy().getIdentifier()), 5L);
      }
    };
    player.setHealth(player.getMaxHealth());
    player.setFoodLevel(20);
    player.setExp(0F);
    player.setLevel(0);
    inventory.clear();
    inventory.setArmorContents(new ItemStack[0]);
    inventory.setHeldItemSlot(0);
    user.setRedirecting(true);
    this.plugin.getPlatformUserFactory().findUserByUniqueId(player.getUniqueId())
        .filter(PlatformUser::isInCombat).ifPresent(platformUser -> {
          platformUser.setCombatTime(0L);
          PrioritizedActionBarPlugin.getInstance().getPrioritizedActionBarFactory()
              .updateActionBar(player.getUniqueId(), ChatHelper.colored(
                      this.plugin.getPlatformConfiguration().messagesWrapper.combatEndInfo),
                  PrioritizedActionBarConstants.ANTI_LOGOUT_ACTION_BAR_PRIORITY);
          this.plugin.getRedisAdapter().sendPacket(
              new PlatformUserCombatTimeUpdatePacket(platformUser.getUniqueId(),
                  platformUser.getCombatTime()), "rhc_master_controller", "rhc_platform");
        });
    this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
      final Optional<Sector> sectorOptional = SectorHelper.getRandomSector(SectorType.SPAWN);
      ItemHelper.addItems(player,
          this.plugin.getPlatformConfiguration().customItemsWrapper.respawnItemList.stream()
              .map(CustomItemWrapper::asItemStack).collect(Collectors.toList()));
      if (SectorsPlugin.getInstance().getSectorFactory().getCurrentSector().getType()
          .equals(SectorType.SPAWN) || !sectorOptional.isPresent()) {
        player.setFireTicks(0);
        player.getActivePotionEffects()
            .forEach(effect -> player.removePotionEffect(effect.getType()));
        user.setRedirecting(false);
        player.teleport(SectorsPlugin.getInstance().getSectorFactory().getCurrentSector().random());
        deathKickRunnable.run();
        return;
      }

      final SectorPlayerData data = SectorPlayerData.of(player);
      data.setLocation(!sectorOptional.filter(sector ->
          ((sector.getStatistics().getPlayers() * 100D)
              / this.plugin.getPlatformConfiguration().slotWrapper.spigotSlots) >= 60D).isPresent()
          ? this.plugin.getPlatformConfiguration().spawnLocationWrapper.unwrap()
          : sectorOptional.get().random());
      data.setPotionEffects(new PotionEffect[0]);
      data.setFireTicks(0);
      data.setHeldSlot(0);
      ConnectHelper.connect(player, user, data, sectorOptional.get(), deathKickRunnable,
          deathKickRunnable);
    }, 2L);
  }
}
