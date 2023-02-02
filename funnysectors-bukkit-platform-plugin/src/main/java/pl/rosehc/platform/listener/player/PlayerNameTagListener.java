package pl.rosehc.platform.listener.player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import pl.rosehc.adapter.nametag.NameTagPlayerEvent;
import pl.rosehc.platform.PlatformPlugin;
import pl.rosehc.platform.rank.RankEntry;

/**
 * @author stevimeister on 30/01/2022
 **/
public final class PlayerNameTagListener implements Listener {

  private final PlatformPlugin plugin;

  public PlayerNameTagListener(final PlatformPlugin plugin) {
    this.plugin = plugin;
  }

  @EventHandler
  public void onNameTag(final NameTagPlayerEvent event) {
    this.plugin.getPlatformUserFactory()
        .findUserByUniqueId(event.getPlayer().getUniqueId()).ifPresent(user -> {
          final RankEntry rank = user.getRank();
          event.setPrefix(rank.getCurrentRank().getNameTagPrefix());
          event.setSuffix(rank.getCurrentRank().getNameTagSuffix() + (user.isVanish()
              ? this.plugin.getPlatformConfiguration().messagesWrapper.vanishNameTagSuffix : ""));
        });
  }
}
