package pl.rosehc.controller;

public final class ControllerPanicHelper {

  private static volatile long lastControllerUpdateTime;
  private static volatile long proxyEnableTime;
  private static volatile boolean panicEnabled;

  private ControllerPanicHelper() {
  }

  public static synchronized boolean isInPanic() {
    if ((proxyEnableTime != 0L && proxyEnableTime <= System.currentTimeMillis())
        && lastControllerUpdateTime <= System.currentTimeMillis()) {
      panicEnabled = true;
    }

    return panicEnabled;
  }

  public static synchronized void markEnabled() {
    proxyEnableTime = System.currentTimeMillis() + 5000L;
  }

  public static synchronized void update() {
    if (!panicEnabled) {
      lastControllerUpdateTime = System.currentTimeMillis() + 2000L;
    }
  }
}
