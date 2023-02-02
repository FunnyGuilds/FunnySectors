package pl.rosehc.controller.packet.platform.safe;

import java.util.UUID;
import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.PlatformPacketHandler;

public final class PlatformSafeContentsModifyPacket extends Packet {

  private UUID uniqueId;
  private byte[] contents;

  private PlatformSafeContentsModifyPacket() {
  }

  public PlatformSafeContentsModifyPacket(final UUID uniqueId, final byte[] contents) {
    this.uniqueId = uniqueId;
    this.contents = contents;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((PlatformPacketHandler) handler).handle(this);
  }

  public UUID getUniqueId() {
    return this.uniqueId;
  }

  public byte[] getContents() {
    return this.contents;
  }
}
