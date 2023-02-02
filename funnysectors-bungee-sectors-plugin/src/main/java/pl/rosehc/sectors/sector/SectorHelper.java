package pl.rosehc.sectors.sector;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import pl.rosehc.sectors.SectorsPlugin;

/**
 * @author stevimeister on 06/01/2022
 **/
public final class SectorHelper {

  private SectorHelper() {
  }

  public static Optional<Sector> getRandomSector(final SectorType type,
      final Predicate<Sector> checker) {
    final List<Sector> sectorList = SectorsPlugin.getInstance().getSectorFactory().getSectorMap()
        .values().stream().filter(sector -> sector.getType().equals(type)).filter(checker)
        .collect(Collectors.toList());
    return !sectorList.isEmpty() ? Optional.of(
        sectorList.get(ThreadLocalRandom.current().nextInt(0, sectorList.size())))
        : Optional.empty();
  }

  public static Optional<Sector> getRandomSector(final SectorType type) {
    return getRandomSector(type,
        sector -> sector.getStatistics().isOnline() && sector.getStatistics().getLoad() < 68.8D
            && sector.getStatistics().getTps() > 5.58D);
  }
}