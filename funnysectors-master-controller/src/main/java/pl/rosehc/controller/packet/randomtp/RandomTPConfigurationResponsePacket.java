package pl.rosehc.controller.packet.randomtp;

import pl.rosehc.adapter.redis.callback.CallbackPacket;
import pl.rosehc.adapter.redis.packet.PacketHandler;

public final class RandomTPConfigurationResponsePacket extends CallbackPacket {

  private byte[] serializedConfigurationData;

  private RandomTPConfigurationResponsePacket() {
  }

  public RandomTPConfigurationResponsePacket(final byte[] serializedConfigurationData) {
    this.serializedConfigurationData = serializedConfigurationData;
  }

  @Override
  public void handle(final PacketHandler ignored) {
  }

  public byte[] getSerializedConfigurationData() {
    return this.serializedConfigurationData;
  }
}
