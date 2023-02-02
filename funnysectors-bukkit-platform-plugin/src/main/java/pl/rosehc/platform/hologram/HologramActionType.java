package pl.rosehc.platform.hologram;

public enum HologramActionType {

  INTERACT, ATTACK,
  INTERACT_AT;

  public static HologramActionType fromOriginal(final Enum<?> enumValue) {
    return values()[enumValue.ordinal()];
  }
}
