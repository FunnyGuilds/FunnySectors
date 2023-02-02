package pl.rosehc.platform.rank;

public final class RankEntry {

  private final Rank previousRank, currentRank;
  private final long expirationTime;

  private RankEntry(final Rank previousRank, final Rank currentRank, final long expirationTime) {
    this.previousRank = previousRank;
    this.currentRank = currentRank;
    this.expirationTime = expirationTime;
  }

  public static RankEntry create(final Rank previousRank, final Rank currentRank,
      final long expirationTime) {
    return new RankEntry(previousRank, currentRank, expirationTime);
  }

  public Rank getPreviousRank() {
    return this.previousRank;
  }

  public Rank getCurrentRank() {
    return this.currentRank;
  }

  public long getExpirationTime() {
    return this.expirationTime;
  }
}
