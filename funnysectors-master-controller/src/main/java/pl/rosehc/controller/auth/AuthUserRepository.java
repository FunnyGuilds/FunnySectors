package pl.rosehc.controller.auth;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import pl.rosehc.adapter.database.DatabaseAdapter;
import pl.rosehc.adapter.database.DatabaseRepository;

public final class AuthUserRepository extends DatabaseRepository<String, AuthUser> {

  public AuthUserRepository(final DatabaseAdapter databaseAdapter) throws SQLException {
    super(databaseAdapter);
  }

  @Override
  public Map<String, AuthUser> loadAll() throws SQLException {
    final Map<String, AuthUser> userMap = new ConcurrentHashMap<>();
    this.doSelect("SELECT * FROM rhc_auth_users", result -> {
      final AuthUser user = new AuthUser(result);
      userMap.put(user.getNickname().toLowerCase(), user);
    });
    return userMap;
  }

  @Override
  public AuthUser load(final String ignored) {
    throw new RuntimeException("Not implemented");
  }

  @Override
  public void prepareTable() throws SQLException {
    this.consumeConnection(connection -> {
      try (final Statement statement = connection.createStatement()) {
        //noinspection SqlDialectInspection,SqlNoDataSourceInspection
        statement.executeUpdate(
            "CREATE TABLE IF NOT EXISTS rhc_auth_users (nickname VARCHAR(16) PRIMARY KEY NOT NULL, password TEXT, lastIP TEXT,"
                + " firstJoinTime BIGINT, lastOnlineTime BIGINT, premium BOOLEAN, registered BOOLEAN);");
      }
    });
  }

  @Override
  public void insert(final AuthUser user) throws SQLException {
    this.doUpdate("INSERT INTO rhc_auth_users (nickname, password, lastIP, firstJoinTime,"
        + " lastOnlineTime, premium, registered)"
        + " VALUES (?, ?, ?, ?, ?, ?, ?)", statement -> {
      statement.setString(1, user.getNickname());
      statement.setString(2, user.getPassword());
      statement.setString(3, user.getLastIP());
      statement.setLong(4, user.getFirstJoinTime());
      statement.setLong(5, user.getLastOnlineTime());
      statement.setBoolean(6, user.isPremium());
      statement.setBoolean(7, user.isRegistered());
    });
  }

  @Override
  public void update(final AuthUser ignored) {
    throw new RuntimeException("Not implemented");
  }

  @Override
  public void updateAll(final Collection<AuthUser> userCollection) throws SQLException {
    this.doUpdate("UPDATE rhc_auth_users SET password = ?, lastIP = ?, firstJoinTime = ?,"
            + " lastOnlineTime = ?, premium = ?, registered = ? WHERE nickname = ?", true,
        statement -> {
          for (final AuthUser user : userCollection) {
            statement.setString(1, user.getPassword());
            statement.setString(2, user.getLastIP());
            statement.setLong(3, user.getFirstJoinTime());
            statement.setLong(4, user.getLastOnlineTime());
            statement.setBoolean(5, user.isPremium());
            statement.setBoolean(6, user.isRegistered());
            statement.setString(7, user.getNickname());
            statement.addBatch();
          }
        });
  }

  @Override
  public void delete(final AuthUser user) throws SQLException {
    this.doUpdate("DELETE FROM rhc_auth_users WHERE nickname = ?",
        statement -> statement.setString(1, user.getNickname()));
  }

  @Override
  public void deleteAll(final Collection<AuthUser> ignored) {
    throw new RuntimeException("Not implemented");
  }
}
