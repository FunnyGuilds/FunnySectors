package pl.rosehc.platform.kit;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import pl.rosehc.adapter.helper.TimeHelper;
import pl.rosehc.platform.PlatformConfiguration;

public final class KitFactory {

  private final Map<String, Kit> kitMap;

  public KitFactory(final PlatformConfiguration platformConfiguration) {
    this.kitMap = platformConfiguration.kitWrapperList.stream().map(
            wrapper -> new Kit(wrapper.name, wrapper.permission, wrapper.items.stream().map(
                    kitItemWrapper -> new SimpleEntry<>(kitItemWrapper.id, kitItemWrapper.asItemStack()))
                .collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue)),
                TimeHelper.timeFromString(wrapper.time)))
        .collect(Collectors.toConcurrentMap(kit -> kit.getName().toLowerCase(), kit -> kit));
  }

  public Optional<Kit> findKit(final String name) {
    return Optional.ofNullable(this.kitMap.get(name.toLowerCase()));
  }

  public Map<String, Kit> getKitMap() {
    return this.kitMap;
  }
}
