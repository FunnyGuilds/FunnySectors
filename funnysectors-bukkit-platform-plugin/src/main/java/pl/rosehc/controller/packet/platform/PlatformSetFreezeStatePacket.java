package pl.rosehc.controller.packet.platform;

import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.PlatformPacketHandler;

public final class PlatformSetFreezeStatePacket extends Packet {

  private boolean state;

  private PlatformSetFreezeStatePacket() {
  }

  public PlatformSetFreezeStatePacket(final boolean state) {
    this.state = state;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((PlatformPacketHandler) handler).handle(this);
  }

  public boolean getState() {
    return this.state;
  }
}
