package pl.rosehc.sectors.sector;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import pl.rosehc.adapter.helper.NumberHelper;
import pl.rosehc.sectors.SectorsPlugin;

public final class Sector {

  private final String name;
  private final SectorType type;
  private final SectorStatistics statistics = new SectorStatistics();
  private final World world;
  private final int minX, maxX;
  private final int minZ, maxZ;
  private final int centerX, centerZ;

  public Sector(final String name, final SectorType type, final int minX, final int maxX,
      final int minZ, final int maxZ) {
    World world = Bukkit.getWorld(type.getWorldName());
    this.name = name;
    this.type = type;
    this.minX = minX;
    this.maxX = maxX;
    this.minZ = minZ;
    this.maxZ = maxZ;
    this.centerX = (minX + maxX) / 2;
    this.centerZ = (minZ + maxZ) / 2;
    if (world == null) {
      if (this.type.equals(SectorType.END)) {
        throw new UnsupportedOperationException("Sektor end musi posiadać świat!");
      }

      try {
        final Path serverDirectoryPath = new File(Bukkit.getServer().getWorldContainer(),
            this.type.getWorldName()).toPath();
        if (!Files.exists(serverDirectoryPath)) {
          Files.createDirectory(serverDirectoryPath);
        }

        world = Bukkit.getScheduler().callSyncMethod(SectorsPlugin.getInstance(),
            () -> new WorldCreator(this.type.getWorldName()).environment(Environment.NORMAL)
                .type(WorldType.FLAT).createWorld()).get(1L, TimeUnit.MINUTES);
      } catch (final Exception ex) {
        throw new UnsupportedOperationException("Nie można było stworzyć świata od sektora!", ex);
      }
    }

    this.world = world;
  }

  public Location random() {
    final Location location = new Location(this.world, NumberHelper.range(this.minX, this.maxX), 0D,
        NumberHelper.range(this.minZ, this.maxZ));
    location.setY(
        location.getWorld().getHighestBlockYAt((int) location.getX(), (int) location.getZ()));
    return location;
  }

  public String getName() {
    return this.name;
  }

  public SectorType getType() {
    return this.type;
  }

  public SectorStatistics getStatistics() {
    return this.statistics;
  }

  public World getWorld() {
    return this.world;
  }

  public int getMinX() {
    return this.minX;
  }

  public int getMaxX() {
    return this.maxX;
  }

  public int getMinZ() {
    return this.minZ;
  }

  public int getMaxZ() {
    return this.maxZ;
  }

  public int getCenterX() {
    return this.centerX;
  }

  public int getCenterZ() {
    return this.centerZ;
  }

  @Override
  public boolean equals(final Object object) {
    if (this == object) {
      return true;
    }

    if (object == null || this.getClass() != object.getClass()) {
      return false;
    }

    final Sector sector = (Sector) object;
    return this.name.equals(sector.name) && this.type == sector.type;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.name, this.type);
  }
}
