package pl.rosehc.adapter.redis.callback;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author stevimeister on 25/08/2021
 **/
public class CallbackFactory {

  private final Map<Long, Callback> callbackMap = new ConcurrentHashMap<>();

  public void addCallback(final long id, Callback callback) {
    this.callbackMap.putIfAbsent(id, callback);
  }

  public Callback findCallbackById(final long id) {
    return this.callbackMap.remove(id);
  }
}

