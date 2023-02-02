package pl.rosehc.controller.wrapper.platform;

import java.util.Set;

public final class PlatformUserDropSettingsSerializableWrapper {

  private Set<String> disabledDropSet;
  private double turboDropMultiplier;
  private boolean cobbleStone;
  private long turboDropTime;
  private int currentXP, neededXP;
  private int level;

  private PlatformUserDropSettingsSerializableWrapper() {
  }

  public PlatformUserDropSettingsSerializableWrapper(final Set<String> disabledDropSet,
      final double turboDropMultiplier, final boolean cobbleStone, final long turboDropTime,
      final int currentXP, final int neededXP, final int level) {
    this.disabledDropSet = disabledDropSet;
    this.turboDropMultiplier = turboDropMultiplier;
    this.cobbleStone = cobbleStone;
    this.turboDropTime = turboDropTime;
    this.currentXP = currentXP;
    this.neededXP = neededXP;
    this.level = level;
  }

  public Set<String> getDisabledDropSet() {
    return this.disabledDropSet;
  }

  public double getTurboDropMultiplier() {
    return this.turboDropMultiplier;
  }

  public boolean isCobbleStone() {
    return this.cobbleStone;
  }

  public long getTurboDropTime() {
    return this.turboDropTime;
  }

  public int getCurrentXP() {
    return this.currentXP;
  }

  public int getNeededXP() {
    return this.neededXP;
  }

  public int getLevel() {
    return this.level;
  }
}
