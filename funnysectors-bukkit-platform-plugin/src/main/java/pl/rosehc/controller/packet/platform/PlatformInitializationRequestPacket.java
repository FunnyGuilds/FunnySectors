package pl.rosehc.controller.packet.platform;

import pl.rosehc.adapter.redis.callback.CallbackPacket;
import pl.rosehc.adapter.redis.packet.PacketHandler;

public final class PlatformInitializationRequestPacket extends CallbackPacket {

  private String from;

  private PlatformInitializationRequestPacket() {
  }

  public PlatformInitializationRequestPacket(final String from) {
    this.from = from;
  }

  @Override
  public void handle(final PacketHandler ignored) {
  }

  public String getFrom() {
    return this.from;
  }
}
