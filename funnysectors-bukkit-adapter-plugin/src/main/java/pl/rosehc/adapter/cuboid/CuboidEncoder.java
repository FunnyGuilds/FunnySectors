package pl.rosehc.adapter.cuboid;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.Deflater;

/**
 * @author stevimeister on 22/12/2021
 **/
public final class CuboidEncoder {

  public static byte[] encodeRectangleList(final List<CuboidAbstractRectangle> rectangleList,
      final CuboidRectangle cuboidRectangle) {
    try {
      final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      final DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
      dataOutputStream.writeInt(cuboidRectangle.getX());
      dataOutputStream.writeInt(cuboidRectangle.getZ());
      dataOutputStream.writeInt(cuboidRectangle.getHeight());
      dataOutputStream.writeInt(cuboidRectangle.getWidth());
      dataOutputStream.writeInt(rectangleList.size());
      for (CuboidAbstractRectangle abstractRectangle : rectangleList) {
        if (Objects.nonNull(abstractRectangle.getMessage())) {
          dataOutputStream.writeUTF(abstractRectangle.getMessage());
        } else {
          dataOutputStream.writeUTF("");
        }
        dataOutputStream.writeInt(abstractRectangle.getX());
        dataOutputStream.writeInt(abstractRectangle.getZ());
        dataOutputStream.writeInt(abstractRectangle.getHeight());
        dataOutputStream.writeInt(abstractRectangle.getWidth());
        dataOutputStream.writeBoolean(abstractRectangle.isAllowDigging());
        dataOutputStream.writeBoolean(abstractRectangle.isAllowPlacing());
      }
      return byteArrayOutputStream.toByteArray();
    } catch (final IOException ex) {
      Logger.getLogger(CuboidEncoder.class.getName()).log(Level.SEVERE, null, ex);
    }
    return null;
  }

  public static byte[] compress(byte[] data) throws IOException {
    final Deflater deflater = new Deflater(9);
    deflater.setStrategy(Deflater.DEFAULT_STRATEGY);
    deflater.setInput(data);
    final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(data.length);
    deflater.finish();
    byte[] buffer = new byte[4096];
    while (!deflater.finished()) {
      final int count = deflater.deflate(buffer);
      byteArrayOutputStream.write(buffer, 0, count);
    }
    byteArrayOutputStream.close();

    return byteArrayOutputStream.toByteArray();
  }

  public static byte[] encrypt(final byte[] data) {
    try {
      final CuboidOutputStream cuboidOutputStream = new CuboidOutputStream((byte) 113);
      cuboidOutputStream.write(data);
      cuboidOutputStream.flush();
      return cuboidOutputStream.toByteArray();
    } catch (IOException ex) {
      Logger.getLogger(CuboidEncoder.class.getName()).log(Level.SEVERE, null, ex);
    }
    return null;
  }

  public static byte[] encryptAndCompressRectangleList(
      final List<CuboidAbstractRectangle> rectangleList, final CuboidRectangle cuboidRectangle) {
    try {
      return encrypt(compress(encodeRectangleList(rectangleList, cuboidRectangle)));
    } catch (IOException ex) {
      Logger.getLogger(CuboidEncoder.class.getName()).log(Level.SEVERE, null, ex);
    }
    return null;
  }

  public static byte[] encodeRectangleList(final List<CuboidAbstractRectangle> rectangleList) {
    try {
      final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      final DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
      dataOutputStream.writeInt(rectangleList.size());
      for (CuboidAbstractRectangle abstractRectangle : rectangleList) {
        dataOutputStream.writeUTF(abstractRectangle.getMessage());
        dataOutputStream.writeInt(abstractRectangle.getX());
        dataOutputStream.writeInt(abstractRectangle.getZ());
        dataOutputStream.writeInt(abstractRectangle.getHeight());
        dataOutputStream.writeInt(abstractRectangle.getWidth());
        dataOutputStream.writeBoolean(abstractRectangle.isAllowDigging());
        dataOutputStream.writeBoolean(abstractRectangle.isAllowPlacing());
      }

      return byteArrayOutputStream.toByteArray();
    } catch (IOException ex) {
      Logger.getLogger(CuboidEncoder.class.getName()).log(Level.SEVERE, null, ex);
    }
    return null;
  }

  public static byte[] encryptAndEncodeRectangleList(
      final List<CuboidAbstractRectangle> rectangleList) {
    return encrypt(encodeRectangleList(rectangleList));
  }
}