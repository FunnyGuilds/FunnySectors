package pl.rosehc.sectors.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import pl.rosehc.sectors.SectorsPlugin;
import pl.rosehc.sectors.sector.user.SectorUser;
import pl.rosehc.sectors.sector.user.SectorUserJoinEvent;

public final class PlayerJoinListener implements Listener {

  private final SectorsPlugin plugin;

  public PlayerJoinListener(final SectorsPlugin plugin) {
    this.plugin = plugin;
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onJoin(final PlayerJoinEvent event) {
    this.plugin.getSectorUserFactory().findUserByUniqueId(event.getPlayer().getUniqueId())
        .filter(SectorUser::isFirstJoin).ifPresent(user -> {
          this.plugin.getServer().getPluginManager()
              .callEvent(new SectorUserJoinEvent(user, event.getPlayer()));
          user.setFirstJoin(false);
        });
  }
}
