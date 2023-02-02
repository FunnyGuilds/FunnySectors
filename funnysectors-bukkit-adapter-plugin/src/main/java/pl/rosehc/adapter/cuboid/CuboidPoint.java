package pl.rosehc.adapter.cuboid;

/**
 * @author stevimeister on 22/12/2021
 **/
public final class CuboidPoint {

  private int x;
  private int z;

  public CuboidPoint(final int x, final int z) {
    this.x = x;
    this.z = z;
  }

  public int getX() {
    return this.x;
  }

  public void setX(final int x) {
    this.x = x;
  }

  public int getZ() {
    return this.z;
  }

  public void setZ(final int z) {
    this.z = z;
  }
}