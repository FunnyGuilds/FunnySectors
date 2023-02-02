package pl.rosehc.controller.packet.platform.user;

import java.util.UUID;
import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.PlatformPacketHandler;

public final class PlatformUserReceiveKitPacket extends Packet {

  private UUID uniqueId;
  private String kitName;
  private long kitTime;

  private PlatformUserReceiveKitPacket() {
  }

  public PlatformUserReceiveKitPacket(final UUID uniqueId, final String kitName,
      final long kitTime) {
    this.uniqueId = uniqueId;
    this.kitName = kitName;
    this.kitTime = kitTime;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((PlatformPacketHandler) handler).handle(this);
  }

  public UUID getUniqueId() {
    return this.uniqueId;
  }

  public String getKitName() {
    return this.kitName;
  }

  public long getKitTime() {
    return this.kitTime;
  }
}
