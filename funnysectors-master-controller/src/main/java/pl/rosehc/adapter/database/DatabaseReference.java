package pl.rosehc.adapter.database;

import java.sql.SQLException;
import java.util.Objects;

/**
 * @author stevimeister on 22/12/2021
 **/
public final class DatabaseReference<T> {

  private volatile T referent;

  public synchronized T get() {
    return this.referent;
  }

  public synchronized T getOrDefault(final T defaultValue,
      final DatabaseConsumer<T> defaultConsumer)
      throws SQLException {
    if (Objects.isNull(this.referent)) {
      this.referent = defaultValue;
      defaultConsumer.consume(defaultValue);
    }

    return this.referent;
  }

  public synchronized T getOrDefault(final T defaultValue) throws SQLException {
    return this.getOrDefault(defaultValue, ignored -> {
    });
  }

  public synchronized void set(final T value) {
    this.referent = value;
  }
}

