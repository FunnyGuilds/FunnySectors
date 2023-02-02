package pl.rosehc.controller;

import java.lang.reflect.Method;

public final class MasterControllerBootstrap {

  public static void main(final String[] args) throws Exception {
    final Class<?> clazz = Class.forName("pl.rosehc.controller.MasterController");
    final Object instance = clazz.newInstance();
    final Method start = clazz.getDeclaredMethod("start");
    final Method stop = clazz.getDeclaredMethod("stop");
    new Thread((SafeRunnable) () -> start.invoke(instance)).start();
    Runtime.getRuntime().addShutdownHook(new Thread((SafeRunnable) () -> stop.invoke(instance)));
  }
}
