package pl.rosehc.platform.hologram;

import org.bukkit.entity.Player;

@FunctionalInterface
public interface HologramText {

  static HologramText staticText(final String text) {
    return ignored -> text;
  }

  String get(final Player player);
}
