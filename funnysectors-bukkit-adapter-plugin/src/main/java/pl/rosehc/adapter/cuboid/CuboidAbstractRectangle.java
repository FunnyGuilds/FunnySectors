package pl.rosehc.adapter.cuboid;

/**
 * @author stevimeister on 22/12/2021
 **/
public final class CuboidAbstractRectangle extends CuboidRectangle {

  private String message;

  public CuboidAbstractRectangle(final String message, final int x, final int z, final int height,
      final int width, final boolean allowDigging, final boolean allowPlacing) {
    super(x, z, height, width, allowDigging, allowPlacing);
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(final String message) {
    this.message = message;
  }
}

