package pl.rosehc.adapter;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import net.md_5.bungee.api.plugin.Plugin;
import pl.rosehc.adapter.database.DatabaseAdapter;
import pl.rosehc.adapter.helper.ConfigurationHelper;
import pl.rosehc.adapter.redis.RedisAdapter;
import pl.rosehc.adapter.redis.callback.CallbackFactory;
import pl.rosehc.adapter.redis.packet.PacketFactory;

/**
 * @author stevimeister on 19/11/2021
 **/
public final class AdapterPlugin extends Plugin {

  private static AdapterPlugin instance;

  private RedisAdapter redisAdapter;
  private DatabaseAdapter databaseAdapter;

  private PacketFactory packetFactory;
  private CallbackFactory callbackFactory;

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

    this.packetFactory = new PacketFactory();
    this.callbackFactory = new CallbackFactory();

    this.scheduledExecutorService = Executors.newScheduledThreadPool(4);
  }

  public RedisAdapter getRedisAdapter() {
    return this.redisAdapter;
  }

  public DatabaseAdapter getDatabaseAdapter() {
    return this.databaseAdapter;
  }

  public PacketFactory getPacketFactory() {
    return this.packetFactory;
  }

  public CallbackFactory getCallbackFactory() {
    return this.callbackFactory;
  }

  public ScheduledExecutorService getScheduledExecutorService() {
    return this.scheduledExecutorService;
  }
}