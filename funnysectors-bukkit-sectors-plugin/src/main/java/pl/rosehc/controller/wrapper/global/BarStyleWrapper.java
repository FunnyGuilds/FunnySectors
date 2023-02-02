package pl.rosehc.controller.wrapper.global;

import pl.rosehc.bossbar.BarStyle;

public enum BarStyleWrapper {

  SOLID, SEGMENTED_6,
  SEGMENTED_10, SEGMENTED_12,
  SEGMENTED_20;

  public static BarStyleWrapper fromOriginal(final BarStyle style) {
    return values()[style.ordinal()];
  }

  public BarStyle toOriginal() {
    return BarStyle.values()[this.ordinal()];
  }
}
