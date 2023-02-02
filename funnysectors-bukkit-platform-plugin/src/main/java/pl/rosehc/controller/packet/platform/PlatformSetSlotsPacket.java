package pl.rosehc.controller.packet.platform;

import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.PlatformPacketHandler;

public final class PlatformSetSlotsPacket extends Packet {

  private int slots;
  private boolean proxy;

  private PlatformSetSlotsPacket() {
  }

  public PlatformSetSlotsPacket(final int slots, final boolean proxy) {
    this.slots = slots;
    this.proxy = proxy;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((PlatformPacketHandler) handler).handle(this);
  }

  public int getSlots() {
    return this.slots;
  }

  public boolean isProxy() {
    return this.proxy;
  }
}
