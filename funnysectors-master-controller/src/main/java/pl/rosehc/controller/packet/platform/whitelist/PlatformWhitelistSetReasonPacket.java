package pl.rosehc.controller.packet.platform.whitelist;

import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.PlatformPacketHandler;

public final class PlatformWhitelistSetReasonPacket extends Packet {

  private String reason;

  private PlatformWhitelistSetReasonPacket() {
  }

  public PlatformWhitelistSetReasonPacket(final String reason) {
    this.reason = reason;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((PlatformPacketHandler) handler).handle(this);
  }

  public String getReason() {
    return this.reason;
  }
}
