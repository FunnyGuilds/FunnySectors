package pl.rosehc.controller.wrapper.platform;

public final class PlatformEndPortalPointWrapper {

  public double minX, maxX;
  public double minY, maxY;
  public double minZ, maxZ;

  public static PlatformEndPortalPointWrapper createEndPortalPointWrapper(final double minX,
      final double maxX, final double minY, final double maxY, final double minZ,
      final double maxZ) {
    final PlatformEndPortalPointWrapper wrapper = new PlatformEndPortalPointWrapper();
    wrapper.minX = minX;
    wrapper.maxX = maxX;
    wrapper.minY = minY;
    wrapper.maxY = maxY;
    wrapper.minZ = minZ;
    wrapper.maxZ = maxZ;
    return wrapper;
  }
}