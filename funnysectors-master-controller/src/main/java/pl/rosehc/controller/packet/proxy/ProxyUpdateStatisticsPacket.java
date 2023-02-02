package pl.rosehc.controller.packet.proxy;

import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.ProxyPacketHandler;

public final class ProxyUpdateStatisticsPacket extends Packet {

  private int proxyIdentifier;
  private double load;
  private int players;

  private ProxyUpdateStatisticsPacket() {
  }

  public ProxyUpdateStatisticsPacket(final int proxyIdentifier, final double load,
      final int players) {
    this.proxyIdentifier = proxyIdentifier;
    this.load = load;
    this.players = players;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((ProxyPacketHandler) handler).handle(this);
  }

  public int getProxyIdentifier() {
    return this.proxyIdentifier;
  }

  public double getLoad() {
    return this.load;
  }

  public int getPlayers() {
    return this.players;
  }
}
