package pl.rosehc.controller.wrapper.guild;

import java.util.Map;
import java.util.UUID;

public final class GuildUserSerializableWrapper {

  private UUID uniqueId;
  private String nickname;
  private Map<UUID, GuildUserFightInfoSerializableWrapper> fightInfoMap;
  private Map<UUID, Long> victimMap;
  private int points, kills, deaths, killStreak;

  private GuildUserSerializableWrapper() {
  }

  public GuildUserSerializableWrapper(final UUID uniqueId, final String nickname,
      final Map<UUID, GuildUserFightInfoSerializableWrapper> fightInfoMap,
      final Map<UUID, Long> victimMap, final int points, final int kills, final int deaths,
      final int killStreak) {
    this.uniqueId = uniqueId;
    this.nickname = nickname;
    this.fightInfoMap = fightInfoMap;
    this.victimMap = victimMap;
    this.points = points;
    this.kills = kills;
    this.deaths = deaths;
    this.killStreak = killStreak;
  }

  public UUID getUniqueId() {
    return this.uniqueId;
  }

  public String getNickname() {
    return this.nickname;
  }

  public Map<UUID, GuildUserFightInfoSerializableWrapper> getFightInfoMap() {
    return this.fightInfoMap;
  }

  public Map<UUID, Long> getVictimMap() {
    return this.victimMap;
  }

  public int getPoints() {
    return this.points;
  }

  public int getKills() {
    return this.kills;
  }

  public int getDeaths() {
    return this.deaths;
  }

  public int getKillStreak() {
    return this.killStreak;
  }
}
