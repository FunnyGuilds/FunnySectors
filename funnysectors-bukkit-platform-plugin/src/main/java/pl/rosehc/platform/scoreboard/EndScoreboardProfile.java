package pl.rosehc.platform.scoreboard;

import java.util.ArrayList;
import java.util.List;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import pl.rosehc.adapter.helper.TimeHelper;
import pl.rosehc.adapter.scoreboard.ScoreboardProfile;
import pl.rosehc.platform.PlatformPlugin;
import pl.rosehc.sectors.SectorsPlugin;
import pl.rosehc.sectors.sector.Sector;

public final class EndScoreboardProfile implements ScoreboardProfile {

  private final PlatformPlugin plugin;

  public EndScoreboardProfile(final PlatformPlugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public List<String> getEntries(final Player player) {
    final Sector currentSector = SectorsPlugin.getInstance().getSectorFactory().getCurrentSector();
    final List<String> formattedEntries = new ArrayList<>();
    for (String entry : this.plugin.getPlatformConfiguration().endScoreboardProfileWrapper.endScoreboardEntries) {
      entry = entry.replace("{SECTOR_NAME}", currentSector.getName());
      entry = entry.replace("{ONLINE_PLAYERS}",
          String.valueOf(currentSector.getStatistics().getPlayers()));
      entry = entry.replace("{MAX_PLAYERS}",
          String.valueOf(this.plugin.getPlatformConfiguration().slotWrapper.spigotSlots));
      entry = entry.replace("{FORMATTED_TPS}", format(currentSector.getStatistics().getTps()));
      entry = entry.replace("{TELEPORTATION_POINT_CHANGE_TIME}", TimeHelper.timeToString(
          this.plugin.getEndPointFactory().getLastTeleportationPointUpdateTime()
              - System.currentTimeMillis()));
      entry = entry.replace("{PORTAL_POINT_CHANGE_TIME}", TimeHelper.timeToString(
          this.plugin.getEndPointFactory().getLastPortalPointUpdateTime()
              - System.currentTimeMillis()));
      formattedEntries.add(entry);
    }

    return formattedEntries;
  }

  @Override
  public String getTitle(final Player player) {
    return this.plugin.getPlatformConfiguration().endScoreboardProfileWrapper.endScoreboardTitle;
  }

  private String format(final double tps) {
    return (tps > 18D ? ChatColor.GREEN : (tps > 16D ? ChatColor.YELLOW : ChatColor.RED).toString())
        + (tps > 20D ? "*" : "") + Math.min(Math.round(tps * 100D) / 100D, 20D);
  }
}
