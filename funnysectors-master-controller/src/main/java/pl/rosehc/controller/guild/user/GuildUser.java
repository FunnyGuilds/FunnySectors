package pl.rosehc.controller.guild.user;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import pl.rosehc.controller.MasterController;
import pl.rosehc.controller.configuration.impl.configuration.GuildsConfiguration;
import pl.rosehc.controller.guild.guild.Guild;
import pl.rosehc.controller.wrapper.guild.GuildUserSerializableWrapper;

public final class GuildUser {

  private final UUID uniqueId;
  private final GuildUserRanking userRanking;
  private final Map<UUID, GuildUserFightInfo> fightInfoMap;
  private final Map<UUID, Long> victimsMap;

  private Guild guild;
  private String nickname;
  private int memberArrayPosition = -1;

  public GuildUser(final ResultSet result) throws SQLException {
    this.uniqueId = UUID.fromString(result.getString("uniqueId"));
    this.userRanking = new GuildUserRanking(result.getInt("points"), result.getInt("kills"),
        result.getInt("deaths"), result.getInt("killStreak"));
    this.nickname = result.getString("nickname");
    this.fightInfoMap = new ConcurrentHashMap<>();
    this.victimsMap = new ConcurrentHashMap<>();
  }

  public GuildUser(final UUID uniqueId, final String nickname) {
    this.uniqueId = uniqueId;
    this.nickname = nickname;
    this.userRanking = new GuildUserRanking(MasterController.getInstance().getConfigurationFactory()
        .findConfiguration(GuildsConfiguration.class).pluginWrapper.startUserPoints, 0, 0, 0);
    this.fightInfoMap = new ConcurrentHashMap<>();
    this.victimsMap = new ConcurrentHashMap<>();
  }

  public GuildUserSerializableWrapper wrap() {
    return new GuildUserSerializableWrapper(this.uniqueId, this.nickname,
        this.fightInfoMap.entrySet().stream()
            .map(entry -> new SimpleEntry<>(entry.getKey(), entry.getValue().wrap()))
            .collect(Collectors.toMap(Entry::getKey, Entry::getValue)), this.victimsMap,
        this.userRanking.getPoints(), this.userRanking.getKills(),
        this.userRanking.getDeaths(), this.userRanking.getKillStreak());
  }

  public UUID getUniqueId() {
    return this.uniqueId;
  }

  public GuildUserRanking getUserRanking() {
    return this.userRanking;
  }

  public Optional<GuildUserFightInfo> findLastOptionalFighter() {
    GuildUserFightInfo lastOptionalFightInfo = null;
    for (final GuildUserFightInfo fightInfo : this.fightInfoMap.values()) {
      if (lastOptionalFightInfo == null
          || lastOptionalFightInfo.getFightTime() <= fightInfo.getFightTime()) {
        lastOptionalFightInfo = fightInfo;
      }
    }

    return Optional.ofNullable(lastOptionalFightInfo);
  }

  public void cacheFighter(final UUID uniqueId, final long fightTime) {
    final GuildUserFightInfo fightInfo = this.fightInfoMap.get(uniqueId);
    if (Objects.nonNull(fightInfo)) {
      fightInfo.setFightTime(fightTime);
      this.fightInfoMap.replace(uniqueId, fightInfo);
    } else {
      this.fightInfoMap.put(uniqueId, new GuildUserFightInfo(uniqueId, fightTime));
    }
  }

  public void clearFighters() {
    this.fightInfoMap.clear();
  }

  public long getAttackerTime(final GuildUser user) {
    return this.victimsMap.containsKey(user.getUniqueId())
        && this.victimsMap.get(user.getUniqueId()) > System.currentTimeMillis()
        ? this.victimsMap.get(user.getUniqueId()) : 0L;
  }

  public long getVictimTime(final GuildUser user) {
    return user.victimsMap.containsKey(this.uniqueId)
        && user.victimsMap.get(this.uniqueId) > System.currentTimeMillis() ? user.victimsMap.get(
        this.uniqueId) : 0L;
  }

  public void cacheVictim(final UUID uniqueId, final long time) {
    this.victimsMap.put(uniqueId, time);
  }

  public String getNickname() {
    return this.nickname;
  }

  public void setNickname(final String nickname) {
    this.nickname = nickname;
  }

  public Guild getGuild() {
    return this.guild;
  }

  public void setGuild(final Guild guild) {
    this.guild = guild;
  }

  public int getMemberArrayPosition() {
    return this.memberArrayPosition;
  }

  public void setMemberArrayPosition(final int memberArrayPosition) {
    this.memberArrayPosition = memberArrayPosition;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }

    if (other == null || this.getClass() != other.getClass()) {
      return false;
    }

    final GuildUser user = (GuildUser) other;
    return this.uniqueId.equals(user.uniqueId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.uniqueId);
  }
}
