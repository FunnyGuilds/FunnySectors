package pl.rosehc.controller.packet.configuration;

import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.SectorPacketHandler;

public final class ConfigurationReloadPacket extends Packet {

  @Override
  public void handle(final PacketHandler handler) {
    ((SectorPacketHandler) handler).handle(this);
  }
}
