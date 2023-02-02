package pl.rosehc.sectors.sector.task;

import java.util.Objects;
import pl.rosehc.controller.packet.sector.user.SectorUserDeletePacket;
import pl.rosehc.sectors.SectorsPlugin;
import pl.rosehc.sectors.sector.user.SectorUser;

public final class SectorGhostUserCleanupTask implements Runnable {

  private final SectorsPlugin plugin;

  public SectorGhostUserCleanupTask(final SectorsPlugin plugin) {
    this.plugin = plugin;
    this.plugin.getServer().getScheduler()
        .runTaskTimerAsynchronously(this.plugin, this, 1200L, 1200L);
  }

  @Override
  public void run() {
    for (final SectorUser user : this.plugin.getSectorUserFactory().getUserMap().values()) {
      if ((Objects.nonNull(user.getSector()) && user.getSector()
          .equals(this.plugin.getSectorFactory().getCurrentSector()))
          && Objects.isNull(this.plugin.getServer().getPlayer(user.getUniqueId()))) {
        this.plugin.getRedisAdapter()
            .sendPacket(new SectorUserDeletePacket(user.getUniqueId()), "rhc_global");
      }
    }
  }
}
