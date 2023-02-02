package pl.rosehc.controller.packet.platform.ban;

import pl.rosehc.adapter.redis.callback.CallbackPacket;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.PlatformPacketHandler;

public final class PlatformBansRequestPacket extends CallbackPacket {

  private int proxyIdentifier;

  private PlatformBansRequestPacket() {
  }

  public PlatformBansRequestPacket(final int proxyIdentifier) {
    this.proxyIdentifier = proxyIdentifier;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((PlatformPacketHandler) handler).handle(this);
  }

  public int getProxyIdentifier() {
    return this.proxyIdentifier;
  }
}
