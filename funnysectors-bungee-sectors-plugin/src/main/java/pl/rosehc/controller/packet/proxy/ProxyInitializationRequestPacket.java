package pl.rosehc.controller.packet.proxy;

import pl.rosehc.adapter.redis.callback.CallbackPacket;
import pl.rosehc.adapter.redis.packet.PacketHandler;

public final class ProxyInitializationRequestPacket extends CallbackPacket {

  private int proxyIdentifier;

  private ProxyInitializationRequestPacket() {
  }

  public ProxyInitializationRequestPacket(final int proxyIdentifier) {
    this.proxyIdentifier = proxyIdentifier;
  }

  @Override
  public void handle(final PacketHandler handler) {
  }

  public int getProxyIdentifier() {
    return this.proxyIdentifier;
  }
}
