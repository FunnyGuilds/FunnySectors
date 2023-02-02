package pl.rosehc.controller.packet.trade;

import pl.rosehc.adapter.redis.callback.CallbackPacket;
import pl.rosehc.adapter.redis.packet.PacketHandler;

public final class TradeConfigurationResponsePacket extends CallbackPacket {

  private byte[] configurationData;

  private TradeConfigurationResponsePacket() {
  }

  public TradeConfigurationResponsePacket(final byte[] configurationData) {
    this.configurationData = configurationData;
  }

  @Override
  public void handle(final PacketHandler ignored) {
  }

  public byte[] getConfigurationData() {
    return this.configurationData;
  }
}
