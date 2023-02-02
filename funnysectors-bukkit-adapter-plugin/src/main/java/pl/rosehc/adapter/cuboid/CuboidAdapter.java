package pl.rosehc.adapter.cuboid;

import java.util.ArrayList;
import java.util.List;

/**
 * @author stevimeister on 22/12/2021
 **/
public final class CuboidAdapter {

  private static final String SET_ALL_COMPRESSED_CUBOIDS_CHANNEL = "MC|BMW_S1000RR";
  private static final String ADD_SOME_CUBOIDS_CHANNEL = "MC|YAMAHA_R1";
  private static final String REMOVE_SOME_CUBOIDS_CHANNEL = "MC|HONDA_CBR_1000";

  public static CuboidPayload setAllCompressedCuboids(
      final List<CuboidAbstractRectangle> rectangleList, final CuboidRectangle cuboidRectangle) {
    return new CuboidPayload(SET_ALL_COMPRESSED_CUBOIDS_CHANNEL,
        CuboidEncoder.encryptAndCompressRectangleList(rectangleList, cuboidRectangle));
  }

  public static CuboidPayload clearCuboids() {
    return new CuboidPayload(SET_ALL_COMPRESSED_CUBOIDS_CHANNEL,
        CuboidEncoder.encryptAndCompressRectangleList(new ArrayList<>(),
            new CuboidRectangle(0, 0, 0, 0, true, true)));
  }

  public static CuboidPayload addCuboids(final List<CuboidAbstractRectangle> rectangleList) {
    return new CuboidPayload(ADD_SOME_CUBOIDS_CHANNEL,
        CuboidEncoder.encodeRectangleList(rectangleList));
  }

  public static CuboidPayload removeCuboids(final List<CuboidAbstractRectangle> rectangleList) {
    return new CuboidPayload(REMOVE_SOME_CUBOIDS_CHANNEL,
        CuboidEncoder.encodeRectangleList(rectangleList));
  }
}

