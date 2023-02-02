package pl.rosehc.adapter.cuboid;

/**
 * @author stevimeister on 22/12/2021
 **/
public class CuboidRectangle {

  private int x;
  private int z;
  private boolean allowDigging;
  private boolean allowPlacing;
  private int height;
  private int width;

  public CuboidRectangle(final int x, final int z, final int height, final int width,
      final boolean allowDigging, final boolean allowPlacing) {
    this.x = x;
    this.z = z;
    this.height = height;
    this.width = width;
    this.allowDigging = allowDigging;
    this.allowPlacing = allowPlacing;
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

  public int getHeight() {
    return this.height;
  }

  public void setHeight(final int height) {
    this.height = height;
  }

  public int getWidth() {
    return this.width;
  }

  public void setWidth(final int width) {
    this.width = width;
  }

  public boolean isAllowDigging() {
    return this.allowDigging;
  }

  public void setAllowDigging(final boolean allowDigging) {
    this.allowDigging = allowDigging;
  }

  public boolean isAllowPlacing() {
    return this.allowPlacing;
  }

  public void setAllowPlacing(final boolean allowPlacing) {
    this.allowPlacing = allowPlacing;
  }

  @Override
  public String toString() {
    return "CuboidRectangle{" + "x=" + x + ", z=" + z + ", allowDigging=" + allowDigging
        + ", allowPlacing=" + allowPlacing + ", height=" + height + ", width=" + width + '}';
  }

  public boolean isPointInside(final CuboidPoint cuboidPoint) {
    return cuboidPoint.getX() >= this.x && cuboidPoint.getZ() >= this.z
        && cuboidPoint.getX() <= this.x + this.width && cuboidPoint.getZ() <= this.z + this.height;
  }
}
