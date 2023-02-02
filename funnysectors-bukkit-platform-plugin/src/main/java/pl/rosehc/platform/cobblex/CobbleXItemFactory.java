package pl.rosehc.platform.cobblex;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import pl.rosehc.platform.PlatformConfiguration;

public final class CobbleXItemFactory {

  private final List<CobbleXItem> cobbleXItemList;
  private final Lock lock = new ReentrantLock();

  public CobbleXItemFactory(final PlatformConfiguration platformConfiguration) {
    this.cobbleXItemList = platformConfiguration.cobbleXItemWrappers.stream()
        .map(CobbleXItem::create).collect(Collectors.toList());
  }

  public void executeLocked(final Consumer<List<CobbleXItem>> consumer) {
    try {
      this.lock.lock();
      consumer.accept(this.cobbleXItemList);
    } finally {
      this.lock.unlock();
    }
  }
}
