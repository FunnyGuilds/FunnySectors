package pl.rosehc.adapter.cuboid;

/**
 * @author stevimeister on 22/12/2021
 **/
public final class CuboidPayload {

  private final String channel;
  private final byte[] data;

  public CuboidPayload(final String channel, final byte[] data) {
    this.channel = channel;
    this.data = data;
  }

  public String getChannel() {
    return channel;
  }

  public byte[] getData() {
    return data;
  }
}