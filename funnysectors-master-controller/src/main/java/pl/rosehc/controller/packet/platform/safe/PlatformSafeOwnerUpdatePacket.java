package pl.rosehc.controller.packet.platform.safe;

import java.util.UUID;
import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.PlatformPacketHandler;

public final class PlatformSafeOwnerUpdatePacket extends Packet {

  private UUID uniqueId, ownerUniqueId;
  private String ownerNickname;

  private PlatformSafeOwnerUpdatePacket() {
  }

  public PlatformSafeOwnerUpdatePacket(final UUID uniqueId, final UUID ownerUniqueId,
      final String ownerNickname) {
    this.uniqueId = uniqueId;
    this.ownerUniqueId = ownerUniqueId;
    this.ownerNickname = ownerNickname;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((PlatformPacketHandler) handler).handle(this);
  }

  public UUID getUniqueId() {
    return this.uniqueId;
  }

  public UUID getOwnerUniqueId() {
    return this.ownerUniqueId;
  }

  public String getOwnerNickname() {
    return this.ownerNickname;
  }
}
