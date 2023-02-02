package pl.rosehc.controller.guild.user;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import pl.rosehc.adapter.database.DatabaseAdapter;
import pl.rosehc.adapter.database.DatabaseRepository;

public final class GuildUserRepository extends DatabaseRepository<UUID, GuildUser> {

  public GuildUserRepository(final DatabaseAdapter databaseAdapter) throws SQLException {
    super(databaseAdapter);
  }

  @Override
  public Map<UUID, GuildUser> loadAll() throws SQLException {
    final Map<UUID, GuildUser> userMap = new ConcurrentHashMap<>();
    this.doSelect("SELECT * FROM rhc_guild_users", result -> {
      final GuildUser user = new GuildUser(result);
      userMap.put(user.getUniqueId(), user);
    });
    return userMap;
  }

  @Override
  public GuildUser load(final UUID ignored) {
    throw new RuntimeException("Not implemented");
  }

  @Override
  public void prepareTable() throws SQLException {
    this.consumeConnection(connection -> {
      try (final Statement statement = connection.createStatement()) {
        //noinspection SqlDialectInspection,SqlNoDataSourceInspection
        statement.executeUpdate(
            "CREATE TABLE IF NOT EXISTS rhc_guild_users (uniqueId CHAR(36) PRIMARY KEY NOT NULL, nickname VARCHAR(16) NOT NULL,"
                + " points INT, kills INT, deaths INT, killStreak INT);");
      }
    });
  }

  @Override
  public void insert(final GuildUser user) throws SQLException {
    this.doUpdate(
        "INSERT INTO rhc_guild_users (uniqueId, nickname, points, kills, deaths, killStreak)"
            + " VALUES (?, ?, ?, ?, ?, ?)",
        statement -> {
          statement.setString(1, user.getUniqueId().toString());
          statement.setString(2, user.getNickname());
          statement.setInt(3, user.getUserRanking().getPoints());
          statement.setInt(4, 0);
          statement.setInt(5, 0);
          statement.setInt(6, 0);
        });
  }

  @Override
  public void update(final GuildUser ignored) {
    throw new RuntimeException("Not implemented");
  }

  @Override
  public void updateAll(final Collection<GuildUser> userCollection) throws SQLException {
    this.doUpdate(
        "UPDATE rhc_guild_users SET nickname = ?, points = ?, kills = ?, deaths = ?, killStreak = ?, WHERE uniqueId = ?",
        true, statement -> {
          for (final GuildUser user : userCollection) {
            statement.setString(1, user.getNickname());
            statement.setInt(2, user.getUserRanking().getPoints());
            statement.setInt(3, user.getUserRanking().getKills());
            statement.setInt(4, user.getUserRanking().getDeaths());
            statement.setInt(5, user.getUserRanking().getKillStreak());
            statement.setString(6, user.getUniqueId().toString());
            statement.addBatch();
          }
        });
  }

  @Override
  public void delete(final GuildUser ignored) {
    throw new RuntimeException("Not implemented");
  }

  @Override
  public void deleteAll(final Collection<GuildUser> ignored) {
    throw new RuntimeException("Not implemented");
  }
}
