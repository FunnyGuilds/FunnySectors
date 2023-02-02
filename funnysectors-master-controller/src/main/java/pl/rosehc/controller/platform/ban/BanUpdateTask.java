package pl.rosehc.controller.platform.ban;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import pl.rosehc.controller.MasterController;

public final class BanUpdateTask implements Runnable {

  private final MasterController masterController;

  public BanUpdateTask(final MasterController masterController) {
    this.masterController = masterController;
  }

  @Override
  public void run() {
    final List<Ban> banList = new ArrayList<>(
        this.masterController.getBanFactory().getBanMap().values());
    if (!banList.isEmpty()) {
      try {
        this.masterController.getBanRepository().updateAll(banList);
      } catch (final SQLException ex) {
        System.err.println(
            "[PLATFORMA] Wystąpił niespodziewany problem podczas próby zapisania banów do bazy danych.");
        ex.printStackTrace();
      }
    }
  }
}
