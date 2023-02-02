package pl.rosehc.controller.wrapper.guild;

import pl.rosehc.controller.guild.guild.group.GuildGroupColor;

public enum GuildGroupColorWrapper {

  WHITE, ORANGE, MAGENTA,
  LIGHT_BLUE, YELLOW, LIME,
  PINK, GRAY, LIGHT_GRAY,
  CYAN, PURPLE, BLUE,
  BROWN, GREEN, RED,
  BLACK;

  public static GuildGroupColorWrapper fromOriginal(final GuildGroupColor color) {
    return values()[color.ordinal()];
  }

  public GuildGroupColor toOriginal() {
    return GuildGroupColor.values()[this.ordinal()];
  }
}
