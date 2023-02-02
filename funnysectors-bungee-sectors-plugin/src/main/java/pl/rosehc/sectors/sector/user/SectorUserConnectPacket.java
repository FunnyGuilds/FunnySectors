package pl.rosehc.sectors.sector.user;

import java.util.UUID;
import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.SectorPacketHandler;

public final class SectorUserConnectPacket extends Packet {

  private UUID uniqueId;
  private String sectorName;

  private SectorUserConnectPacket() {
  }

  public SectorUserConnectPacket(final UUID uniqueId, final String sectorName) {
    this.uniqueId = uniqueId;
    this.sectorName = sectorName;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((SectorPacketHandler) handler).handle(this);
  }

  public UUID getUniqueId() {
    return this.uniqueId;
  }

  public String getSectorName() {
    return this.sectorName;
  }
}
