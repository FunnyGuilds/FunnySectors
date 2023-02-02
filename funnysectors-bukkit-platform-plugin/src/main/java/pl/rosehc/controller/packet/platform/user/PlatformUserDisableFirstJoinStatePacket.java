package pl.rosehc.controller.packet.platform.user;

import java.util.UUID;
import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.PlatformPacketHandler;

public final class PlatformUserDisableFirstJoinStatePacket extends Packet {

  private UUID uniqueId;

  private PlatformUserDisableFirstJoinStatePacket() {
  }

  public PlatformUserDisableFirstJoinStatePacket(final UUID uniqueId) {
    this.uniqueId = uniqueId;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((PlatformPacketHandler) handler).handle(this);
  }

  public UUID getUniqueId() {
    return this.uniqueId;
  }
}
