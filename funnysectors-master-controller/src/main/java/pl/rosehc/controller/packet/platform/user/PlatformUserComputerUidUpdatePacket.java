package pl.rosehc.controller.packet.platform.user;

import java.util.UUID;
import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.PlatformPacketHandler;

public final class PlatformUserComputerUidUpdatePacket extends Packet {

  private UUID uniqueId;
  private byte[] computerUid;

  private PlatformUserComputerUidUpdatePacket() {
  }

  public PlatformUserComputerUidUpdatePacket(final UUID uniqueId, final byte[] computerUid) {
    this.uniqueId = uniqueId;
    this.computerUid = computerUid;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((PlatformPacketHandler) handler).handle(this);
  }

  public UUID getUniqueId() {
    return this.uniqueId;
  }

  public byte[] getComputerUid() {
    return this.computerUid;
  }
}
