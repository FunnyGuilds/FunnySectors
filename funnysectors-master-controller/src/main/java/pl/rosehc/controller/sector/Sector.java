package pl.rosehc.controller.sector;

public final class Sector {

  private final String name;
  private final SectorType type;
  private final SectorStatistics statistics = new SectorStatistics();
  private final int minX, maxX;
  private final int minZ, maxZ;

  public Sector(String name, SectorType type, int minX, int maxX, int minZ, int maxZ) {
    this.name = name;
    this.type = type;
    this.minX = minX;
    this.maxX = maxX;
    this.minZ = minZ;
    this.maxZ = maxZ;
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
}
