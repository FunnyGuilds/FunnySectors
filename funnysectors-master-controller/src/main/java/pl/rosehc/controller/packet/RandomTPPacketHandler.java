package pl.rosehc.controller.packet;

import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.MasterController;
import pl.rosehc.controller.configuration.ConfigurationData;
import pl.rosehc.controller.configuration.ConfigurationHelper;
import pl.rosehc.controller.configuration.impl.configuration.LinkerRandomTPConfiguration;
import pl.rosehc.controller.configuration.impl.configuration.SystemRandomTPConfiguration;
import pl.rosehc.controller.packet.randomtp.RandomTPConfigurationRequestPacket;
import pl.rosehc.controller.packet.randomtp.RandomTPConfigurationResponsePacket;

public final class RandomTPPacketHandler implements PacketHandler {

  private final MasterController masterController;

  public RandomTPPacketHandler(final MasterController masterController) {
    this.masterController = masterController;
  }

  public void handle(final RandomTPConfigurationRequestPacket packet) {
    final ConfigurationData configurationData = this.masterController.getConfigurationFactory()
        .findConfiguration((Class<? extends ConfigurationData>) (packet.isLinker()
            ? LinkerRandomTPConfiguration.class : SystemRandomTPConfiguration.class));
    final RandomTPConfigurationResponsePacket responsePacket = new RandomTPConfigurationResponsePacket(
        ConfigurationHelper.serializeConfiguration(configurationData));
    responsePacket.setCallbackId(packet.getCallbackId());
    responsePacket.setResponse(true);
    responsePacket.setSuccess(true);
    this.masterController.getRedisAdapter()
        .sendPacket(responsePacket, "rhc_rtp_" + packet.getSectorName());
  }
}
