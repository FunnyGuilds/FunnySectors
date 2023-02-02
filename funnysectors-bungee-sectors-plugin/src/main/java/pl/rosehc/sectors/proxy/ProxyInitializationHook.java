package pl.rosehc.sectors.proxy;

import pl.rosehc.adapter.helper.EventCompletionStage;

@FunctionalInterface
public interface ProxyInitializationHook {

  void onInitialize(final EventCompletionStage completionStage, final Proxy proxy,
      final boolean success);
}
