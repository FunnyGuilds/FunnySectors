package pl.rosehc.adapter.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Map;

/**
 * @author stevimeister on 22/12/2021
 **/
public abstract class DatabaseRepository<K, T> {

  private final DatabaseAdapter databaseAdapter;

  public DatabaseRepository(final DatabaseAdapter databaseAdapter) throws SQLException {
    this.databaseAdapter = databaseAdapter;
    this.prepareTable();
  }

  public abstract Map<K, T> loadAll() throws SQLException;

  public abstract T load(final K key) throws SQLException;

  public abstract void prepareTable() throws SQLException;

  public abstract void insert(final T data) throws SQLException;

  public abstract void update(final T data) throws SQLException;

  public abstract void updateAll(final Collection<T> dataCollection) throws SQLException;

  public abstract void delete(final T data) throws SQLException;

  public abstract void deleteAll(final Collection<T> dataCollection) throws SQLException;

  protected void doSelect(final String statement,
      final DatabaseConsumer<ResultSet> resultSetConsumer)
      throws SQLException {
    try (
        final Connection connection = this.databaseAdapter.borrowConnection();
        final Statement connectionStatement = connection.createStatement();
        final ResultSet resultSet = connectionStatement.executeQuery(statement)
    ) {
      while (resultSet.next()) {
        resultSetConsumer.consume(resultSet);
      }
    }
  }

  protected void doSelect(final String statement,
      final DatabaseConsumer<PreparedStatement> statementConsumer,
      final DatabaseConsumer<ResultSet> resultSetConsumer) throws SQLException {
    try (
        final Connection connection = this.databaseAdapter.borrowConnection();
        final PreparedStatement preparedStatement = connection.prepareStatement(statement)
    ) {
      statementConsumer.consume(preparedStatement);
      try (final ResultSet resultSet = preparedStatement.executeQuery()) {
        while (resultSet.next()) {
          resultSetConsumer.consume(resultSet);
        }
      }
    }
  }

  protected void doUpdate(final String statement, final boolean batch,
      final DatabaseConsumer<PreparedStatement> statementConsumer)
      throws SQLException {
    try (
        final Connection connection = this.databaseAdapter.borrowConnection();
        final PreparedStatement preparedStatement = connection.prepareStatement(statement)
    ) {
      statementConsumer.consume(preparedStatement);
      if (batch) {
        preparedStatement.executeBatch();
      } else {
        preparedStatement.executeUpdate();
      }
    }
  }

  protected void doUpdate(final String statement,
      final DatabaseConsumer<PreparedStatement> statementConsumer)
      throws SQLException {
    this.doUpdate(statement, false, statementConsumer);
  }

  protected void consumeConnection(final DatabaseConsumer<Connection> connectionConsumer)
      throws SQLException {
    try (final Connection connection = this.databaseAdapter.borrowConnection()) {
      connectionConsumer.consume(connection);
    }
  }
}

