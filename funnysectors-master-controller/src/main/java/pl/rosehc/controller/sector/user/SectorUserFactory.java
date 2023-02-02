package pl.rosehc.controller.sector.user;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class SectorUserFactory {

  private final Map<UUID, SectorUser> userMap = new ConcurrentHashMap<>();

  public void addUser(final SectorUser user) {
    this.userMap.put(user.getUniqueId(), user);
  }

  public void removeUser(final SectorUser user) {
    this.userMap.remove(user.getUniqueId());
  }

  public Optional<SectorUser> findUserByUniqueId(final UUID uniqueId) {
    return Optional.ofNullable(this.userMap.get(uniqueId));
  }

  public Optional<SectorUser> findUserByNickname(final String nickname) {
    for (final SectorUser user : this.userMap.values()) {
      if (user.getNickname().equalsIgnoreCase(nickname)) {
        return Optional.of(user);
      }
    }

    return Optional.empty();
  }

  public Map<UUID, SectorUser> getUserMap() {
    return this.userMap;
  }
}
