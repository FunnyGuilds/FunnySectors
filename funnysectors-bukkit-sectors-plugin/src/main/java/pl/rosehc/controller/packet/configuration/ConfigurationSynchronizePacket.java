package pl.rosehc.controller.packet.configuration;

import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.ConfigurationSynchronizePacketHandler;

public final class ConfigurationSynchronizePacket extends Packet {

  private String configurationName;
  private byte[] serializedConfiguration;

  private ConfigurationSynchronizePacket() {
  }

  public ConfigurationSynchronizePacket(final String configurationName,
      final byte[] serializedConfiguration) {
    this.configurationName = configurationName;
    this.serializedConfiguration = serializedConfiguration;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((ConfigurationSynchronizePacketHandler) handler).handle(this);
  }

  public String getConfigurationName() {
    return this.configurationName;
  }

  public byte[] getSerializedConfiguration() {
    return this.serializedConfiguration;
  }
}
