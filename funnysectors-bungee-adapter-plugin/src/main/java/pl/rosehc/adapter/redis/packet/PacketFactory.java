package pl.rosehc.adapter.redis.packet;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.List;

/**
 * @author stevimeister on 17/06/2021
 **/
public final class PacketFactory {

  private final BiMap<Integer, Class<? extends Packet>> packets = HashBiMap.create();

  public void registerPackets(final List<Class<? extends Packet>> packetClasses) {
    for (final Class<? extends Packet> packetClass : packetClasses) {
      this.registerPacket0(packetClass);
    }
  }

  private void registerPacket0(final Class<? extends Packet> packetClass) {
    this.packets.put(this.packets.size() + 1, packetClass);
  }

  public Class<? extends Packet> findPacketById(final int id) {
    return this.packets.get(id);
  }

  public int findIdByPacket(final Class<? extends Packet> packetClass) {
    return this.packets.inverse().getOrDefault(packetClass, -1);
  }
}
