package pl.rosehc.controller.auth;

import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import pl.rosehc.controller.MasterController;

public final class AuthUserFactory {

  private final Map<String, AuthUser> userMap;
  private final AtomicBoolean shutdownExecuted = new AtomicBoolean();

  public AuthUserFactory(final MasterController masterController) throws SQLException {
    this.userMap = masterController.getAuthUserRepository().loadAll();
    System.out.println("[LOGOWANIE] Załadowano " + this.userMap.size() + " użytkowników.");
  }

  public void addUser(final AuthUser user) {
    if (!this.shutdownExecuted.get()) {
      this.userMap.put(user.getNickname().toLowerCase(), user);
    }
  }

  public void removeUser(final AuthUser user) {
    if (!this.shutdownExecuted.get()) {
      this.userMap.remove(user.getNickname().toLowerCase());
    }
  }

  public void markToShutdown() {
    this.shutdownExecuted.set(true);
  }

  public Optional<AuthUser> findUser(final String nickname) {
    return Optional.ofNullable(this.userMap.get(nickname.toLowerCase()));
  }

  public Map<String, AuthUser> getUserMap() {
    return this.userMap;
  }
}
