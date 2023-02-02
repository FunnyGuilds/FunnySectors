package pl.rosehc.controller.packet.platform.user;

import java.util.UUID;
import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.PlatformPacketHandler;

public final class PlatformUserIgnoredPlayerUpdatePacket extends Packet {

  private UUID senderUniqueId, targetUniqueId;
  private boolean add;

  private PlatformUserIgnoredPlayerUpdatePacket() {
  }

  public PlatformUserIgnoredPlayerUpdatePacket(final UUID senderUniqueId, final UUID targetUniqueId,
      final boolean add) {
    this.senderUniqueId = senderUniqueId;
    this.targetUniqueId = targetUniqueId;
    this.add = add;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((PlatformPacketHandler) handler).handle(this);
  }

  public UUID getSenderUniqueId() {
    return this.senderUniqueId;
  }

  public UUID getTargetUniqueId() {
    return this.targetUniqueId;
  }

  public boolean isAdd() {
    return this.add;
  }
}
