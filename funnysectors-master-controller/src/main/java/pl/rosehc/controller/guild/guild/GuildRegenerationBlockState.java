package pl.rosehc.controller.guild.guild;

import pl.rosehc.controller.wrapper.guild.GuildRegenerationBlockStateSerializationWrapper;

public final class GuildRegenerationBlockState {

  private final String material;
  private final byte data;
  private final int x, y, z;

  public GuildRegenerationBlockState(final String material, final byte data, final int x,
      final int y, final int z) {
    this.material = material;
    this.data = data;
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public GuildRegenerationBlockStateSerializationWrapper wrap() {
    return new GuildRegenerationBlockStateSerializationWrapper(this.material, this.data, this.x,
        this.y, this.z);
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
