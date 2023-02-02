package pl.rosehc.controller.packet.platform.user;

import java.util.UUID;
import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.PlatformPacketHandler;

public final class PlatformUserSetHomePacket extends Packet {

  private UUID uniqueId;
  private String homeLocation;
  private int homeId;

  private PlatformUserSetHomePacket() {
  }

  public PlatformUserSetHomePacket(final UUID uniqueId, final String homeLocation,
      final int homeId) {
    this.uniqueId = uniqueId;
    this.homeLocation = homeLocation;
    this.homeId = homeId;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((PlatformPacketHandler) handler).handle(this);
  }

  public UUID getUniqueId() {
    return this.uniqueId;
  }

  public String getHomeLocation() {
    return this.homeLocation;
  }

  public int getHomeId() {
    return this.homeId;
  }
}
