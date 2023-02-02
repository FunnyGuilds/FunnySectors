package pl.rosehc.adapter.plugin;

import java.util.Arrays;
import java.util.concurrent.ScheduledExecutorService;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import pl.rosehc.adapter.AdapterPlugin;
import pl.rosehc.adapter.database.DatabaseAdapter;
import pl.rosehc.adapter.redis.RedisAdapter;
import pl.rosehc.adapter.redis.callback.CallbackFactory;
import pl.rosehc.adapter.redis.packet.PacketFactory;

/**
 * @author stevimeister on 22/12/2021
 **/
public abstract class BungeePlugin extends Plugin {

  public void registerListeners(final Listener... listeners) {
    Arrays.stream(listeners)
        .forEach(listener -> this.getProxy().getPluginManager().registerListener(this, listener));
  }

  public RedisAdapter getRedisAdapter() {
    return AdapterPlugin.getInstance().getRedisAdapter();
  }

  public DatabaseAdapter getDatabaseAdapter() {
    return AdapterPlugin.getInstance().getDatabaseAdapter();
  }

  public PacketFactory getPacketFactory() {
    return AdapterPlugin.getInstance().getPacketFactory();
  }

  public CallbackFactory getCallbackFactory() {
    return AdapterPlugin.getInstance().getCallbackFactory();
  }

  public ScheduledExecutorService getScheduledExecutorService() {
    return AdapterPlugin.getInstance().getScheduledExecutorService();
  }
}