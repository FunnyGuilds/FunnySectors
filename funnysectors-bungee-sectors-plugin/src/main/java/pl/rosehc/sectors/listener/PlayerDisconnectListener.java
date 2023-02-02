package pl.rosehc.sectors.listener;

import java.util.Map.Entry;
import java.util.UUID;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import pl.rosehc.controller.packet.sector.user.SectorUserDeletePacket;
import pl.rosehc.sectors.SectorsPlugin;
import pl.rosehc.sectors.sector.user.SectorUser;

public final class PlayerDisconnectListener implements Listener {

  private final SectorsPlugin plugin;

  public PlayerDisconnectListener(final SectorsPlugin plugin) {
    this.plugin = plugin;
  }

  @EventHandler
  public void onDisconnect(final PlayerDisconnectEvent event) {
    final ProxiedPlayer player = event.getPlayer();
    for (Entry<UUID, SectorUser> entry : this.plugin.getSectorUserFactory().getUserMap()
        .entrySet()) {
      if (entry.getKey().equals(player.getUniqueId()) && !entry.getValue().getProxy()
          .equals(this.plugin.getProxyFactory().getCurrentProxy())) {
        return;
      }
    }

    this.plugin.getSectorUserFactory().findUserByUniqueId(player.getUniqueId()).ifPresent(user -> {
      this.plugin.getSectorUserFactory().removeUser(user);
      this.plugin.getRedisAdapter()
          .sendPacket(new SectorUserDeletePacket(user.getUniqueId()), "rhc_global");
    });
  }
}
