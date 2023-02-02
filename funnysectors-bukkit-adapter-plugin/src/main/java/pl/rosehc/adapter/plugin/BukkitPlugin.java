package pl.rosehc.adapter.plugin;

import java.util.Arrays;
import java.util.concurrent.ScheduledExecutorService;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import pl.rosehc.adapter.AdapterPlugin;
import pl.rosehc.adapter.database.DatabaseAdapter;
import pl.rosehc.adapter.nametag.NameTagFactory;
import pl.rosehc.adapter.redis.RedisAdapter;
import pl.rosehc.adapter.redis.callback.CallbackFactory;
import pl.rosehc.adapter.scoreboard.ScoreboardFactory;

public abstract class BukkitPlugin extends JavaPlugin {

  public void registerListeners(final Listener... listeners) {
    Arrays.stream(listeners)
        .forEach(listener -> this.getServer().getPluginManager().registerEvents(listener, this));
  }

  public RedisAdapter getRedisAdapter() {
    return AdapterPlugin.getInstance().getRedisAdapter();
  }

  public DatabaseAdapter getDatabaseAdapter() {
    return AdapterPlugin.getInstance().getDatabaseAdapter();
  }

  public CallbackFactory getCallbackFactory() {
    return AdapterPlugin.getInstance().getCallbackFactory();
  }

  public NameTagFactory getNameTagFactory() {
    return AdapterPlugin.getInstance().getNameTagFactory();
  }

  public ScoreboardFactory getScoreboardFactory() {
    return AdapterPlugin.getInstance().getScoreboardFactory();
  }

  public ScheduledExecutorService getScheduledExecutorService() {
    return AdapterPlugin.getInstance().getScheduledExecutorService();
  }
}
