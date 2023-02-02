package pl.rosehc.controller.packet.auth.user;

import java.util.UUID;
import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;

public final class AuthQueueAddPacket extends Packet {

  private UUID uniqueId;
  private String sectorName;
  private int priority;

  private AuthQueueAddPacket() {
  }

  public AuthQueueAddPacket(final UUID uniqueId, final String sectorName, final int priority) {
    this.uniqueId = uniqueId;
    this.sectorName = sectorName;
    this.priority = priority;
  }

  @Override
  public void handle(final PacketHandler ignored) {
  }

  public UUID getUniqueId() {
    return this.uniqueId;
  }

  public String getSectorName() {
    return this.sectorName;
  }

  public int getPriority() {
    return this.priority;
  }
}
