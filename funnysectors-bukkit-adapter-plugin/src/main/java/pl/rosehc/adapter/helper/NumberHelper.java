package pl.rosehc.adapter.helper;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author stevimeister on 19/01/2022
 **/
public final class NumberHelper {

  private NumberHelper() {
  }

  public static int range(final int min, final int max) {
    return ThreadLocalRandom.current()
        .nextInt(min, max);
  }

  public static double range(final double min, final double max) {
    return ThreadLocalRandom.current()
        .nextDouble(min, max);
  }

  public static double parseDouble(final String string, final double defaultValue) {
    try {
      return Double.parseDouble(string);
    } catch (final NumberFormatException ex) {
      return defaultValue;
    }
  }

  public static double parseDouble(final String string) {
    return parseDouble(string, 0.0D);
  }

  public static int parseInt(final String string, final int defaultValue) {
    try {
      return Integer.parseInt(string);
    } catch (final NumberFormatException ex) {
      return defaultValue;
    }
  }

  public static int parseInt(final String string) {
    return parseInt(string, 0);
  }
}
