package pl.rosehc.controller.guild.user;

public final class GuildUserRanking {

  private int points, kills, deaths;
  private int killStreak;

  public GuildUserRanking(final int points, final int kills, final int deaths,
      final int killStreak) {
    this.points = points;
    this.kills = kills;
    this.deaths = deaths;
    this.killStreak = killStreak;
  }

  public int getPoints() {
    return this.points;
  }

  public void setPoints(final int points) {
    this.points = points;
  }

  public int getKills() {
    return this.kills;
  }

  public void setKills(final int kills) {
    this.kills = kills;
  }

  public int getDeaths() {
    return this.deaths;
  }

  public void setDeaths(final int deaths) {
    this.deaths = deaths;
  }

  public int getKillStreak() {
    return this.killStreak;
  }

  public void setKillStreak(final int killStreak) {
    this.killStreak = killStreak;
  }
}
