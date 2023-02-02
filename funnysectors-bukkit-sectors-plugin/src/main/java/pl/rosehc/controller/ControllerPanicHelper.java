package pl.rosehc.controller;

public final class ControllerPanicHelper {

  private static volatile long lastControllerUpdateTime;
  private static volatile long sectorEnableTime, sectorDisableTime;
  private static volatile boolean panicEnabled;

  private ControllerPanicHelper() {
  }

  public static synchronized boolean isInPanic() {
    if ((sectorEnableTime != 0L && sectorEnableTime <= System.currentTimeMillis())
        && lastControllerUpdateTime <= System.currentTimeMillis()) {
      panicEnabled = true;
    }

    if (panicEnabled && sectorDisableTime == 0L) {
      sectorDisableTime = System.currentTimeMillis() + 10_000L;
    }

    return panicEnabled;
  }

  public static synchronized boolean canDisable() {
    return sectorDisableTime > 0L && sectorDisableTime <= System.currentTimeMillis();
  }

  public static synchronized long getTimeToDisable() {
    return sectorDisableTime > 0L ? sectorDisableTime - System.currentTimeMillis() : 0L;
  }

  public static synchronized void markEnabled() {
    sectorEnableTime = System.currentTimeMillis() + 5000L;
  }

  public static synchronized void update() {
    if (!panicEnabled) {
      lastControllerUpdateTime = System.currentTimeMillis() + 2000L;
    }
  }

  public static synchronized void markDisabling() {
    sectorDisableTime = -1L;
  }
}
