package pl.rosehc.controller.packet.platform.user;

import java.util.UUID;
import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.PlatformPacketHandler;

public final class PlatformUserRankUpdatePacket extends Packet {

  private UUID uniqueId;
  private String previousRankName, currentRankName;
  private long expirationTime;

  private PlatformUserRankUpdatePacket() {
  }

  public PlatformUserRankUpdatePacket(final UUID uniqueId, final String previousRankName,
      final String currentRankName, final long expirationTime) {
    this.uniqueId = uniqueId;
    this.previousRankName = previousRankName;
    this.currentRankName = currentRankName;
    this.expirationTime = expirationTime;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((PlatformPacketHandler) handler).handle(this);
  }

  public UUID getUniqueId() {
    return this.uniqueId;
  }

  public String getPreviousRankName() {
    return this.previousRankName;
  }

  public String getCurrentRankName() {
    return this.currentRankName;
  }

  public long getExpirationTime() {
    return this.expirationTime;
  }
}
