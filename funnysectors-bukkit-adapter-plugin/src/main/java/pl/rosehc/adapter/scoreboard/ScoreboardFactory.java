package pl.rosehc.adapter.scoreboard;

import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.rosehc.adapter.AdapterPlugin;

public final class ScoreboardFactory {

  private final Map<UUID, ScoreboardPlayer> scoreboardMap = Maps.newConcurrentMap();
  private ScoreboardProfile selectedScoreboardProfile;

  public void setSelectedScoreboardProfile(final ScoreboardProfile scoreboardProfile) {
    if (Objects.isNull(scoreboardProfile) && Objects.nonNull(this.selectedScoreboardProfile)) {
      for (final ScoreboardPlayer player : this.scoreboardMap.values()) {
        player.remove();
      }

      this.scoreboardMap.clear();
      this.selectedScoreboardProfile = null;
    } else if (Objects.nonNull(scoreboardProfile) && Objects.isNull(
        this.selectedScoreboardProfile)) {
      this.selectedScoreboardProfile = scoreboardProfile;
      for (final Player player : Bukkit.getOnlinePlayers()) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(AdapterPlugin.getInstance(),
            () -> this.createScoreboard(player));
      }
    }
  }

  public void createScoreboard(final Player player) {
    if (Objects.isNull(this.selectedScoreboardProfile)) {
      return;
    }

    final ScoreboardPlayer scoreboardPlayer = new ScoreboardPlayer(player,
        this.selectedScoreboardProfile);
    this.scoreboardMap.put(player.getUniqueId(), scoreboardPlayer);
    AdapterPlugin.getInstance().getScheduledExecutorService().schedule(() -> {
      if (player.isOnline()) {
        scoreboardPlayer.update(true);
      }
    }, 2L, TimeUnit.SECONDS);
  }

  public void removeScoreboard(final UUID uniqueId) {
    final ScoreboardPlayer player = this.scoreboardMap.remove(uniqueId);
    if (Objects.nonNull(player)) {
      player.remove();
    }
  }

  public ScoreboardPlayer getScoreboard(final Player player) {
    return this.scoreboardMap.get(player.getUniqueId());
  }

  public Collection<ScoreboardPlayer> getScoreboardCollection() {
    return Collections.unmodifiableCollection(this.scoreboardMap.values());
  }
}