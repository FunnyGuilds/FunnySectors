package pl.rosehc.sectors.sector;

public final class SectorStatistics {

  private double tps, load;
  private long lastUpdate;
  private int players;

  public double getTps() {
    return this.tps;
  }

  public void setTps(final double tps) {
    this.tps = tps;
  }

  public double getLoad() {
    return this.load;
  }

  public void setLoad(final double load) {
    this.load = load;
  }

  public long getLastUpdate() {
    return this.lastUpdate;
  }

  public void setLastUpdate(final long lastUpdate) {
    this.lastUpdate = lastUpdate;
  }

  public boolean isOnline() {
    return this.lastUpdate + 2500L > System.currentTimeMillis();
  }

  public int getPlayers() {
    return this.players;
  }

  public void setPlayers(final int players) {
    this.players = players;
  }
}
