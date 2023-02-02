package pl.rosehc.controller.packet;

import pl.rosehc.controller.packet.configuration.ConfigurationSynchronizePacket;

@FunctionalInterface
public interface ConfigurationSynchronizePacketHandler {

  void handle(final ConfigurationSynchronizePacket packet);
}
