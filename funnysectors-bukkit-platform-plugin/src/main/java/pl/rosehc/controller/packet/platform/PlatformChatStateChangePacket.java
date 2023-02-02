package pl.rosehc.controller.packet.platform;

import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.PlatformPacketHandler;
import pl.rosehc.controller.wrapper.platform.PlatformChatStatusType;

public final class PlatformChatStateChangePacket extends Packet {

  private PlatformChatStatusType type;

  private PlatformChatStateChangePacket() {
  }

  public PlatformChatStateChangePacket(final PlatformChatStatusType type) {
    this.type = type;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((PlatformPacketHandler) handler).handle(this);
  }

  public PlatformChatStatusType getType() {
    return this.type;
  }
}
