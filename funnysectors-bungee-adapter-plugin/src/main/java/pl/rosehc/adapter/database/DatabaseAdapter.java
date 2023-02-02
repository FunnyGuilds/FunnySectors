package pl.rosehc.adapter.database;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.util.Objects;

/**
 * @author stevimeister on 22/12/2021
 **/
public final class DatabaseAdapter {

  private final HikariDataSource source;

  public DatabaseAdapter(final String host, final int port, final String password,
      final String user, final String base) {
    this.source = new HikariDataSource();
    this.source.addDataSourceProperty("cachePrepStmts", true);
    this.source.addDataSourceProperty("prepStmtCacheSize", 250);
    this.source.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
    this.source.addDataSourceProperty("useServerPrepStmts", true);
    this.source.addDataSourceProperty("rewriteBatchedStatements", true);
    this.source.addDataSourceProperty("useSSL", false);
    this.source.addDataSourceProperty("requireSSL", false);
    this.source.addDataSourceProperty("characterEncoding", "utf8");
    this.source.addDataSourceProperty("encoding", "UTF-8");
    this.source.addDataSourceProperty("useUnicode", true);
    this.source.setDriverClassName("org.postgresql.Driver");
    this.source.setJdbcUrl("jdbc:postgresql://" + host + ":" + port + "/" + base);
    this.source.setUsername(user);
    if (Objects.nonNull(password) && !password.isEmpty()) {
      this.source.setPassword(password);
    }

    this.source.setMaximumPoolSize(20);
    this.source.setConnectionTimeout(Duration.ofSeconds(30L).toMillis());
  }

  public Connection borrowConnection() throws SQLException {
    return this.source.getConnection();
  }

  public void close() {
    this.source.close();
  }
}
