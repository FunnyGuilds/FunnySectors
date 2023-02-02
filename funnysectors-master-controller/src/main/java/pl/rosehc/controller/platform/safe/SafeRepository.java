package pl.rosehc.controller.platform.safe;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import pl.rosehc.adapter.database.DatabaseAdapter;
import pl.rosehc.adapter.database.DatabaseRepository;

public final class SafeRepository extends DatabaseRepository<UUID, Safe> {

  public SafeRepository(final DatabaseAdapter databaseAdapter) throws SQLException {
    super(databaseAdapter);
  }

  @Override
  public Map<UUID, Safe> loadAll() throws SQLException {
    final Map<UUID, Safe> safeMap = new ConcurrentHashMap<>();
    this.doSelect("SELECT * FROM rhc_platform_safes", result -> {
      final Safe safe = new Safe(result);
      safeMap.put(safe.getUniqueId(), safe);
    });
    return safeMap;
  }

  @Override
  public Safe load(final UUID ignored) {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public void prepareTable() throws SQLException {
    this.consumeConnection(connection -> {
      try (final Statement statement = connection.createStatement()) {
        //noinspection SqlDialectInspection,SqlNoDataSourceInspection
        statement.executeUpdate(
            "CREATE TABLE IF NOT EXISTS rhc_platform_safes (uniqueId CHAR(36) PRIMARY KEY NOT NULL, creationTime BIGINT, ownerUniqueId CHAR(36) NOT NULL,"
                + " ownerNickname VARCHAR(16) NOT NULL, description VARCHAR(16), contents BYTEA, lastOpenedTime BIGINT);");
      }
    });
  }

  @Override
  public void insert(final Safe safe) throws SQLException {
    this.doUpdate(
        "INSERT INTO rhc_platform_safes (uniqueId, creationTime, ownerUniqueId, ownerNickname, description, contents, lastOpenedTime)"
            + " VALUES (?, ?, ?, ?, ?, ?, ?)",
        statement -> {
          statement.setString(1, safe.getUniqueId().toString());
          statement.setLong(2, safe.getCreationTime());
          statement.setString(3, safe.getOwnerUniqueId().toString());
          statement.setString(4, safe.getOwnerNickname());
          statement.setString(5, null);
          statement.setBytes(6, null);
          statement.setLong(7, 0L);
        });
  }

  @Override
  public void update(final Safe ignored) {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public void updateAll(final Collection<Safe> safeCollection) throws SQLException {
    this.doUpdate(
        "UPDATE rhc_platform_safes SET ownerUniqueId = ?, ownerNickname = ?, description = ?, contents = ?, lastOpenedTime = ? WHERE uniqueId = ?",
        true, statement -> {
          for (final Safe safe : safeCollection) {
            statement.setString(1, safe.getOwnerUniqueId().toString());
            statement.setString(2, safe.getOwnerNickname());
            statement.setString(3, safe.getDescription());
            statement.setBytes(4, safe.getContents());
            statement.setLong(5, safe.getLastOpenedTime());
            statement.setString(6, safe.getUniqueId().toString());
            statement.addBatch();
          }
        });
  }

  @Override
  public void delete(final Safe ignored) {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public void deleteAll(final Collection<Safe> ignored) {
    throw new UnsupportedOperationException("Not implemented");
  }
}
