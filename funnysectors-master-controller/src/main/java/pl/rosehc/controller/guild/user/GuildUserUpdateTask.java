package pl.rosehc.controller.guild.user;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import pl.rosehc.controller.MasterController;

public final class GuildUserUpdateTask implements Runnable {

  private final MasterController masterController;

  public GuildUserUpdateTask(final MasterController masterController) {
    this.masterController = masterController;
  }

  @Override
  public void run() {
    final List<GuildUser> guildUserList = new ArrayList<>(
        this.masterController.getGuildUserFactory().getUserMap().values());
    if (!guildUserList.isEmpty()) {
      try {
        this.masterController.getGuildUserRepository().updateAll(guildUserList);
      } catch (final SQLException ex) {
        System.err.println(
            "[GILDIE] Wystąpił niespodziewany problem podczas próby zapisania użytkowników do bazy danych.");
        ex.printStackTrace();
      }
    }
  }
}
