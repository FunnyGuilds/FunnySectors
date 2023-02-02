package pl.rosehc.adapter;

import com.google.gson.annotations.SerializedName;
import pl.rosehc.adapter.configuration.ConfigurationData;

public final class AdapterConfiguration extends ConfigurationData {

  @SerializedName("database")
  public DatabaseWrapper databaseWrapper = new DatabaseWrapper();
  @SerializedName("redis")
  public RedisWrapper redisWrapper = new RedisWrapper();

  public static class DatabaseWrapper {

    public String host = "5.181.151.39";
    public int port = 5432;
    public String password = "5KyfpyMH7czxksXU";
    public String user = "admin";
    public String database = "rosehc_db";
  }

  public static class RedisWrapper {

    public String host = "5.181.151.39";
    public int port = 6379;
    public String password = "none";
  }
}
