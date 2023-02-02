package pl.rosehc.adapter.scoreboard;

import java.util.List;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import pl.rosehc.adapter.helper.ChatHelper;

public final class ScoreboardPlayer {

  private static final int MAX_LINE_LENGTH = Integer.parseInt(
      System.getProperty("rosehc.max_sidebar_line_length", "64"));
  private final Player player;
  private final Scoreboard scoreboard;
  private ScoreboardProfile profile;
  private boolean firstInitialized;
  private int lastSentCount = -1;

  public ScoreboardPlayer(final Player player, final ScoreboardProfile profile) {
    this.player = player;
    this.profile = profile;

    this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    player.setScoreboard(this.scoreboard);
  }

  public void setProfile(final ScoreboardProfile profile) {
    this.profile = profile;
  }

  public void update(final boolean ignoreInitialization) {
    if (!this.firstInitialized && !ignoreInitialization) {
      return;
    }

    final Objective objective = this.getOrCreateObjective();
    final String title = this.profile.getTitle(this.player);
    final List<String> entries = this.profile.getEntries(this.player);
    objective.setDisplayName(ChatHelper.colored(title));

    for (int i = 0; i < entries.size(); i++) {
      final Team team = getOrCreateTeam(ChatColor.stripColor(title.substring(0, 5)) + i, i);
      final String[] split = this.split(ChatHelper.colored(entries.get(entries.size() - i - 1)));
      team.setPrefix(split[0]);
      team.setSuffix(split[1]);
      objective.getScore(getNameForIndex(i)).setScore(i + 1);
    }

    if (this.lastSentCount != -1) {
      for (int i = 0; i < this.lastSentCount - entries.size(); i++) {
        this.removeLine(entries.size() + i);
      }
    }

    this.lastSentCount = entries.size();
    if (!this.firstInitialized) {
      this.firstInitialized = true;
    }
  }

  public void remove() {
    this.scoreboard.getTeams().forEach(Team::unregister);
    this.scoreboard.getObjectives().forEach(Objective::unregister);
  }

  private void removeLine(final int i) {
    final String name = getNameForIndex(i);
    this.scoreboard.resetScores(name);

    final Team team = getOrCreateTeam(name, i);
    team.unregister();
  }

  private String getNameForIndex(final int i) {
    return ChatColor.values()[i].toString() + ChatColor.RESET;
  }

  private Objective getOrCreateObjective() {
    Objective objective = this.scoreboard.getObjective("rosehc");
    if (Objects.isNull(objective)) {
      objective = this.scoreboard.registerNewObjective("rosehc", "dummy");
      objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    return objective;
  }

  private Team getOrCreateTeam(final String name, final int i) {
    Team team = this.scoreboard.getTeam(name);

    if (Objects.isNull(team)) {
      team = this.scoreboard.registerNewTeam(name);
      team.addEntry(getNameForIndex(i));
    }

    return team;
  }

  private String[] split(final String input) {
    if (input.length() > MAX_LINE_LENGTH) {
      String prefix = input.substring(0, MAX_LINE_LENGTH);
      String suffix;
      final int lastColorIndex = prefix.lastIndexOf(ChatColor.COLOR_CHAR);

      if (lastColorIndex >= (MAX_LINE_LENGTH - 2)) {
        prefix = prefix.substring(0, lastColorIndex);
        suffix = ChatColor.getLastColors(input.substring(0, MAX_LINE_LENGTH + 1)) + input.substring(
            lastColorIndex + 2);
      } else {
        suffix = ChatColor.getLastColors(prefix) + input.substring(MAX_LINE_LENGTH);
      }

      if (suffix.length() > MAX_LINE_LENGTH) {
        suffix = suffix.substring(0, MAX_LINE_LENGTH);
      }

      return new String[]{prefix, suffix};
    }

    return new String[]{input, ""};
  }
}