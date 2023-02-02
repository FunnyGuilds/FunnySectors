package pl.rosehc.controller.packet;

import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.MasterController;
import pl.rosehc.controller.configuration.ConfigurationHelper;
import pl.rosehc.controller.configuration.impl.configuration.ProtectionConfiguration;
import pl.rosehc.controller.packet.protection.ProtectionConfigurationRequestPacket;
import pl.rosehc.controller.packet.protection.ProtectionConfigurationResponsePacket;

public final class ProtectionPacketHandler implements PacketHandler {

  private final MasterController masterController;

  public ProtectionPacketHandler(final MasterController masterController) {
    this.masterController = masterController;
  }

  public void handle(final ProtectionConfigurationRequestPacket packet) {
    final ProtectionConfigurationResponsePacket responsePacket = new ProtectionConfigurationResponsePacket(
        ConfigurationHelper.serializeConfiguration(this.masterController.getConfigurationFactory()
            .findConfiguration(ProtectionConfiguration.class)));
    responsePacket.setCallbackId(packet.getCallbackId());
    responsePacket.setResponse(true);
    responsePacket.setSuccess(true);
    this.masterController.getRedisAdapter()
        .sendPacket(responsePacket, "rhc_protection_" + packet.getSectorName());
  }
}
