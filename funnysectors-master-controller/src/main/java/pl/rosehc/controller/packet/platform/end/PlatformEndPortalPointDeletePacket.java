package pl.rosehc.controller.packet.platform.end;

import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.PlatformPacketHandler;

public final class PlatformEndPortalPointDeletePacket extends Packet {

  private int pointId;

  private PlatformEndPortalPointDeletePacket() {
  }

  public PlatformEndPortalPointDeletePacket(final int pointId) {
    this.pointId = pointId;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((PlatformPacketHandler) handler).handle(this);
  }

  public int getPointId() {
    return this.pointId;
  }
}
