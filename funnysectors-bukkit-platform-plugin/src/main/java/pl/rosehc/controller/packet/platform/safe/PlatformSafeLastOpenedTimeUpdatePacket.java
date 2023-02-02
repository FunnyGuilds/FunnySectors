package pl.rosehc.controller.packet.platform.safe;

import java.util.UUID;
import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.PlatformPacketHandler;

public final class PlatformSafeLastOpenedTimeUpdatePacket extends Packet {

  private UUID uniqueId;
  private long lastOpenedTime;

  private PlatformSafeLastOpenedTimeUpdatePacket() {
  }

  public PlatformSafeLastOpenedTimeUpdatePacket(final UUID uniqueId, final long lastOpenedTime) {
    this.uniqueId = uniqueId;
    this.lastOpenedTime = lastOpenedTime;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((PlatformPacketHandler) handler).handle(this);
  }

  public UUID getUniqueId() {
    return this.uniqueId;
  }

  public long getLastOpenedTime() {
    return this.lastOpenedTime;
  }
}
