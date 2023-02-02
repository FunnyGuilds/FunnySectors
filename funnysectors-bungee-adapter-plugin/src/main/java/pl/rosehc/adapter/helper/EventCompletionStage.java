package pl.rosehc.adapter.helper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public final class EventCompletionStage {

  private final Map<Object, AtomicInteger> completionStages = new ConcurrentHashMap<>();
  private final AtomicInteger latch = new AtomicInteger();
  private final AtomicBoolean postCalled = new AtomicBoolean();
  private final Runnable completionAction;

  public EventCompletionStage(final Runnable completionAction) {
    this.completionAction = completionAction;
  }

  public void removeWaiter(final Object waiter) {
    final AtomicInteger stages = this.completionStages.get(waiter);
    if (stages == null || stages.get() < 1) {
      throw new IllegalStateException("This waiter has no stages registered!");
    }

    stages.decrementAndGet();
    if (this.postCalled.get() && this.latch.decrementAndGet() == 0) {
      this.preFire();
    } else if (!this.postCalled.get()) {
      this.latch.decrementAndGet();
    }
  }

  public void addWaiter(final Object waiter) {
    AtomicInteger stages = this.completionStages.get(waiter);
    if (stages == null) {
      stages = new AtomicInteger();
      this.completionStages.put(waiter, stages);
    }

    stages.incrementAndGet();
  }

  public void postFire() {
    if (this.latch.get() == 0) {
      this.preFire();
    }

    this.postCalled.set(true);
  }

  public void preFire() {
    this.completionAction.run();
  }
}