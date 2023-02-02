package pl.rosehc.adapter.redis.callback;

import java.util.concurrent.ThreadLocalRandom;
import pl.rosehc.adapter.redis.packet.Packet;

/**
 * @author stevimeister on 25/08/2021
 **/
public abstract class CallbackPacket extends Packet {

  private long callbackId = ThreadLocalRandom.current().nextLong(Long.MIN_VALUE, Long.MAX_VALUE);
  private boolean response, success;
  private String responseText;

  public long getCallbackId() {
    return this.callbackId;
  }

  public void setCallbackId(long callbackId) {
    this.callbackId = callbackId;
  }

  public boolean isResponse() {
    return this.response;
  }

  public void setResponse(final boolean response) {
    this.response = response;
  }

  public boolean hasSucceeded() {
    return this.success;
  }

  public void setSuccess(final boolean success) {
    this.success = success;
  }

  public String getResponseText() {
    return this.responseText;
  }

  public void setResponseText(final String responseText) {
    this.responseText = responseText;
  }
}
