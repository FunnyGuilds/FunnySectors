package pl.rosehc.platform.end;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import pl.rosehc.controller.wrapper.platform.PlatformEndPortalPointWrapper;

public final class EndPortalPoint {

  private final Vector minimumPoint;
  private final Vector maximumPoint;
  private final Location centerLocation;

  private EndPortalPoint(final PlatformEndPortalPointWrapper wrapper) {
    this.minimumPoint = new Vector(wrapper.minX, 0D, wrapper.minZ);
    this.maximumPoint = new Vector(wrapper.maxX, 256D, wrapper.maxZ);
    this.centerLocation = new Location(Bukkit.getWorld("world_the_end"),
        (wrapper.minX + wrapper.maxX) / 2D, (wrapper.minY + wrapper.maxY) / 2D,
        (wrapper.minZ + wrapper.maxZ) / 2D);
  }

  public static EndPortalPoint create(final PlatformEndPortalPointWrapper wrapper) {
    return new EndPortalPoint(wrapper);
  }

  public Vector getMinimumPoint() {
    return this.minimumPoint;
  }

  public Vector getMaximumPoint() {
    return this.maximumPoint;
  }

  public Location getCenterLocation() {
    return this.centerLocation;
  }

  public boolean isInside(final Location location) {
    return location.toVector().isInAABB(this.minimumPoint, this.maximumPoint);
  }
}
