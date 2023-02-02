package pl.rosehc.controller.packet;

import java.util.Map.Entry;
import java.util.Objects;
import pl.rosehc.adapter.helper.ConfigurationHelper;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.configuration.ConfigurationSynchronizePacket;
import pl.rosehc.controller.packet.proxy.ProxyUpdateStatisticsPacket;
import pl.rosehc.controller.packet.sector.SectorUpdateStatisticsPacket;
import pl.rosehc.controller.packet.sector.user.SectorUserCreatePacket;
import pl.rosehc.controller.packet.sector.user.SectorUserDeletePacket;
import pl.rosehc.controller.packet.sector.user.SectorUserUpdateSectorPacket;
import pl.rosehc.controller.wrapper.sector.SectorWrapper;
import pl.rosehc.sectors.SectorsConfiguration;
import pl.rosehc.sectors.SectorsPlugin;
import pl.rosehc.sectors.proxy.Proxy;
import pl.rosehc.sectors.sector.Sector;
import pl.rosehc.sectors.sector.SectorStatistics;
import pl.rosehc.sectors.sector.user.SectorUser;

public final class SectorPacketHandler implements PacketHandler,
    ConfigurationSynchronizePacketHandler {

  private final SectorsPlugin plugin;

  public SectorPacketHandler(final SectorsPlugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public void handle(final ConfigurationSynchronizePacket packet) {
    if (packet.getConfigurationName()
        .equals("pl.rosehc.controller.configuration.impl.configuration.SectorsConfiguration")
        && this.plugin.isLoaded()) {
      final SectorsConfiguration sectorsConfiguration = ConfigurationHelper.deserializeConfiguration(
          packet.getSerializedConfiguration(), SectorsConfiguration.class);
      this.plugin.setSectorsConfiguration(sectorsConfiguration);
      for (final Sector sector : this.plugin.getSectorFactory().getSectorMap().values()) {
        final SectorStatistics sectorStatistics = sector.getStatistics();
        final SectorWrapper newSectorWrapper = sectorsConfiguration.sectorMap.get(sector.getName());
        this.plugin.getSectorFactory().removeSector(sector);
        if (Objects.nonNull(newSectorWrapper)) {
          final Sector newSector = new Sector(sector.getName(), newSectorWrapper.type,
              newSectorWrapper.minX, newSectorWrapper.maxX, newSectorWrapper.minZ,
              newSectorWrapper.maxZ);
          final SectorStatistics newSectorStatistics = newSector.getStatistics();
          newSectorStatistics.setTps(sectorStatistics.getTps());
          newSectorStatistics.setLoad(sectorStatistics.getLoad());
          newSectorStatistics.setPlayers(sectorStatistics.getPlayers());
          newSectorStatistics.setLastUpdate(sectorStatistics.getLastUpdate());
          this.plugin.getSectorFactory().addSector(newSector);
        }
      }

      for (final Entry<String, SectorWrapper> entry : sectorsConfiguration.sectorMap.entrySet()) {
        if (!this.plugin.getSectorFactory().findSector(entry.getKey()).isPresent()) {
          final SectorWrapper sectorWrapper = entry.getValue();
          this.plugin.getSectorFactory().addSector(
              new Sector(entry.getKey(), sectorWrapper.type, sectorWrapper.minX, sectorWrapper.maxX,
                  sectorWrapper.minZ, sectorWrapper.maxZ));
        }
      }

      this.plugin.getSectorFactory().updateCurrentSector();
      this.plugin.getProxyFactory().getProxyMap().entrySet()
          .removeIf(entry -> !sectorsConfiguration.proxyList.contains(entry.getKey()));
      sectorsConfiguration.proxyList.forEach(
          proxyIdentifier -> this.plugin.getProxyFactory().addProxy(new Proxy(proxyIdentifier)));
    }
  }

  public void handle(final SectorUserCreatePacket packet) {
    if (this.plugin.isLoaded()) {
      this.plugin.getSectorFactory().findSector(packet.getSectorName()).ifPresent(
          sector -> this.plugin.getProxyFactory().findProxy(packet.getProxyIdentifier())
              .ifPresent(proxy -> {
                final SectorUser sectorUser = new SectorUser(packet.getUniqueId(),
                    packet.getNickname(), proxy, sector);
                if (this.plugin.getSectorFactory().getCurrentSector().equals(sector)) {
                  sectorUser.setFirstJoin(true);
                }

                this.plugin.getSectorUserFactory().addUser(sectorUser);
              }));
    }
  }

  public void handle(final SectorUpdateStatisticsPacket packet) {
    if (this.plugin.isLoaded()) {
      this.plugin.getSectorFactory().findSector(packet.getSectorName()).map(Sector::getStatistics)
          .ifPresent(sectorStatistics -> {
            sectorStatistics.setTps(packet.getTps());
            sectorStatistics.setLoad(packet.getLoad());
            sectorStatistics.setPlayers(packet.getPlayers());
            sectorStatistics.setLastUpdate(System.currentTimeMillis());
          });
    }
  }

  public void handle(final ProxyUpdateStatisticsPacket packet) {
    if (this.plugin.isLoaded()) {
      this.plugin.getProxyFactory().findProxy(packet.getProxyIdentifier()).ifPresent(proxy -> {
        proxy.setLoad(packet.getLoad());
        proxy.setPlayers(packet.getPlayers());
        proxy.setLastUpdate(System.currentTimeMillis());
      });
    }
  }

  public void handle(final SectorUserUpdateSectorPacket packet) {
    if (this.plugin.isLoaded()) {
      this.plugin.getSectorUserFactory().findUserByUniqueId(packet.getUniqueId()).ifPresent(
          user -> this.plugin.getSectorFactory().findSector(packet.getSectorName())
              .ifPresent(user::setSector));
    }
  }

  public void handle(final SectorUserDeletePacket packet) {
    if (this.plugin.isLoaded()) {
      this.plugin.getSectorUserFactory().findUserByUniqueId(packet.getUniqueId())
          .ifPresent(user -> this.plugin.getSectorUserFactory().removeUser(user));
    }
  }
}
