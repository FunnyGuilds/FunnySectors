package pl.rosehc.controller.platform.user.task;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import pl.rosehc.controller.MasterController;
import pl.rosehc.controller.platform.user.PlatformUser;

public final class PlatformUserUpdateTask implements Runnable {

  private final MasterController masterController;

  public PlatformUserUpdateTask(final MasterController masterController) {
    this.masterController = masterController;
  }

  @Override
  public void run() {
    final List<PlatformUser> platformUserList = new ArrayList<>(
        this.masterController.getPlatformUserFactory().getUserMap().values());
    if (!platformUserList.isEmpty()) {
      try {
        this.masterController.getPlatformUserRepository().updateAll(platformUserList);
      } catch (final SQLException ex) {
        System.err.println(
            "[PLATFORMA] Wystąpił niespodziewany problem podczas próby zapisania graczy do bazy danych.");
        ex.printStackTrace();
      }
    }
  }
}
