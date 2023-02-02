package pl.rosehc.platform.hologram;

import org.bukkit.entity.Player;

@FunctionalInterface
public interface HologramAction {

  HologramAction EMPTY = (ignored1, ignored2, ignored3, ignored4) -> {
  };

  void accept(final Player player, final Hologram hologram, final HologramEntry entry,
      final HologramActionType type);
}
