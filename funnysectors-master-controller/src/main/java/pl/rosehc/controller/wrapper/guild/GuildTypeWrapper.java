package pl.rosehc.controller.wrapper.guild;

import pl.rosehc.controller.guild.guild.GuildType;

public enum GuildTypeWrapper {

  SMALL, MEDIUM, LARGE;

  public static GuildTypeWrapper fromOriginal(final GuildType type) {
    return values()[type.ordinal()];
  }

  public GuildType toOriginal() {
    return GuildType.values()[this.ordinal()];
  }
}
