package pl.rosehc.platform.packet.player;

import java.util.UUID;
import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.platform.packet.PlayerPacketHandler;

public final class PlayerSelfTeleportPacket extends Packet {

  private UUID fromUniqueId, toUniqueId;
  private boolean timer;

  @SuppressWarnings("unused")
  private PlayerSelfTeleportPacket() {
  }

  public PlayerSelfTeleportPacket(final UUID fromUniqueId, final UUID toUniqueId,
      final boolean timer) {
    this.fromUniqueId = fromUniqueId;
    this.toUniqueId = toUniqueId;
    this.timer = timer;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((PlayerPacketHandler) handler).handle(this);
  }

  public UUID getFromUniqueId() {
    return this.fromUniqueId;
  }

  public UUID getToUniqueId() {
    return this.toUniqueId;
  }

  public boolean isTimer() {
    return this.timer;
  }
}