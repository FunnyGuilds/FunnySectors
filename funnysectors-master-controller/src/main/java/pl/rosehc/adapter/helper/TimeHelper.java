package pl.rosehc.adapter.helper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author stevimeister on 03/10/2021
 **/
public final class TimeHelper {

  private static final SimpleDateFormat FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

  private TimeHelper() {
  }

  public static String timeToString(final long time) {
    if (time < 1L) {
      return "< 1s";
    }

    final long months = TimeUnit.MILLISECONDS.toDays(time) / 30L;
    final long days = TimeUnit.MILLISECONDS.toDays(time) % 30L;
    final long hours = TimeUnit.MILLISECONDS.toHours(time) - TimeUnit.DAYS
        .toHours(TimeUnit.MILLISECONDS.toDays(time));
    final long minutes = TimeUnit.MILLISECONDS.toMinutes(time) - TimeUnit.HOURS
        .toMinutes(TimeUnit.MILLISECONDS.toHours(time));
    final long seconds = TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES
        .toSeconds(TimeUnit.MILLISECONDS.toMinutes(time));

    final StringBuilder stringBuilder = new StringBuilder();
    if (months > 0L) {
      stringBuilder.append(months).append("msc")
          .append(" ");
    }
    if (days > 0L) {
      stringBuilder.append(days).append("d")
          .append(" ");
    }
    if (hours > 0L) {
      stringBuilder.append(hours).append("h")
          .append(" ");
    }
    if (minutes > 0L) {
      stringBuilder.append(minutes).append("m")
          .append(" ");
    }
    if (seconds > 0L) {
      stringBuilder.append(seconds).append("s");
    }

    return stringBuilder.length() > 0 ? stringBuilder.toString().trim() : time + "ms";
  }

  public static long timeFromString(final String string) {
    if (string.isEmpty()) {
      return 0L;
    }

    StringBuilder stringBuilder = new StringBuilder();
    long time = 0L;

    for (char character : string.toCharArray()) {
      if (Character.isDigit(character)) {
        stringBuilder.append(character);
        continue;
      }

      int amount = Integer.parseInt(stringBuilder.toString());
      switch (character) {
        case 'd': {
          time += TimeUnit.DAYS.toMillis(amount);
          break;
        }

        case 'h': {
          time += TimeUnit.HOURS.toMillis(amount);
          break;
        }

        case 'm': {
          time += TimeUnit.MINUTES.toMillis(amount);
          break;
        }

        case 's': {
          time += TimeUnit.SECONDS.toMillis(amount);
        }
      }

      stringBuilder = new StringBuilder();
    }

    return time;
  }

  public static String dateToString(long time) {
    final Date date = new Date(time);
    return FORMAT.format(date);
  }
}

