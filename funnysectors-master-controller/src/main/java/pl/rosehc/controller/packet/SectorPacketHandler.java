package pl.rosehc.controller.packet;

import java.util.Map.Entry;
import java.util.stream.Collectors;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.MasterController;
import pl.rosehc.controller.configuration.ConfigurationData;
import pl.rosehc.controller.configuration.ConfigurationHelper;
import pl.rosehc.controller.configuration.ConfigurationReloadHandler;
import pl.rosehc.controller.configuration.impl.configuration.SectorsConfiguration;
import pl.rosehc.controller.packet.configuration.ConfigurationReloadPacket;
import pl.rosehc.controller.packet.sector.SectorInitializationRequestPacket;
import pl.rosehc.controller.packet.sector.SectorInitializationResponsePacket;
import pl.rosehc.controller.packet.sector.SectorUpdateStatisticsPacket;
import pl.rosehc.controller.packet.sector.user.SectorUserCreatePacket;
import pl.rosehc.controller.packet.sector.user.SectorUserDeletePacket;
import pl.rosehc.controller.packet.sector.user.SectorUserUpdateSectorPacket;
import pl.rosehc.controller.sector.Sector;
import pl.rosehc.controller.sector.user.SectorUser;

public final class SectorPacketHandler implements PacketHandler {

  private final MasterController masterController;

  public SectorPacketHandler(final MasterController masterController) {
    this.masterController = masterController;
  }

  public void handle(final SectorInitializationRequestPacket packet) {
    final SectorsConfiguration sectorsConfiguration = this.masterController.getConfigurationFactory()
        .findConfiguration(SectorsConfiguration.class);
    final SectorInitializationResponsePacket responsePacket = new SectorInitializationResponsePacket(
        packet.getSectorName(),
        this.masterController.getSectorUserFactory().getUserMap().values().stream()
            .map(SectorUser::wrap).collect(Collectors.toList()),
        ConfigurationHelper.serializeConfiguration(sectorsConfiguration));
    responsePacket.setCallbackId(packet.getCallbackId());
    responsePacket.setResponse(true);
    if (!this.masterController.getSectorFactory().findSector(packet.getSectorName()).isPresent()) {
      responsePacket.setResponseText(
          "Nie znaleziono sektora o nazwie " + packet.getSectorName() + "!");
      this.masterController.getRedisAdapter().sendPacket(responsePacket, "rhc_sectors");
      return;
    }

    responsePacket.setSuccess(true);
    this.masterController.getRedisAdapter().sendPacket(responsePacket, "rhc_sectors");
  }

  public void handle(final ConfigurationReloadPacket packet) {
    for (final Entry<Class<?>, ConfigurationData> entry : this.masterController.getConfigurationFactory()
        .getCachedConfigurationMap().entrySet()) {
      if (entry.getValue().canReload) {
        final Class<? extends ConfigurationData> type = (Class<? extends ConfigurationData>) entry.getKey();
        final ConfigurationData configuration = ConfigurationHelper.load(entry.getValue().file,
            type);
        this.masterController.getConfigurationFactory().getCachedConfigurationMap()
            .remove(entry.getKey());
        this.masterController.getConfigurationFactory().getCachedConfigurationMap()
            .put(entry.getKey(), configuration);
        for (final ConfigurationReloadHandler reloadHandler : this.masterController.getConfigurationFactory()
            .findReloadHandlers(type)) {
          reloadHandler.handle(configuration);
        }
      }
    }
  }

  public void handle(final SectorUpdateStatisticsPacket packet) {
    this.masterController.getSectorFactory().findSector(packet.getSectorName())
        .map(Sector::getStatistics).ifPresent(sectorStatistics -> {
          sectorStatistics.setTps(packet.getTps());
          sectorStatistics.setLoad(packet.getLoad());
          sectorStatistics.setPlayers(packet.getPlayers());
          sectorStatistics.setLastUpdate(System.currentTimeMillis());
        });
  }

  public void handle(final SectorUserCreatePacket packet) {
    this.masterController.getSectorFactory().findSector(packet.getSectorName()).ifPresent(
        sector -> this.masterController.getProxyFactory().findProxy(packet.getProxyIdentifier())
            .ifPresent(proxy -> this.masterController.getSectorUserFactory().addUser(
                new SectorUser(packet.getUniqueId(), packet.getNickname(), proxy, sector))));
  }

  public void handle(final SectorUserUpdateSectorPacket packet) {
    this.masterController.getSectorUserFactory().findUserByUniqueId(packet.getUniqueId()).ifPresent(
        user -> this.masterController.getSectorFactory().findSector(packet.getSectorName())
            .ifPresent(user::setSector));
  }

  public void handle(final SectorUserDeletePacket packet) {
    this.masterController.getSectorUserFactory().findUserByUniqueId(packet.getUniqueId())
        .ifPresent(user -> this.masterController.getSectorUserFactory().removeUser(user));
  }
}
