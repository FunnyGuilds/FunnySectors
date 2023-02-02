package pl.rosehc.sectors.sector;

import java.util.Optional;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

public final class Sector {

  private final String name;
  private final SectorType type;
  private final SectorStatistics statistics = new SectorStatistics();
  private final int minX, maxX;
  private final int minZ, maxZ;
  private final int centerX, centerZ;

  public Sector(String name, SectorType type, int minX, int maxX, int minZ, int maxZ) {
    this.name = name;
    this.type = type;
    this.minX = minX;
    this.maxX = maxX;
    this.minZ = minZ;
    this.maxZ = maxZ;
    this.centerX = (minX + maxX) / 2;
    this.centerZ = (minZ + maxZ) / 2;
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

  public Optional<ServerInfo> getServerInfo() {
    return Optional.ofNullable(ProxyServer.getInstance().getServerInfo(this.name));
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
}
