package pl.rosehc.controller.packet.platform.safe.request;

import pl.rosehc.adapter.redis.callback.CallbackPacket;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.PlatformPacketHandler;

public final class PlatformSafeDataRequestPacket extends CallbackPacket {

  private String sectorName;

  private PlatformSafeDataRequestPacket() {
  }

  public PlatformSafeDataRequestPacket(final String sectorName) {
    this.sectorName = sectorName;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((PlatformPacketHandler) handler).handle(this);
  }

  public String getSectorName() {
    return this.sectorName;
  }
}
