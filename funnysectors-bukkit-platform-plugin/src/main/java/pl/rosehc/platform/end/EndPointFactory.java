package pl.rosehc.platform.end;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import pl.rosehc.adapter.helper.NumberHelper;
import pl.rosehc.controller.wrapper.platform.PlatformEndPortalPointWrapper;
import pl.rosehc.platform.PlatformPlugin;
import pl.rosehc.platform.user.PlatformUserSectorSpoofHelper;
import pl.rosehc.sectors.helper.SectorHelper;
import pl.rosehc.sectors.sector.Sector;
import pl.rosehc.sectors.sector.SectorType;
import pl.rosehc.waypoint.WaypointConstants;
import pl.rosehc.waypoint.WaypointHelper;

public final class EndPointFactory {

  private final PlatformPlugin plugin;
  private volatile Location currentTeleportationPoint;
  private volatile EndPortalPoint currentPortalPoint;
  private volatile long lastTeleportationPointUpdateTime, lastPortalPointUpdateTime;

  public EndPointFactory(final PlatformPlugin plugin) {
    this.plugin = plugin;
  }

  public synchronized Location getCurrentTeleportationPoint() {
    return this.currentTeleportationPoint;
  }

  public synchronized EndPortalPoint getCurrentPortalPoint() {
    return this.currentPortalPoint;
  }

  public synchronized long getLastTeleportationPointUpdateTime() {
    return this.lastTeleportationPointUpdateTime;
  }

  public synchronized long getLastPortalPointUpdateTime() {
    return this.lastPortalPointUpdateTime;
  }

  public synchronized void updatePoints() {
    portalPoint:
    {
      final List<PlatformEndPortalPointWrapper> endPortalPointWrapperList = new ArrayList<>(
          this.plugin.getPlatformConfiguration().endPortalPointWrapperList);
      if (endPortalPointWrapperList.isEmpty()) {
        this.lastPortalPointUpdateTime = 0L;
        this.currentPortalPoint = null;
        break portalPoint;
      }

      if (this.lastPortalPointUpdateTime <= System.currentTimeMillis()) {
        if (Objects.nonNull(this.currentPortalPoint) && endPortalPointWrapperList.size() > 1) {
          endPortalPointWrapperList.removeIf(
              wrapper -> this.currentPortalPoint.getMinimumPoint().getX() == wrapper.minX
                  && this.currentPortalPoint.getMaximumPoint().getX() == wrapper.maxX
                  && this.currentPortalPoint.getMinimumPoint().getY() == wrapper.minY
                  && this.currentPortalPoint.getMaximumPoint().getY() == wrapper.maxY
                  && this.currentPortalPoint.getMinimumPoint().getZ() == wrapper.minZ
                  && this.currentPortalPoint.getMaximumPoint().getZ() == wrapper.maxZ);
        }

        this.lastPortalPointUpdateTime = System.currentTimeMillis() + 180_000L;
        this.currentPortalPoint = EndPortalPoint.create(
            endPortalPointWrapperList.get(NumberHelper.range(0, endPortalPointWrapperList.size())));
        final Location waypointLocation = this.currentPortalPoint.getCenterLocation().clone()
            .add(0D, 3D, 0D);
        for (final Player player : Bukkit.getOnlinePlayers()) {
          WaypointHelper.deleteWaypoint(player, WaypointConstants.PORTAL_POINT_WAYPOINT_ID);
          WaypointHelper.createWaypoint(player, waypointLocation,
              WaypointConstants.PORTAL_POINT_WAYPOINT_ID, "PORTAL",
              WaypointConstants.PORTAL_POINT_WAYPOINT_COLOR,
              WaypointConstants.PORTAL_POINT_WAYPOINT_ASSET_SHA,
              WaypointConstants.PORTAL_POINT_WAYPOINT_ASSET_ID, Long.MAX_VALUE);
        }
      }
    }

    teleportationPoint:
    {
      if (this.lastTeleportationPointUpdateTime <= System.currentTimeMillis()) {
        final Optional<Sector> randomSector = SectorHelper.getRandomSector(SectorType.GAME,
            sector -> sector.getStatistics().isOnline());
        if (!randomSector.isPresent()) {
          PlatformUserSectorSpoofHelper.spoofAllSectors();
          this.plugin.getLogger()
              .log(Level.WARNING, "Nie można było wylosować punktu teleportacji.");
          this.plugin.getServer().getScheduler()
              .scheduleSyncDelayedTask(this.plugin, () -> this.plugin.getServer().shutdown());
          break teleportationPoint;
        }

        this.lastTeleportationPointUpdateTime = System.currentTimeMillis() + 60_000L;
        this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin,
            () -> this.currentTeleportationPoint = randomSector.get().random());
      }
    }
  }
}
