package pl.rosehc.controller;

public interface SafeRunnable extends Runnable {

  void runSafely() throws Exception;

  @Override
  default void run() {
    try {
      this.runSafely();
    } catch (final Exception e) {
      e.printStackTrace();
      System.exit(0);
    }
  }
}