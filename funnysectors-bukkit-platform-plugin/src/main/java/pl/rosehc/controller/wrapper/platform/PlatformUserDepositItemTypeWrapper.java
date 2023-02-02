package pl.rosehc.controller.wrapper.platform;

import pl.rosehc.platform.deposit.DepositItemType;

public enum PlatformUserDepositItemTypeWrapper {

  GOLDEN_HEADS, GOLDEN_APPLES,
  @SuppressWarnings("SpellCheckingInspection") ENDER_PEARLS, SNOWBALLS,
  FISHING_RODS;

  public static PlatformUserDepositItemTypeWrapper fromOriginal(final DepositItemType type) {
    return values()[type.ordinal()];
  }

  public DepositItemType toOriginal() {
    return DepositItemType.values()[this.ordinal()];
  }
}
