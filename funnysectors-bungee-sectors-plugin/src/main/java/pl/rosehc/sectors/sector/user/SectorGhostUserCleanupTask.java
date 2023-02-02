package pl.rosehc.sectors.sector.user;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import pl.rosehc.controller.packet.sector.user.SectorUserDeletePacket;
import pl.rosehc.sectors.SectorsPlugin;

public final class SectorGhostUserCleanupTask implements Runnable {

  private final SectorsPlugin plugin;

  public SectorGhostUserCleanupTask(final SectorsPlugin plugin) {
    this.plugin = plugin;
    this.plugin.getProxy().getScheduler().schedule(this.plugin, this, 1L, 1L, TimeUnit.MINUTES);
  }

  @Override
  public void run() {
    for (final SectorUser user : this.plugin.getSectorUserFactory().getUserMap().values()) {
      if (user.getProxy().equals(this.plugin.getProxyFactory().getCurrentProxy())
          && Objects.isNull(this.plugin.getProxy().getPlayer(user.getUniqueId()))) {
        this.plugin.getRedisAdapter()
            .sendPacket(new SectorUserDeletePacket(user.getUniqueId()), "rhc_global");
      }
    }
  }
}
