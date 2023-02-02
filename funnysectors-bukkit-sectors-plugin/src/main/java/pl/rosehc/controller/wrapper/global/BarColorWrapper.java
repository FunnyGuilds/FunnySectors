package pl.rosehc.controller.wrapper.global;

import pl.rosehc.bossbar.BarColor;

public enum BarColorWrapper {

  PINK, BLUE,
  RED, GREEN,
  YELLOW, PURPLE,
  WHITE, NONE;

  public static BarColorWrapper fromOriginal(final BarColor color) {
    return values()[color.ordinal()];
  }

  public BarColor toOriginal() {
    return BarColor.values()[this.ordinal()];
  }
}
