package pl.rosehc.controller.packet.platform;

import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.PlatformPacketHandler;

public final class PlatformAlertMessagePacket extends Packet {

  private String message;
  private boolean title;

  private PlatformAlertMessagePacket() {
  }

  public PlatformAlertMessagePacket(final String message, final boolean title) {
    this.message = message;
    this.title = title;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((PlatformPacketHandler) handler).handle(this);
  }

  public String getMessage() {
    return this.message;
  }

  public boolean isTitle() {
    return this.title;
  }
}
