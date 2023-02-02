package pl.rosehc.controller.platform.user.subdata;

import java.util.HashSet;
import java.util.Set;
import pl.rosehc.controller.wrapper.platform.PlatformUserDropSettingsSerializableWrapper;

public final class PlatformUserDropSettings {

  private Set<String> disabledDropSet;
  private double turboDropMultiplier;
  private boolean cobbleStone;
  private long turboDropTime;
  private int currentXP, neededXP;
  private int level;

  public PlatformUserDropSettings(final Set<String> disabledDropSet,
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

  public PlatformUserDropSettingsSerializableWrapper wrap() {
    return new PlatformUserDropSettingsSerializableWrapper(new HashSet<>(this.disabledDropSet),
        this.turboDropMultiplier, this.cobbleStone, this.turboDropTime, this.currentXP,
        this.neededXP, this.level);
  }

  public Set<String> getDisabledDropSet() {
    return this.disabledDropSet;
  }

  public void setDisabledDropSet(final Set<String> disabledDropSet) {
    this.disabledDropSet = disabledDropSet;
  }

  public double getTurboDropMultiplier() {
    return this.turboDropMultiplier;
  }

  public void setTurboDropMultiplier(final double turboDropMultiplier) {
    this.turboDropMultiplier = turboDropMultiplier;
  }

  public long getTurboDropTime() {
    return this.turboDropTime;
  }

  public void setTurboDropTime(final long turboDropTime) {
    this.turboDropTime = turboDropTime;
  }

  public boolean isCobbleStone() {
    return this.cobbleStone;
  }

  public void setCobbleStone(final boolean cobbleStone) {
    this.cobbleStone = cobbleStone;
  }

  public int getCurrentXP() {
    return this.currentXP;
  }

  public void setCurrentXP(final int currentXP) {
    this.currentXP = currentXP;
  }

  public int getNeededXP() {
    return this.neededXP;
  }

  public void setNeededXP(final int neededXP) {
    this.neededXP = neededXP;
  }

  public int getLevel() {
    return this.level;
  }

  public void setLevel(final int level) {
    this.level = level;
  }
}
