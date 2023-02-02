package pl.rosehc.controller.packet.platform;

import pl.rosehc.adapter.redis.callback.CallbackPacket;
import pl.rosehc.adapter.redis.packet.PacketHandler;

public final class PlatformInitializationResponsePacket extends CallbackPacket {

  private byte[] platformConfigurationData;

  private PlatformInitializationResponsePacket() {
  }

  public PlatformInitializationResponsePacket(final byte[] platformConfigurationData) {
    this.platformConfigurationData = platformConfigurationData;
  }

  @Override
  public void handle(final PacketHandler ignored) {
  }

  public byte[] getPlatformConfigurationData() {
    return this.platformConfigurationData;
  }
}
