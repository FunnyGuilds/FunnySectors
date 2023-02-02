package pl.rosehc.controller.packet;

import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.configuration.ConfigurationSynchronizePacket;

@FunctionalInterface
public interface ConfigurationSynchronizePacketHandler extends PacketHandler {

  void handle(final ConfigurationSynchronizePacket packet);
}
