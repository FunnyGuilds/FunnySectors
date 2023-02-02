package pl.rosehc.platform.disco.task;

import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import pl.rosehc.platform.PlatformPlugin;
import pl.rosehc.platform.disco.DiscoArmorWrapper;

public final class DiscoUserUpdateTask implements Runnable {

  private final PlatformPlugin plugin;

  public DiscoUserUpdateTask(final PlatformPlugin plugin) {
    this.plugin = plugin;
    this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(this.plugin, this, 20L, 20L);
  }

  @Override
  public void run() {
    for (final Player sender : Bukkit.getOnlinePlayers()) {
      this.plugin.getPlatformUserFactory().findUserByUniqueId(sender.getUniqueId())
          .filter(user -> user.getSelectedDiscoEffectType() != null).ifPresent(user -> {
            final PlayerConnection senderConnection = ((CraftPlayer) sender).getHandle().playerConnection;
            final List<DiscoArmorWrapper> wrapperList = user.getSelectedDiscoEffectType().getEffect()
                .getWrapperList();
            if (sender.isSneaking()) {
              wrapperList.stream().map(wrapper -> wrapper.unwrap(sender, 4))
                  .collect(Collectors.toList()).forEach(senderConnection::sendPacket);
              user.setSneaking(true);
            } else {
              DiscoArmorWrapper.wrap(sender.getInventory()).stream()
                  .map(wrapper -> wrapper.unwrap(sender, 0)).forEach(senderConnection::sendPacket);
              user.setSneaking(false);
            }

            final List<PacketPlayOutEntityEquipment> receiverPacketList = wrapperList.stream()
                .map(wrapper -> wrapper.unwrap(sender, 5)).collect(Collectors.toList());
            for (final Player receiver : Bukkit.getOnlinePlayers()) {
              if (!receiver.equals(sender)) {
                final PlayerConnection receiverConnection = ((CraftPlayer) receiver).getHandle().playerConnection;
                receiverPacketList.forEach(receiverConnection::sendPacket);
              }
            }
          });
    }
  }
}
