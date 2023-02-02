package pl.rosehc.controller.packet.protection;

import pl.rosehc.adapter.redis.callback.CallbackPacket;
import pl.rosehc.adapter.redis.packet.PacketHandler;

public final class ProtectionConfigurationResponsePacket extends CallbackPacket {

  private byte[] configurationData;

  private ProtectionConfigurationResponsePacket() {
  }

  public ProtectionConfigurationResponsePacket(final byte[] configurationData) {
    this.configurationData = configurationData;
  }

  @Override
  public void handle(final PacketHandler ignored) {
  }

  public byte[] getConfigurationData() {
    return this.configurationData;
  }
}
