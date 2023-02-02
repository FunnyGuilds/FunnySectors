package pl.rosehc.controller.packet;

import java.util.stream.Collectors;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.MasterController;
import pl.rosehc.controller.configuration.ConfigurationHelper;
import pl.rosehc.controller.configuration.impl.configuration.SectorsConfiguration;
import pl.rosehc.controller.packet.proxy.ProxyInitializationRequestPacket;
import pl.rosehc.controller.packet.proxy.ProxyInitializationResponsePacket;
import pl.rosehc.controller.packet.proxy.ProxyUpdateStatisticsPacket;
import pl.rosehc.controller.sector.user.SectorUser;

public final class ProxyPacketHandler implements PacketHandler {

  private final MasterController masterController;

  public ProxyPacketHandler(final MasterController masterController) {
    this.masterController = masterController;
  }

  public void handle(final ProxyInitializationRequestPacket packet) {
    final SectorsConfiguration sectorsConfiguration = this.masterController.getConfigurationFactory()
        .findConfiguration(SectorsConfiguration.class);
    final ProxyInitializationResponsePacket responsePacket = new ProxyInitializationResponsePacket(
        packet.getProxyIdentifier(),
        this.masterController.getSectorUserFactory().getUserMap().values().stream()
            .map(SectorUser::wrap).collect(Collectors.toList()),
        ConfigurationHelper.serializeConfiguration(sectorsConfiguration));
    responsePacket.setCallbackId(packet.getCallbackId());
    responsePacket.setResponse(true);
    if (!this.masterController.getProxyFactory().findProxy(packet.getProxyIdentifier())
        .isPresent()) {
      responsePacket.setResponseText(
          "Nie znaleziono proxy o identyfikatorze " + packet.getProxyIdentifier() + "!");
      this.masterController.getRedisAdapter().sendPacket(responsePacket, "rhc_proxies");
      return;
    }

    responsePacket.setSuccess(true);
    this.masterController.getRedisAdapter().sendPacket(responsePacket, "rhc_proxies");
  }

  public void handle(final ProxyUpdateStatisticsPacket packet) {
    this.masterController.getProxyFactory().findProxy(packet.getProxyIdentifier())
        .ifPresent(proxy -> {
          proxy.setLoad(packet.getLoad());
          proxy.setPlayers(packet.getPlayers());
          proxy.setLastUpdate(System.currentTimeMillis());
        });
  }
}
