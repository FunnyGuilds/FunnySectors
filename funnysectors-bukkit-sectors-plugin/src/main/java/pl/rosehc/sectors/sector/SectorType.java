package pl.rosehc.sectors.sector;

public enum SectorType {

  GROUP_TELEPORTS("world_gtp"), GUILD_TOURNAMENT("world_tournament"), PVP_TOURNAMENT(
      "world_tournament"),
  SPAWN("world"), GAME("world"), END("world_the_end");

  private final String worldName;

  SectorType(final String worldName) {
    this.worldName = worldName;
  }

  public String getWorldName() {
    return this.worldName;
  }
}
