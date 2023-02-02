package pl.rosehc.controller.guild.guild;

import pl.rosehc.controller.wrapper.guild.GuildRegionSerializableWrapper;

public final class GuildRegion {

  private final String centerLocation;
  private int size;

  public GuildRegion(final String centerLocation, final int size) {
    this.centerLocation = centerLocation;
    this.size = size;
  }

  public GuildRegionSerializableWrapper wrap() {
    return new GuildRegionSerializableWrapper(this.centerLocation, this.size);
  }

  public String getCenterLocation() {
    return this.centerLocation;
  }

  public int getSize() {
    return this.size;
  }

  public void setSize(final int size) {
    this.size = size;
  }
}
