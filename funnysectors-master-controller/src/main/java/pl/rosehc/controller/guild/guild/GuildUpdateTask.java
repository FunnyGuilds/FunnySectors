package pl.rosehc.controller.guild.guild;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import pl.rosehc.controller.MasterController;

public final class GuildUpdateTask implements Runnable {

  private final MasterController masterController;

  public GuildUpdateTask(final MasterController masterController) {
    this.masterController = masterController;
  }

  @Override
  public void run() {
    final List<Guild> guildList = new ArrayList<>(
        this.masterController.getGuildFactory().getGuildMap().values());
    if (!guildList.isEmpty()) {
      try {
        this.masterController.getGuildRepository().updateAll(guildList);
      } catch (final SQLException ex) {
        System.err.println(
            "[GILDIE] Wystąpił niespodziewany problem podczas próby zapisania gildii do bazy danych.");
        ex.printStackTrace();
      }
    }
  }
}
