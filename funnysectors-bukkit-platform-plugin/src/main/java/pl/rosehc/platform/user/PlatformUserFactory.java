package pl.rosehc.platform.user;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import pl.rosehc.controller.wrapper.platform.PlatformUserSerializableWrapper;
import pl.rosehc.platform.PlatformPlugin;

public final class PlatformUserFactory {

  private final Map<UUID, PlatformUser> userMap;

  public PlatformUserFactory(final List<PlatformUserSerializableWrapper> users) {
    this.userMap = new ConcurrentHashMap<>();
    for (final PlatformUserSerializableWrapper user : users) {
      this.userMap.put(user.getUniqueId(), PlatformUser.create(user));
    }

    PlatformPlugin.getInstance().getLogger()
        .log(Level.INFO, "Załadowano " + this.userMap.size() + " użytkowników.");
  }

  public void addUser(final PlatformUser user) {
    this.userMap.put(user.getUniqueId(), user);
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
