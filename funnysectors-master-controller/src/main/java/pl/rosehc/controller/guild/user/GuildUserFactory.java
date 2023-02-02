package pl.rosehc.controller.guild.user;

import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import pl.rosehc.controller.MasterController;

public final class GuildUserFactory {

  private final Map<UUID, GuildUser> userMap;
  private final AtomicBoolean shutdownExecuted = new AtomicBoolean();

  public GuildUserFactory(final MasterController masterController) throws SQLException {
    this.userMap = masterController.getGuildUserRepository().loadAll();
    System.out.println("[GILDIE] Załadowano " + this.userMap.size() + " użytkowników.");
  }

  public void addUser(final GuildUser user) {
    if (!this.shutdownExecuted.get()) {
      this.userMap.put(user.getUniqueId(), user);
    }
  }

  public void markToShutdown() {
    this.shutdownExecuted.set(true);
  }

  public Optional<GuildUser> findUserByUniqueId(final UUID uniqueId) {
    return Optional.ofNullable(this.userMap.get(uniqueId));
  }

  public Optional<GuildUser> findUserByNickname(final String nickname) {
    for (final GuildUser user : this.userMap.values()) {
      if (user.getNickname().equalsIgnoreCase(nickname)) {
        return Optional.of(user);
      }
    }

    return Optional.empty();
  }

  public Map<UUID, GuildUser> getUserMap() {
    return this.userMap;
  }
}
