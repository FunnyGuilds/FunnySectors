package pl.rosehc.sectors.sector.user;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import pl.rosehc.controller.wrapper.sector.SectorUserSerializableWrapper;

public final class SectorUserFactory {

  private final Map<UUID, SectorUser> userMap;

  public SectorUserFactory(final List<SectorUserSerializableWrapper> sectorUsers) {
    this.userMap = sectorUsers.stream().map(SectorUser::unwrap)
        .collect(Collectors.toConcurrentMap(SectorUser::getUniqueId, user -> user));
  }

  public void addUser(final SectorUser user) {
    this.userMap.put(user.getUniqueId(), user);
  }

  public void removeUser(final SectorUser user) {
    this.userMap.remove(user.getUniqueId());
  }

  public SectorUser findUserByPlayer(final ProxiedPlayer player) {
    return this.userMap.get(player.getUniqueId());
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
