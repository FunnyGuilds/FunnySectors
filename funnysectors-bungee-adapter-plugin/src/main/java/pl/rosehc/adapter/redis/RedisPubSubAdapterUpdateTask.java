package pl.rosehc.adapter.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public final class RedisPubSubAdapterUpdateTask implements Runnable {

  private final JedisPool pool;
  private final RedisPubSubAdapter adapter;
  private final byte[] channel;

  public RedisPubSubAdapterUpdateTask(final JedisPool pool, final RedisPubSubAdapter adapter,
      final byte[] channel) {
    this.pool = pool;
    this.adapter = adapter;
    this.channel = channel;
  }

  @Override
  public void run() {
    while (!this.pool.isClosed()) {
      Jedis jedis;
      try {
        jedis = this.pool.getResource();
      } catch (final Exception e) {
        throw new RuntimeException("Cannot open the subscriber connection.", e);
      }

      jedis.subscribe(this.adapter, this.channel);
      try {
        jedis.close();
      } catch (final Exception ignored) {
      }
    }
  }
}
