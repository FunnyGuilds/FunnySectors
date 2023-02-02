package pl.rosehc.sectors.proxy;

public final class Proxy {

  private final int identifier;
  private double load;
  private long lastUpdate;
  private int players;

  public Proxy(int identifier) {
    this.identifier = identifier;
  }

  public int getIdentifier() {
    return this.identifier;
  }

  public double getLoad() {
    return this.load;
  }

  public void setLoad(double load) {
    this.load = load;
  }

  public long getLastUpdate() {
    return this.lastUpdate;
  }

  public void setLastUpdate(long lastUpdate) {
    this.lastUpdate = lastUpdate;
  }

  public boolean isOnline() {
    return this.lastUpdate + 2500L > System.currentTimeMillis();
  }

  public int getPlayers() {
    return this.players;
  }

  public void setPlayers(int players) {
    this.players = players;
  }
}
