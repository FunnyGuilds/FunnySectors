package pl.rosehc.platform.drop;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import pl.rosehc.adapter.helper.NumberHelper;
import pl.rosehc.platform.PlatformConfiguration;
import pl.rosehc.platform.PlatformConfiguration.DropSettingsWrapper.DropWrapper;
import pl.rosehc.platform.user.PlatformUser;

public final class DropFactory {

  private final Map<String, Drop> dropMap;

  public DropFactory(final PlatformConfiguration platformConfiguration) {
    this.dropMap = platformConfiguration.dropSettingsWrapper.dropWrapperList.stream()
        .map(DropWrapper::asDrop)
        .collect(Collectors.toMap(drop -> drop.getName().toLowerCase(), drop -> drop));
  }

  public Optional<Drop> findRandomDrop(final Player player, final Location location,
      final PlatformUser user) {
    for (final Drop drop : this.dropMap.values()) {
      if (user.getDropSettings().getDisabledDropSet().contains(drop)) {
        continue;
      }

      if (location.getBlockY() < drop.getMinY() || location.getBlockY() > drop.getMaxY()) {
        continue;
      }

      final double chance = drop.calculateChance(player, user);
      if (chance >= 100D || chance >= NumberHelper.range(0D, 100D)) {
        return Optional.of(drop);
      }
    }

    return Optional.empty();
  }

  public Optional<Drop> findDrop(final String name) {
    return Optional.ofNullable(this.dropMap.get(name.toLowerCase()));
  }

  public Map<String, Drop> getDropMap() {
    return this.dropMap;
  }
}
