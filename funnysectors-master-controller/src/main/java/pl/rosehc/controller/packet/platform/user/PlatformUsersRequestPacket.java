package pl.rosehc.controller.packet.platform.user;

import pl.rosehc.adapter.redis.callback.CallbackPacket;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.PlatformPacketHandler;

public final class PlatformUsersRequestPacket extends CallbackPacket {

  private String from;

  private PlatformUsersRequestPacket() {
  }

  public PlatformUsersRequestPacket(final String from) {
    this.from = from;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((PlatformPacketHandler) handler).handle(this);
  }

  public String getFrom() {
    return this.from;
  }
}
