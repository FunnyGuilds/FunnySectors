package pl.rosehc.adapter.helper;

import java.util.Arrays;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * @author stevimeister on 09/01/2022
 **/
public final class PacketHelper {

  private PacketHelper() {
  }

  public static void sendPacket(final Player player, final Packet<?>... packets) {
    Arrays.stream(packets)
        .forEach(packet -> ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet));
  }
}
