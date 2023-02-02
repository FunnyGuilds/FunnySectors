package pl.rosehc.controller.platform.ban;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import pl.rosehc.adapter.database.DatabaseAdapter;
import pl.rosehc.adapter.database.DatabaseRepository;

public final class BanRepository extends DatabaseRepository<String, Ban> {

  public BanRepository(final DatabaseAdapter databaseAdapter) throws SQLException {
    super(databaseAdapter);
  }

  @Override
  public Map<String, Ban> loadAll() throws SQLException {
    final Map<String, Ban> banMap = new ConcurrentHashMap<>();
    this.doSelect("SELECT * FROM rhc_platform_bans", result -> {
      final Ban ban = new Ban(result);
      banMap.put(ban.getPlayerNickname().toLowerCase(), ban);
    });
    return banMap;
  }

  @Override
  public Ban load(final String ignored) {
    throw new RuntimeException("Not implemented");
  }

  @Override
  public void prepareTable() throws SQLException {
    this.consumeConnection(connection -> {
      try (final Statement statement = connection.createStatement()) {
        //noinspection SqlDialectInspection,SqlNoDataSourceInspection
        statement.executeUpdate(
            "CREATE TABLE IF NOT EXISTS rhc_platform_bans (playerNickname VARCHAR(16) PRIMARY KEY NOT NULL, staffNickname VARCHAR(16) NOT NULL,"
                + " ip TEXT, reason TEXT NOT NULL, computerUid BYTEA, creationTime BIGINT, leftTime BIGINT);");
      }
    });
  }

  @Override
  public void insert(final Ban ban) throws SQLException {
    this.doUpdate(
        "INSERT INTO rhc_platform_bans (playerNickname, staffNickname, ip, reason, computerUid, creationTime, leftTime)"
            + " VALUES (?, ?, ?, ?, ?, ?, ?)",
        statement -> {
          statement.setString(1, ban.getPlayerNickname());
          statement.setString(2, ban.getStaffNickname());
          statement.setString(3, ban.getIp());
          statement.setString(4, ban.getReason());
          statement.setBytes(5, ban.getComputerUid());
          statement.setLong(6, ban.getCreationTime());
          statement.setLong(7, ban.getLeftTime());
        });
  }

  @Override
  public void update(final Ban ignored) {
    throw new RuntimeException("Not implemented");
  }

  @Override
  public void updateAll(final Collection<Ban> banCollection) throws SQLException {
    this.doUpdate("UPDATE rhc_platform_bans SET computerUid = ?, ip = ? WHERE playerNickname = ?",
        true, statement -> {
          for (final Ban ban : banCollection) {
            statement.setBytes(1, ban.getComputerUid());
            statement.setString(2, ban.getIp());
            statement.setString(3, ban.getPlayerNickname());
            statement.addBatch();
          }
        });
  }

  @Override
  public void delete(final Ban ban) throws SQLException {
    this.doUpdate("DELETE FROM rhc_platform_bans WHERE playerNickname = ?",
        statement -> statement.setString(1, ban.getPlayerNickname()));
  }

  @Override
  public void deleteAll(final Collection<Ban> ignored) {
    throw new RuntimeException("Not implemented");
  }
}
