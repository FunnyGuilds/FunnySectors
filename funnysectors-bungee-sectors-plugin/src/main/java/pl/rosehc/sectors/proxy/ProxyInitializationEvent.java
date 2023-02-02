package pl.rosehc.sectors.proxy;

import net.md_5.bungee.api.plugin.Event;
import pl.rosehc.adapter.helper.EventCompletionStage;

public final class ProxyInitializationEvent extends Event {

  private final Proxy proxy;
  private final boolean success;
  private EventCompletionStage completionStage;

  public ProxyInitializationEvent(final Proxy proxy, final boolean success) {
    this.proxy = proxy;
    this.success = success;
  }

  public Proxy getProxy() {
    return this.proxy;
  }

  public boolean isSuccess() {
    return this.success;
  }

  public EventCompletionStage getCompletionStage() {
    return this.completionStage;
  }

  public void setCompletionStage(final EventCompletionStage completionStage) {
    this.completionStage = completionStage;
  }
}
