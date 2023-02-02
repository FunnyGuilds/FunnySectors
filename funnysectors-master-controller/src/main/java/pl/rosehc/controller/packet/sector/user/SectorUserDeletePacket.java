package pl.rosehc.controller.packet.sector.user;

import java.util.UUID;
import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.SectorPacketHandler;

public final class SectorUserDeletePacket extends Packet {

  private UUID uniqueId;

  private SectorUserDeletePacket() {
  }

  public SectorUserDeletePacket(final UUID uniqueId) {
    this.uniqueId = uniqueId;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((SectorPacketHandler) handler).handle(this);
  }

  public UUID getUniqueId() {
    return this.uniqueId;
  }
}
