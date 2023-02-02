package pl.rosehc.controller.packet.trade;

import pl.rosehc.adapter.redis.callback.CallbackPacket;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.TradePacketHandler;

public final class TradeConfigurationRequestPacket extends CallbackPacket {

  private String sectorName;

  private TradeConfigurationRequestPacket() {
  }

  public TradeConfigurationRequestPacket(final String sectorName) {
    this.sectorName = sectorName;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((TradePacketHandler) handler).handle(this);
  }

  public String getSectorName() {
    return this.sectorName;
  }
}
