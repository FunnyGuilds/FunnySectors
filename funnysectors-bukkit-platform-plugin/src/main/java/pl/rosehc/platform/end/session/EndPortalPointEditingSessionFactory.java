package pl.rosehc.platform.end.session;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class EndPortalPointEditingSessionFactory {

  private final Map<UUID, EndPortalPointEditingSession> sessionMap = new ConcurrentHashMap<>();

  public void addSession(final EndPortalPointEditingSession session) {
    this.sessionMap.put(session.getUniqueId(), session);
  }

  public void removeSession(final EndPortalPointEditingSession session) {
    this.sessionMap.remove(session.getUniqueId());
  }

  public Optional<EndPortalPointEditingSession> findSession(final UUID uniqueId) {
    return Optional.ofNullable(this.sessionMap.get(uniqueId));
  }
}
