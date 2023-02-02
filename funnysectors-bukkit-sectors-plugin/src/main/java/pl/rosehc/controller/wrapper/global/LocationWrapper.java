package pl.rosehc.controller.wrapper.global;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public final class LocationWrapper {

  private String worldName;
  private double x, y, z;
  private float yaw, pitch;

  private LocationWrapper() {
  }

  private LocationWrapper(final String worldName, final double x, final double y, final double z,
      final float yaw, final float pitch) {
    this.worldName = worldName;
    this.x = x;
    this.y = y;
    this.z = z;
    this.yaw = yaw;
    this.pitch = pitch;
  }

  public static LocationWrapper wrap(final Location location) {
    return new LocationWrapper(location.getWorld().getName(), location.getX(), location.getY(),
        location.getZ(), location.getYaw(), location.getPitch());
  }

  public Location unwrap() {
    return new Location(Bukkit.getWorld(this.worldName), this.x, this.y, this.z, this.yaw,
        this.pitch);
  }

  public String getWorldName() {
    return this.worldName;
  }

  public double getX() {
    return this.x;
  }

  public double getY() {
    return this.y;
  }

  public double getZ() {
    return this.z;
  }

  public float getYaw() {
    return this.yaw;
  }

  public float getPitch() {
    return this.pitch;
  }
}
