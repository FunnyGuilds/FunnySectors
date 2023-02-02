package pl.rosehc.controller.wrapper.guild;

public final class GuildRegenerationBlockStateSerializationWrapper {

  private String material;
  private byte data;
  private int x, y, z;

  private GuildRegenerationBlockStateSerializationWrapper() {
  }

  public GuildRegenerationBlockStateSerializationWrapper(final String material, final byte data,
      final int x, final int y, final int z) {
    this.material = material;
    this.data = data;
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public String getMaterial() {
    return this.material;
  }

  public byte getData() {
    return this.data;
  }

  public int getX() {
    return this.x;
  }

  public int getY() {
    return this.y;
  }

  public int getZ() {
    return this.z;
  }
}
