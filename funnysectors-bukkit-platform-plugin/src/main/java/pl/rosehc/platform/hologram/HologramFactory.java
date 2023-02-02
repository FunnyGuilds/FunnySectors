package pl.rosehc.platform.hologram;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.bukkit.entity.Player;

public final class HologramFactory {

  private final Multimap<UUID, Hologram> hologramMultimap = Multimaps.synchronizedMultimap(
      HashMultimap.create());

  public List<Hologram> findHologramList(final UUID uniqueId) {
    final Collection<Hologram> hologramCollection = this.hologramMultimap.get(uniqueId);
    return new ArrayList<>(hologramCollection);
  }

  public void addHologram(final Hologram hologram, final UUID uniqueId) {
    this.hologramMultimap.put(uniqueId, hologram);
  }

  public void removeHolograms(final Player player) {
    this.hologramMultimap.removeAll(player.getUniqueId());
  }
}
