package pl.rosehc.controller.packet.platform.safe;

import java.util.UUID;
import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.PlatformPacketHandler;

public final class PlatformSafeDescriptionUpdatePacket extends Packet {

  private UUID uniqueId;
  private String description;

  private PlatformSafeDescriptionUpdatePacket() {
  }

  public PlatformSafeDescriptionUpdatePacket(final UUID uniqueId, final String description) {
    this.uniqueId = uniqueId;
    this.description = description;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((PlatformPacketHandler) handler).handle(this);
  }

  public UUID getUniqueId() {
    return this.uniqueId;
  }

  public String getDescription() {
    return this.description;
  }
}
