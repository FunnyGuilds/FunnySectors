package pl.rosehc.controller.platform.safe;

import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import pl.rosehc.controller.MasterController;

public final class SafeFactory {

  private final Map<UUID, Safe> safeMap;
  private final AtomicBoolean shutdownExecuted = new AtomicBoolean();

  public SafeFactory(final MasterController masterController) throws SQLException {
    this.safeMap = masterController.getSafeRepository().loadAll();
    System.out.println("[PLATFORMA] Załadowano " + this.safeMap.size() + " sejfów.");
  }

  public void addSafe(final Safe safe) {
    if (!this.shutdownExecuted.get()) {
      this.safeMap.put(safe.getUniqueId(), safe);
    }
  }

  public void markToShutdown() {
    this.shutdownExecuted.set(true);
  }

  public Optional<Safe> findSafe(final UUID uniqueId) {
    return Optional.ofNullable(this.safeMap.get(uniqueId));
  }

  public Map<UUID, Safe> getSafeMap() {
    return this.safeMap;
  }
}
