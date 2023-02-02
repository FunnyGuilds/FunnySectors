package pl.rosehc.sectors.sector;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import pl.rosehc.sectors.SectorsConfiguration;

public final class SectorFactory {

  private final Map<String, Sector> sectorMap;
  private final String currentSectorName;
  private volatile Sector currentSector;

  public SectorFactory(final SectorsConfiguration sectorsConfiguration,
      final String currentSectorName) {
    this.sectorMap = sectorsConfiguration.sectorMap.entrySet().stream().map(
        entry -> new Sector(entry.getKey(), entry.getValue().type, entry.getValue().minX,
            entry.getValue().maxX, entry.getValue().minZ, entry.getValue().maxZ)).collect(
        Collectors.toConcurrentMap(sector -> sector.getName().toLowerCase(), sector -> sector));
    this.currentSectorName = currentSectorName;
    this.updateCurrentSector();
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

  public Sector getCurrentSector() {
    return this.currentSector;
  }

  public synchronized void updateCurrentSector() {
    this.currentSector = this.findSector(this.currentSectorName).orElseThrow(
        () -> new UnsupportedOperationException(
            "Aktualny sektor nie istnieje? (" + this.currentSectorName + ")"));
  }

  public Map<String, Sector> getSectorMap() {
    return this.sectorMap;
  }
}
