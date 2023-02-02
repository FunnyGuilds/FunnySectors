package pl.rosehc.controller.packet;

import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.MasterController;
import pl.rosehc.controller.configuration.ConfigurationHelper;
import pl.rosehc.controller.configuration.impl.configuration.TradeConfiguration;
import pl.rosehc.controller.packet.trade.TradeConfigurationRequestPacket;
import pl.rosehc.controller.packet.trade.TradeConfigurationResponsePacket;

public final class TradePacketHandler implements PacketHandler {

  private final MasterController masterController;

  public TradePacketHandler(final MasterController masterController) {
    this.masterController = masterController;
  }

  public void handle(final TradeConfigurationRequestPacket packet) {
    final TradeConfigurationResponsePacket responsePacket = new TradeConfigurationResponsePacket(
        ConfigurationHelper.serializeConfiguration(this.masterController.getConfigurationFactory()
            .findConfiguration(TradeConfiguration.class)));
    responsePacket.setCallbackId(packet.getCallbackId());
    responsePacket.setResponse(true);
    responsePacket.setSuccess(true);
    this.masterController.getRedisAdapter()
        .sendPacket(responsePacket, "rhc_trade_" + packet.getSectorName());
  }
}
