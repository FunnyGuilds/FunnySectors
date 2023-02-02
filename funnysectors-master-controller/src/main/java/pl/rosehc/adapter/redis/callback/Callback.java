package pl.rosehc.adapter.redis.callback;

/**
 * @author stevimeister on 25/08/2021
 **/
public interface Callback {

  void done(final CallbackPacket packet);

  void error(final String message);
}
