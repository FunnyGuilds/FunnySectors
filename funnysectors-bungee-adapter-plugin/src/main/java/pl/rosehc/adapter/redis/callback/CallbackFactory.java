package pl.rosehc.adapter.redis.callback;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author stevimeister on 25/08/2021
 **/
public final class CallbackFactory {

  private final Cache<Long, Callback> callbackCache = Caffeine.newBuilder()
      .expireAfterWrite(1L, TimeUnit.MINUTES).build();

  public void addCallback(final long id, Callback callback) {
    this.callbackCache.put(id, callback);
  }

  public Callback findCallbackById(final long id) {
    final Callback callback = this.callbackCache.getIfPresent(id);
    if (Objects.nonNull(callback)) {
      this.callbackCache.invalidate(id);
      return callback;
    }

    return null;
  }
}

