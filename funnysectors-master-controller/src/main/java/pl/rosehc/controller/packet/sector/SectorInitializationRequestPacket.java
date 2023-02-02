package pl.rosehc.controller.packet.sector;

import pl.rosehc.adapter.redis.callback.CallbackPacket;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.SectorPacketHandler;

public final class SectorInitializationRequestPacket extends CallbackPacket {

  private String sectorName;

  private SectorInitializationRequestPacket() {
  }

  public SectorInitializationRequestPacket(final String sectorName) {
    this.sectorName = sectorName;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((SectorPacketHandler) handler).handle(this);
  }

  public String getSectorName() {
    return this.sectorName;
  }
}
