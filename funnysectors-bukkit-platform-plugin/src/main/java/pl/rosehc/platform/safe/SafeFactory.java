package pl.rosehc.platform.safe;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import pl.rosehc.controller.wrapper.platform.PlatformSafeSerializableWrapper;
import pl.rosehc.platform.PlatformPlugin;

public final class SafeFactory {

  private final Map<UUID, Safe> safeMap;

  public SafeFactory(final List<PlatformSafeSerializableWrapper> safes) {
    this.safeMap = new ConcurrentHashMap<>();
    for (final PlatformSafeSerializableWrapper safe : safes) {
      this.safeMap.put(safe.getUniqueId(), Safe.create(safe));
    }

    PlatformPlugin.getInstance().getLogger()
        .log(Level.INFO, "Załadowano " + this.safeMap.size() + " sejfów.");
  }

  public void addSafe(final Safe safe) {
    this.safeMap.put(safe.getUniqueId(), safe);
  }

  public Optional<Safe> findSafe(final UUID uniqueId) {
    return Optional.ofNullable(this.safeMap.get(uniqueId));
  }

  public Map<UUID, Safe> getSafeMap() {
    return this.safeMap;
  }
}
