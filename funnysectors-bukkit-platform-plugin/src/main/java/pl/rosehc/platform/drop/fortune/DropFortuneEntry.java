package pl.rosehc.platform.drop.fortune;

final class DropFortuneEntry {

  private final double chance;
  private final int level, incremental;

  DropFortuneEntry(final double chance, final int level, final int incremental) {
    this.chance = chance;
    this.level = level;
    this.incremental = incremental;
  }

  public double getChance() {
    return this.chance;
  }

  public int getLevel() {
    return this.level;
  }

  public int getIncremental() {
    return this.incremental;
  }
}
