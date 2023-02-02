package pl.rosehc.controller.configuration.impl.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.UUID;
import pl.rosehc.controller.MasterController;
import pl.rosehc.controller.configuration.ConfigurationHelper;
import pl.rosehc.controller.configuration.ConfigurationReloadHandler;
import pl.rosehc.controller.configuration.impl.configuration.SectorsConfiguration;
import pl.rosehc.controller.configuration.impl.configuration.SectorsConfiguration.SectorWrapper;
import pl.rosehc.controller.packet.configuration.ConfigurationSynchronizePacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserKickPacket;
import pl.rosehc.controller.packet.sector.user.SectorUserUpdateSectorPacket;
import pl.rosehc.controller.proxy.Proxy;
import pl.rosehc.controller.sector.Sector;
import pl.rosehc.controller.sector.SectorStatistics;
import pl.rosehc.controller.sector.user.SectorUser;

public final class SectorsConfigurationReloadHandler implements
    ConfigurationReloadHandler<SectorsConfiguration> {

  private final MasterController masterController;

  public SectorsConfigurationReloadHandler(final MasterController masterController) {
    this.masterController = masterController;
  }

  @Override
  public void handle(final SectorsConfiguration configuration) {
    final Map<UUID, String> usersSectorsToUpdateMap = new HashMap<>();
    for (final Sector sector : this.masterController.getSectorFactory().getSectorMap().values()) {
      final SectorStatistics sectorStatistics = sector.getStatistics();
      final SectorWrapper newSectorWrapper = configuration.sectorMap.get(sector.getName());
      this.masterController.getSectorFactory().removeSector(sector);
      if (Objects.nonNull(newSectorWrapper)) {
        final Sector newSector = new Sector(sector.getName(), newSectorWrapper.type,
            newSectorWrapper.minX, newSectorWrapper.maxX, newSectorWrapper.minZ,
            newSectorWrapper.maxZ);
        final SectorStatistics newSectorStatistics = newSector.getStatistics();
        newSectorStatistics.setTps(sectorStatistics.getTps());
        newSectorStatistics.setLoad(sectorStatistics.getLoad());
        newSectorStatistics.setPlayers(sectorStatistics.getPlayers());
        newSectorStatistics.setLastUpdate(sectorStatistics.getLastUpdate());
        this.masterController.getSectorFactory().addSector(newSector);
        for (final SectorUser user : this.masterController.getSectorUserFactory().getUserMap()
            .values()) {
          user.setSector(newSector);
          usersSectorsToUpdateMap.put(user.getUniqueId(), user.getSector().getName());
        }
      } else {
        for (final SectorUser user : this.masterController.getSectorUserFactory().getUserMap()
            .values()) {
          this.masterController.getRedisAdapter()
              .sendPacket(new PlatformUserKickPacket(user.getUniqueId(), "&cSectors error."),
                  "rhc_platform_" + user.getProxy().getIdentifier());
        }
      }
    }

    for (final Entry<String, SectorWrapper> entry : configuration.sectorMap.entrySet()) {
      if (!this.masterController.getSectorFactory().findSector(entry.getKey()).isPresent()) {
        final SectorWrapper sectorWrapper = entry.getValue();
        this.masterController.getSectorFactory().addSector(
            new Sector(entry.getKey(), sectorWrapper.type, sectorWrapper.minX, sectorWrapper.maxX,
                sectorWrapper.minZ, sectorWrapper.maxZ));
      }
    }

    this.masterController.getProxyFactory().getProxyMap().entrySet()
        .removeIf(entry -> !configuration.proxyList.contains(entry.getKey()));
    configuration.proxyList.forEach(proxyIdentifier -> this.masterController.getProxyFactory()
        .addProxy(new Proxy(proxyIdentifier)));
    this.masterController.getRedisAdapter().sendPacket(
        new ConfigurationSynchronizePacket(configuration.getClass().getName(),
            ConfigurationHelper.serializeConfiguration(configuration)), "rhc_global");
    for (final Entry<UUID, String> entry : usersSectorsToUpdateMap.entrySet()) {
      this.masterController.getRedisAdapter()
          .sendPacket(new SectorUserUpdateSectorPacket(entry.getKey(), entry.getValue()),
              "rhc_global");
    }
  }
}
