package pl.rosehc.controller.packet.platform.user;

import java.util.UUID;
import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.PlatformPacketHandler;
import pl.rosehc.controller.wrapper.platform.PlatformUserCooldownType;

public final class PlatformUserCooldownSynchronizePacket extends Packet {

  private UUID uniqueId;
  private PlatformUserCooldownType type;
  private long time;

  private PlatformUserCooldownSynchronizePacket() {
  }

  public PlatformUserCooldownSynchronizePacket(final UUID uniqueId,
      final PlatformUserCooldownType type, final long time) {
    this.uniqueId = uniqueId;
    this.type = type;
    this.time = time;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((PlatformPacketHandler) handler).handle(this);
  }

  public UUID getUniqueId() {
    return this.uniqueId;
  }

  public PlatformUserCooldownType getType() {
    return this.type;
  }

  public long getTime() {
    return this.time;
  }
}
