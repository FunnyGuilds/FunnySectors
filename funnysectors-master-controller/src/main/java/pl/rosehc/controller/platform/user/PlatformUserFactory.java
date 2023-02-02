package pl.rosehc.controller.platform.user;

import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import pl.rosehc.controller.MasterController;

public final class PlatformUserFactory {

  private final Map<UUID, PlatformUser> userMap;
  private final AtomicBoolean shutdownExecuted = new AtomicBoolean();

  public PlatformUserFactory(final MasterController masterController) throws SQLException {
    this.userMap = masterController.getPlatformUserRepository().loadAll();
    System.out.println("[PLATFORMA] Załadowano " + this.userMap.size() + " użytkowników.");
  }

  public void addUser(final PlatformUser user) {
    if (!this.shutdownExecuted.get()) {
      this.userMap.put(user.getUniqueId(), user);
    }
  }

  public void markToShutdown() {
    this.shutdownExecuted.set(true);
  }

  public Optional<PlatformUser> findUserByUniqueId(final UUID uniqueId) {
    return Optional.ofNullable(this.userMap.get(uniqueId));
  }

  public Optional<PlatformUser> findUserByNickname(final String nickname) {
    for (final PlatformUser user : this.userMap.values()) {
      if (user.getNickname().equalsIgnoreCase(nickname)) {
        return Optional.of(user);
      }
    }

    return Optional.empty();
  }

  public Map<UUID, PlatformUser> getUserMap() {
    return this.userMap;
  }
}
