package pl.rosehc.controller.wrapper.guild;

public final class GuildRegionSerializableWrapper {

  private String centerLocation;
  private int size;

  private GuildRegionSerializableWrapper() {
  }

  public GuildRegionSerializableWrapper(final String centerLocation, final int size) {
    this.centerLocation = centerLocation;
    this.size = size;
  }

  public String getCenterLocation() {
    return this.centerLocation;
  }

  public int getSize() {
    return this.size;
  }
}
