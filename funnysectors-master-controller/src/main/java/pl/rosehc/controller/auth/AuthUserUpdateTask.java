package pl.rosehc.controller.auth;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import pl.rosehc.controller.MasterController;

public final class AuthUserUpdateTask implements Runnable {

  private final MasterController masterController;

  public AuthUserUpdateTask(final MasterController masterController) {
    this.masterController = masterController;
  }

  @Override
  public void run() {
    final List<AuthUser> authUserList = new ArrayList<>(
        this.masterController.getAuthUserFactory().getUserMap().values());
    if (!authUserList.isEmpty()) {
      try {
        this.masterController.getAuthUserRepository().updateAll(authUserList);
      } catch (final SQLException ex) {
        System.err.println(
            "[LOGOWANIE] Wystąpił niespodziewany problem podczas próby zapisania graczy do bazy danych.");
        ex.printStackTrace();
      }
    }
  }
}
