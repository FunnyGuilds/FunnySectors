package pl.rosehc.adapter;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.bukkit.plugin.java.JavaPlugin;
import pl.rosehc.adapter.database.DatabaseAdapter;
import pl.rosehc.adapter.helper.ConfigurationHelper;
import pl.rosehc.adapter.inventory.BukkitInventoryListeners;
import pl.rosehc.adapter.nametag.NameTagFactory;
import pl.rosehc.adapter.nametag.NameTagListeners;
import pl.rosehc.adapter.nametag.NameTagUpdateTask;
import pl.rosehc.adapter.redis.RedisAdapter;
import pl.rosehc.adapter.redis.callback.CallbackFactory;
import pl.rosehc.adapter.scoreboard.ScoreboardFactory;
import pl.rosehc.adapter.scoreboard.ScoreboardListeners;
import pl.rosehc.adapter.scoreboard.ScoreboardUpdateTask;

/**
 * @author stevimeister on 03/10/2021
 **/
public final class AdapterPlugin extends JavaPlugin {

  private static AdapterPlugin instance;

  private RedisAdapter redisAdapter;
  private DatabaseAdapter databaseAdapter;

  private CallbackFactory callbackFactory;
  private NameTagFactory nameTagFactory;
  private ScoreboardFactory scoreboardFactory;

  private ScheduledExecutorService scheduledExecutorService;

  public static AdapterPlugin getInstance() {
    return instance;
  }

  @Override
  public void onEnable() {
    instance = this;

    final AdapterConfiguration adapterConfiguration = ConfigurationHelper.load(
        new File(this.getDataFolder(), "config.json"), AdapterConfiguration.class);
    this.redisAdapter = new RedisAdapter(adapterConfiguration.redisWrapper.host,
        adapterConfiguration.redisWrapper.port, adapterConfiguration.redisWrapper.password);
    this.databaseAdapter = new DatabaseAdapter(adapterConfiguration.databaseWrapper.host,
        adapterConfiguration.databaseWrapper.port, adapterConfiguration.databaseWrapper.password,
        adapterConfiguration.databaseWrapper.user, adapterConfiguration.databaseWrapper.database);

    this.callbackFactory = new CallbackFactory();
    this.nameTagFactory = new NameTagFactory();
    this.scoreboardFactory = new ScoreboardFactory();

    this.scheduledExecutorService = Executors.newScheduledThreadPool(2);

    new NameTagUpdateTask(this);
    new NameTagListeners(this);

    new ScoreboardUpdateTask(this);
    new ScoreboardListeners(this);
    new BukkitInventoryListeners(this);
  }

  public RedisAdapter getRedisAdapter() {
    return this.redisAdapter;
  }

  public DatabaseAdapter getDatabaseAdapter() {
    return this.databaseAdapter;
  }

  public CallbackFactory getCallbackFactory() {
    return this.callbackFactory;
  }

  public NameTagFactory getNameTagFactory() {
    return this.nameTagFactory;
  }

  public ScoreboardFactory getScoreboardFactory() {
    return this.scoreboardFactory;
  }

  public ScheduledExecutorService getScheduledExecutorService() {
    return this.scheduledExecutorService;
  }
}
