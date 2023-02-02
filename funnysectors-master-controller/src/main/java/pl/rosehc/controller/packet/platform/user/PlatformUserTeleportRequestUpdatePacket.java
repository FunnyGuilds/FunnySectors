package pl.rosehc.controller.packet.platform.user;

import java.util.UUID;
import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.PlatformPacketHandler;

public final class PlatformUserTeleportRequestUpdatePacket extends Packet {

  private UUID fromUniqueId;
  private UUID targetUniqueId;
  private boolean remove;

  private PlatformUserTeleportRequestUpdatePacket() {
  }

  public PlatformUserTeleportRequestUpdatePacket(final UUID fromUniqueId, final UUID targetUniqueId,
      final boolean remove) {
    this.fromUniqueId = fromUniqueId;
    this.targetUniqueId = targetUniqueId;
    this.remove = remove;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((PlatformPacketHandler) handler).handle(this);
  }

  public UUID getFromUniqueId() {
    return this.fromUniqueId;
  }

  public UUID getTargetUniqueId() {
    return this.targetUniqueId;
  }

  public boolean isRemove() {
    return this.remove;
  }
}
