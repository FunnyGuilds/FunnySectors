package pl.rosehc.controller.packet.platform.end;

import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.PlatformPacketHandler;
import pl.rosehc.controller.wrapper.platform.PlatformEndPortalPointWrapper;

public final class PlatformEndPortalPointCreatePacket extends Packet {

  private PlatformEndPortalPointWrapper wrapper;

  private PlatformEndPortalPointCreatePacket() {
  }

  public PlatformEndPortalPointCreatePacket(final PlatformEndPortalPointWrapper wrapper) {
    this.wrapper = wrapper;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((PlatformPacketHandler) handler).handle(this);
  }

  public PlatformEndPortalPointWrapper getWrapper() {
    return this.wrapper;
  }
}
