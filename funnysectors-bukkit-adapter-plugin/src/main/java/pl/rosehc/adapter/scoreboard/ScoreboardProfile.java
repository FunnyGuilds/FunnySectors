package pl.rosehc.adapter.scoreboard;

import java.util.List;
import org.bukkit.entity.Player;

public interface ScoreboardProfile {

  List<String> getEntries(final Player player);

  String getTitle(final Player player);
}
