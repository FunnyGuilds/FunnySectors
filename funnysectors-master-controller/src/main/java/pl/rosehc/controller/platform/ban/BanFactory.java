package pl.rosehc.controller.platform.ban;

import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import pl.rosehc.controller.MasterController;

public final class BanFactory {

  private final Map<String, Ban> banMap;
  private final AtomicBoolean shutdownExecuted = new AtomicBoolean();

  public BanFactory(final MasterController masterController) throws SQLException {
    this.banMap = masterController.getBanRepository().loadAll();
    System.out.println("[PLATFORMA] Załadowano " + this.banMap.size() + " banów.");
  }

  public void addBan(final Ban ban) {
    if (!this.shutdownExecuted.get()) {
      this.banMap.put(ban.getPlayerNickname().toLowerCase(), ban);
    }
  }

  public void removeBan(final Ban ban) {
    if (!this.shutdownExecuted.get()) {
      this.banMap.remove(ban.getPlayerNickname().toLowerCase());
    }
  }

  public void markToShutdown() {
    this.shutdownExecuted.set(true);
  }

  public Optional<Ban> findBan(final String playerNickname) {
    return Optional.ofNullable(this.banMap.get(playerNickname.toLowerCase()));
  }

  public Map<String, Ban> getBanMap() {
    return this.banMap;
  }
}
