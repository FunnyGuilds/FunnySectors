package pl.rosehc.platform.packet;

import java.util.Objects;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.adapter.helper.ItemHelper;
import pl.rosehc.adapter.helper.SerializeHelper;
import pl.rosehc.adapter.redis.callback.Callback;
import pl.rosehc.adapter.redis.callback.CallbackPacket;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.platform.PlatformPlugin;
import pl.rosehc.platform.packet.player.PlayerClearInventoryPacket;
import pl.rosehc.platform.packet.player.PlayerGiveMagicCasePacket;
import pl.rosehc.platform.packet.player.PlayerHealPacket;
import pl.rosehc.platform.packet.player.PlayerLocationRequestPacket;
import pl.rosehc.platform.packet.player.PlayerLocationResponsePacket;
import pl.rosehc.platform.packet.player.PlayerSelfTeleportPacket;
import pl.rosehc.sectors.SectorsPlugin;

public final class PlayerPacketHandler implements PacketHandler {

  private final PlatformPlugin plugin;

  public PlayerPacketHandler(final PlatformPlugin plugin) {
    this.plugin = plugin;
  }

  public void handle(final PlayerSelfTeleportPacket packet) {
    final Player player = this.plugin.getServer().getPlayer(packet.getToUniqueId());
    if (player != null) {
      final boolean timer = packet.isTimer();
      SectorsPlugin.getInstance().getSectorUserFactory()
          .findUserByUniqueId(packet.getFromUniqueId()).ifPresent(
              user -> this.plugin.getRedisAdapter().sendPacket(
                  new PlayerLocationRequestPacket(packet.getFromUniqueId(),
                      SectorsPlugin.getInstance().getSectorFactory().getCurrentSector().getName()),
                  new Callback() {

                    @Override
                    public void done(final CallbackPacket packet) {
                      if (player.isOnline()) {
                        final Location location = SerializeHelper.deserializeLocation(
                            ((PlayerLocationResponsePacket) packet).getLocation());
                        if (!timer) {
                          plugin.getServer().getScheduler()
                              .scheduleSyncDelayedTask(plugin, () -> player.teleport(location));
                        } else {
                          plugin.getTimerTaskFactory().addTimer(player, location, 10);
                        }
                      }
                    }

                    @Override
                    public void error(final String message) {
                      if (player.isOnline()) {
                        ChatHelper.sendMessage(player, message);
                      }
                    }
                  }, "rhc_platform_" + user.getSector().getName()));
    }
  }

  public void handle(final PlayerGiveMagicCasePacket packet) {
    final UUID targetUniqueId = packet.getTargetUniqueId();
    if (targetUniqueId != null) {
      final Player player = this.plugin.getServer().getPlayer(targetUniqueId);
      if (player != null) {
        this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin,
            () -> ItemHelper.addItem(player,
                this.plugin.getPlatformConfiguration().customItemsWrapper.magicCaseWrapper.asItemStack(
                    packet.getAmount())));
      }
    } else {
      this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
        for (final Player player : this.plugin.getServer().getOnlinePlayers()) {
          ItemHelper.addItem(player,
              this.plugin.getPlatformConfiguration().customItemsWrapper.magicCaseWrapper.asItemStack(
                  packet.getAmount()));
        }
      });
    }
  }

  public void handle(final PlayerLocationRequestPacket packet) {
    final Player player = this.plugin.getServer().getPlayer(packet.getUniqueId());
    final PlayerLocationResponsePacket responsePacket = new PlayerLocationResponsePacket(
        packet.getUniqueId());
    responsePacket.setResponse(true);
    responsePacket.setCallbackId(packet.getCallbackId());
    if (Objects.nonNull(player)) {
      responsePacket.setSuccess(true);
      responsePacket.setLocation(SerializeHelper.serializeLocation(player.getLocation()));
    } else {
      responsePacket.setResponseText(
          this.plugin.getPlatformConfiguration().messagesWrapper.playerLoggedOutWhileRequestingTheirLocation);
    }

    this.plugin.getRedisAdapter()
        .sendPacket(responsePacket, "rhc_platform_" + packet.getSectorName());
  }

  public void handle(final PlayerHealPacket packet) {
    final Player player = this.plugin.getServer().getPlayer(packet.getUniqueId());
    if (Objects.nonNull(player)) {
      player.setHealth(player.getMaxHealth());
      player.setFoodLevel(20);
      player.setFireTicks(0);
      player.getActivePotionEffects()
          .forEach(effect -> player.removePotionEffect(effect.getType()));
    }
  }

  public void handle(final PlayerClearInventoryPacket packet) {
    final Player player = this.plugin.getServer().getPlayer(packet.getUniqueId());
    if (Objects.nonNull(player)) {
      player.getInventory().clear();
      player.getInventory().setArmorContents(new ItemStack[4]);
      if (packet.isEnderchest()) {
        player.getEnderChest().clear();
      }
    }
  }
}
