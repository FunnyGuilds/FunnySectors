package pl.rosehc.platform.end.session;

import java.util.Objects;
import java.util.UUID;
import org.bukkit.Location;
import pl.rosehc.controller.wrapper.platform.PlatformEndPortalPointWrapper;

public final class EndPortalPointEditingSession {

  private final UUID uniqueId;
  private Location firstPoint;
  private Location secondPoint;

  public EndPortalPointEditingSession(final UUID uniqueId) {
    this.uniqueId = uniqueId;
  }

  public UUID getUniqueId() {
    return this.uniqueId;
  }

  public EndPortalPointEditingSessionStatus createPoint(final Location location) {
    if (Objects.nonNull(this.firstPoint)) {
      if (this.firstPoint.getBlockX() == location.getBlockX()
          && this.firstPoint.getBlockZ() == location.getBlockZ()) {
        return EndPortalPointEditingSessionStatus.FIRST_POINT_ALREADY_SET;
      }

      if (Objects.nonNull(this.secondPoint)) {
        return EndPortalPointEditingSessionStatus.SECOND_POINT_ALREADY_SET;
      }

      this.secondPoint = location;
      return EndPortalPointEditingSessionStatus.SECOND_POINT_SUCCESSFULLY_SET;
    }

    this.firstPoint = location;
    return EndPortalPointEditingSessionStatus.FIRST_POINT_SUCCESSFULLY_SET;
  }

  public PlatformEndPortalPointWrapper wrap() {
    final double minX = Math.min(this.firstPoint.getX(), this.secondPoint.getX()), maxX = Math.max(
        this.firstPoint.getX(), this.secondPoint.getX());
    final double minY = Math.min(this.firstPoint.getY(), this.secondPoint.getY()), maxY = Math.max(
        this.firstPoint.getY(), this.secondPoint.getY());
    final double minZ = Math.min(this.firstPoint.getZ(), this.secondPoint.getZ()), maxZ = Math.max(
        this.firstPoint.getZ(), this.secondPoint.getZ());
    return PlatformEndPortalPointWrapper.createEndPortalPointWrapper(minX, maxX, minY, maxY, minZ,
        maxZ);
  }

  public boolean arePointsSet() {
    return Objects.nonNull(this.firstPoint) && Objects.nonNull(this.secondPoint);
  }
}
