package pl.rosehc.adapter.database;

import java.sql.SQLException;

/**
 * @author stevimeister on 22/12/2021
 **/
@FunctionalInterface
public interface DatabaseConsumer<T> {

  void consume(T input) throws SQLException;
}
