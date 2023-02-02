package pl.rosehc.controller.wrapper.global;

public final class LocationWrapper {

  private String worldName;
  private double x, y, z;
  private float yaw, pitch;

  private LocationWrapper() {
  }

  public LocationWrapper(final String worldName, final double x, final double y, final double z,
      final float yaw, final float pitch) {
    this.worldName = worldName;
    this.x = x;
    this.y = y;
    this.z = z;
    this.yaw = yaw;
    this.pitch = pitch;
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
