package pl.rosehc.controller.guild.guild;

public enum GuildType {

  SMALL(15), MEDIUM(35), LARGE(250);

  private final int size;

  GuildType(final int size) {
    this.size = size;
  }

  public int getSize() {
    return this.size;
  }
}
