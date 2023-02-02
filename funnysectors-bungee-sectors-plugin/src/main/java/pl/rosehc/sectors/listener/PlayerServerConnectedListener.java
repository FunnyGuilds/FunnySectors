package pl.rosehc.sectors.listener;

import java.util.Optional;
import java.util.UUID;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import pl.rosehc.controller.packet.sector.user.SectorUserCreatePacket;
import pl.rosehc.controller.packet.sector.user.SectorUserDeletePacket;
import pl.rosehc.controller.packet.sector.user.SectorUserUpdateSectorPacket;
import pl.rosehc.sectors.SectorsPlugin;
import pl.rosehc.sectors.proxy.Proxy;
import pl.rosehc.sectors.sector.Sector;
import pl.rosehc.sectors.sector.user.SectorUser;

public final class PlayerServerConnectedListener implements Listener {

  private final SectorsPlugin plugin;

  public PlayerServerConnectedListener(final SectorsPlugin plugin) {
    this.plugin = plugin;
  }

  @EventHandler
  public void onServerConnected(final ServerConnectedEvent event) {
    final ProxiedPlayer player = event.getPlayer();
    final Optional<Sector> sectorOptional = this.plugin.getSectorFactory()
        .findSector(event.getServer().getInfo().getName());
    if (!sectorOptional.isPresent()) {
      this.plugin.getSectorUserFactory().findUserByUniqueId(player.getUniqueId())
          .ifPresent(user -> {
            this.plugin.getSectorUserFactory().removeUser(user);
            this.plugin.getRedisAdapter()
                .sendPacket(new SectorUserDeletePacket(user.getUniqueId()), "rhc_global");
          });
      return;
    }

    final Sector sector = sectorOptional.get();
    final SectorUser user = this.plugin.getSectorUserFactory()
        .findUserByUniqueId(player.getUniqueId()).orElseGet(
            () -> this.createUser(player.getUniqueId(), player.getName(),
                this.plugin.getProxyFactory().getCurrentProxy(), sector));
    if (!user.getSector().equals(sector)) {
      user.setSector(sector);
      this.plugin.getRedisAdapter()
          .sendPacket(new SectorUserUpdateSectorPacket(player.getUniqueId(), sector.getName()),
              "rhc_global");
    }
  }

  private SectorUser createUser(final UUID uniqueId, final String name, final Proxy currentProxy,
      final Sector sector) {
    final SectorUser user = new SectorUser(uniqueId, name, currentProxy, sector);
    this.plugin.getSectorUserFactory().addUser(user);
    this.plugin.getRedisAdapter().sendPacket(
        new SectorUserCreatePacket(user.getUniqueId(), user.getNickname(),
            user.getSector().getName(), user.getProxy().getIdentifier()), "rhc_global");
    return user;
  }
}
