package pl.rosehc.controller.platform.safe.task;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import pl.rosehc.controller.MasterController;
import pl.rosehc.controller.platform.safe.Safe;

public final class SafeUpdateTask implements Runnable {

  private final MasterController masterController;

  public SafeUpdateTask(final MasterController masterController) {
    this.masterController = masterController;
  }

  @Override
  public void run() {
    final List<Safe> safeList = new ArrayList<>(
        this.masterController.getSafeFactory().getSafeMap().values());
    if (!safeList.isEmpty()) {
      try {
        this.masterController.getSafeRepository().updateAll(safeList);
      } catch (final SQLException ex) {
        System.err.println(
            "[PLATFORMA] Wystąpił niespodziewany problem podczas próby zapisania sejfów do bazy danych.");
        ex.printStackTrace();
      }
    }
  }
}
