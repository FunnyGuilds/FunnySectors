package pl.rosehc.controller.packet.platform;

import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.PlatformPacketHandler;

public final class PlatformSetMotdCounterPlayerLimitPacket extends Packet {

  private int limit;

  private PlatformSetMotdCounterPlayerLimitPacket() {
  }

  public PlatformSetMotdCounterPlayerLimitPacket(final int limit) {
    this.limit = limit;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((PlatformPacketHandler) handler).handle(this);
  }

  public int getLimit() {
    return this.limit;
  }
}
