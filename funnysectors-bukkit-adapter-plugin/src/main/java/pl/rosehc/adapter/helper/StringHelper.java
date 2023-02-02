package pl.rosehc.adapter.helper;

import com.google.common.base.Strings;
import java.util.Arrays;
import net.md_5.bungee.api.ChatColor;

public final class StringHelper {

  private StringHelper() {
  }

  public static String getProgressBar(final int current, final int max, final int total,
      final char symbol) {
    final float percentage = (float) current / max;
    final int bars = (int) (total * percentage);
    return Strings.repeat(ChatColor.GREEN.toString() + symbol, total - bars) + Strings.repeat(
        ChatColor.RED.toString() + symbol, bars);
  }

  public static boolean equals(final String string, final String... equalities) {
    return Arrays.asList(equalities).contains(string);
  }
}
