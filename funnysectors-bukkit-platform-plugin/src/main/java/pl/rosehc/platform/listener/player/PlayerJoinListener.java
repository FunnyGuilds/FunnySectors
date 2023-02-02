package pl.rosehc.platform.listener.player;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.adapter.helper.ItemHelper;
import pl.rosehc.adapter.helper.TimeHelper;
import pl.rosehc.bossbar.BossBarBuilder;
import pl.rosehc.bossbar.BossBarPlugin;
import pl.rosehc.bossbar.user.UserBar;
import pl.rosehc.bossbar.user.UserBarConstants;
import pl.rosehc.bossbar.user.UserBarType;
import pl.rosehc.controller.packet.platform.safe.PlatformSafeCreatePacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserDisableFirstJoinStatePacket;
import pl.rosehc.controller.wrapper.platform.PlatformUserCooldownType;
import pl.rosehc.platform.PlatformConfiguration.CustomItemsWrapper.CustomItemWrapper;
import pl.rosehc.platform.PlatformConfiguration.SpecialBossBarWrapper;
import pl.rosehc.platform.PlatformPlugin;
import pl.rosehc.platform.deposit.DepositHelper;
import pl.rosehc.platform.end.EndPortalPoint;
import pl.rosehc.platform.hologram.HologramChannelHandler;
import pl.rosehc.platform.safe.Safe;
import pl.rosehc.platform.user.PlatformUser;
import pl.rosehc.sectors.SectorsPlugin;
import pl.rosehc.sectors.sector.SectorType;
import pl.rosehc.waypoint.WaypointConstants;
import pl.rosehc.waypoint.WaypointHelper;

public final class PlayerJoinListener implements Listener {

  private final PlatformPlugin plugin;

  public PlayerJoinListener(final PlatformPlugin plugin) {
    this.plugin = plugin;
  }

  @EventHandler
  public void onJoin(final PlayerJoinEvent event) {
    event.setJoinMessage(null);
    final Player player = event.getPlayer();
    this.plugin.getPlatformUserFactory().findUserByUniqueId(player.getUniqueId())
        .ifPresent(user -> {
          if (user.isFirstJoin()) {
            final List<ItemStack> itemStackList = new ArrayList<>();
            for (final CustomItemWrapper wrapper : this.plugin.getPlatformConfiguration().customItemsWrapper.firstJoinItemList) {
              itemStackList.add(wrapper.asItemStack());
            }

            final UUID safeUniqueId = UUID.nameUUIDFromBytes(
                ("Safe-Owner:" + player.getName()).getBytes(StandardCharsets.UTF_8));
            final Safe safe = this.plugin.getSafeFactory().findSafe(safeUniqueId).orElse(
                new Safe(safeUniqueId, player.getUniqueId(), player.getName(),
                    System.currentTimeMillis()));
            ItemHelper.addItem(player,
                this.plugin.getPlatformConfiguration().customItemsWrapper.safeItemWrapper.asItemStack(
                    safe));
            this.plugin.getSafeFactory().addSafe(safe);
            this.plugin.getRedisAdapter().sendPacket(
                new PlatformSafeCreatePacket(safeUniqueId, safe.getOwnerUniqueId(),
                    safe.getOwnerNickname(), safe.getCreationTime()), "rhc_master_controller",
                "rhc_platform");
            ItemHelper.addItems(player, itemStackList);
            player.teleport(
                SectorsPlugin.getInstance().getSectorFactory().getCurrentSector().random());
            this.plugin.getRedisAdapter()
                .sendPacket(new PlatformUserDisableFirstJoinStatePacket(player.getUniqueId()),
                    "rhc_master_controller", "rhc_platform");
          }

          user.getCooldownCache().putUserCooldown(PlatformUserCooldownType.SECTOR_CHANGE);
          user.reloadPermissions(player);
          for (final Player sender : this.plugin.getServer().getOnlinePlayers()) {
            if (this.plugin.getPlatformUserFactory().findUserByUniqueId(sender.getUniqueId())
                .filter(PlatformUser::isVanish).isPresent()) {
              for (final Player receiver : Bukkit.getOnlinePlayers()) {
                if (!receiver.equals(sender) && !receiver.hasPermission("platform-vanish-bypass")) {
                  receiver.hidePlayer(sender);
                }
              }
            }
          }

          this.plugin.getServer().getScheduler().runTaskLaterAsynchronously(this.plugin,
              () -> ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel.pipeline()
                  .addBefore("packet_handler", "hologram_channel_handler",
                      new HologramChannelHandler(this.plugin, player)), 1L);
        });

    if (player.isDead()) {
      this.plugin.getServer().getScheduler()
          .scheduleSyncDelayedTask(this.plugin, player.spigot()::respawn, 2L);
      return;
    }

    final SpecialBossBarWrapper specialBossBarWrapper = this.plugin.getPlatformConfiguration().specialBossBarWrapper;
    if (Objects.nonNull(specialBossBarWrapper) && (specialBossBarWrapper.expiryTime == 0L
        || specialBossBarWrapper.expiryTime > System.currentTimeMillis())) {
      final UserBar userBar = BossBarPlugin.getInstance().getUserBarFactory().getUserBar(player);
      final long delta = specialBossBarWrapper.expiryTime - System.currentTimeMillis();
      userBar.addBossBar(UserBarType.SPECIAL_BAR,
          BossBarBuilder.add(UserBarConstants.SPECIAL_BAR_UUID)
              .color(specialBossBarWrapper.barColorWrapper.toOriginal())
              .style(specialBossBarWrapper.barStyleWrapper.toOriginal()).progress(
                  specialBossBarWrapper.expiryTime != 0L ? Math.min(
                      TimeUnit.MILLISECONDS.toSeconds(delta) / specialBossBarWrapper.expiryMaxBars, 1F)
                      : 1F).title(TextComponent.fromLegacyText(ChatHelper.colored(
                  specialBossBarWrapper.expiryTime != 0L ? specialBossBarWrapper.title.replace("{TIME}",
                      TimeHelper.timeToString(delta)) : specialBossBarWrapper.title))));
    }

    if (SectorsPlugin.getInstance().getSectorFactory().getCurrentSector().getType()
        .equals(SectorType.END)) {
      final EndPortalPoint currentPortalPoint = PlatformPlugin.getInstance().getEndPointFactory()
          .getCurrentPortalPoint();
      if (Objects.nonNull(currentPortalPoint)) {
        WaypointHelper.createWaypoint(player,
            currentPortalPoint.getCenterLocation().clone().add(0D, 3D, 0D),
            WaypointConstants.PORTAL_POINT_WAYPOINT_ID, "PORTAL",
            WaypointConstants.PORTAL_POINT_WAYPOINT_COLOR,
            WaypointConstants.PORTAL_POINT_WAYPOINT_ASSET_SHA,
            WaypointConstants.PORTAL_POINT_WAYPOINT_ASSET_ID, Long.MAX_VALUE);
      }
    }

    if (!SectorsPlugin.getInstance().getSectorFactory().getCurrentSector().getType()
        .equals(SectorType.SPAWN)) {
      DepositHelper.limit(player);
    }
  }

  @EventHandler
  public void onSpawnLocation(final PlayerSpawnLocationEvent event) {
    this.plugin.getPlatformUserFactory().findUserByUniqueId(event.getPlayer().getUniqueId())
        .filter(PlatformUser::isFirstJoin).ifPresent(ignored -> event.setSpawnLocation(
            SectorsPlugin.getInstance().getSectorFactory().getCurrentSector().random()));
  }
}
