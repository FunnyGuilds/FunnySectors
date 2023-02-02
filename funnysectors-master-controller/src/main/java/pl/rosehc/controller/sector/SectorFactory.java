package pl.rosehc.controller.sector;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import pl.rosehc.controller.configuration.impl.configuration.SectorsConfiguration;

public final class SectorFactory {

  private final Map<String, Sector> sectorMap;

  public SectorFactory(final SectorsConfiguration sectorsConfiguration) {
    this.sectorMap = sectorsConfiguration.sectorMap.entrySet().stream().map(
        entry -> new Sector(entry.getKey(), entry.getValue().type, entry.getValue().minX,
            entry.getValue().maxX, entry.getValue().minZ, entry.getValue().maxZ)).collect(
        Collectors.toConcurrentMap(sector -> sector.getName().toLowerCase(), sector -> sector));
  }

  public void removeSector(final Sector sector) {
    this.sectorMap.remove(sector.getName().toLowerCase());
  }

  public void addSector(final Sector sector) {
    this.sectorMap.put(sector.getName().toLowerCase(), sector);
  }

  public Optional<Sector> findSector(final String name) {
    return Optional.ofNullable(this.sectorMap.get(name.toLowerCase()));
  }

  public Map<String, Sector> getSectorMap() {
    return this.sectorMap;
  }
}
