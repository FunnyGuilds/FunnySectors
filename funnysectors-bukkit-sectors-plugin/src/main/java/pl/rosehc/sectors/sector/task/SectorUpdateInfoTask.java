package pl.rosehc.sectors.sector.task;

import com.sun.management.OperatingSystemMXBean;
import java.lang.management.ManagementFactory;
import java.util.Objects;
import pl.rosehc.controller.packet.sector.SectorUpdateStatisticsPacket;
import pl.rosehc.sectors.SectorsPlugin;

public final class SectorUpdateInfoTask implements Runnable {

  private static final Object[] CPU_UPDATES = new Object[2];
  private final SectorsPlugin plugin;

  public SectorUpdateInfoTask(final SectorsPlugin plugin) {
    this.plugin = plugin;
    this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(this.plugin, this, 20L, 20L);
  }

  @Override
  public void run() {
    final long now = System.currentTimeMillis();
    if (Objects.isNull(CPU_UPDATES[0])
        || (long) CPU_UPDATES[0] + 5000L <= now) { // double-checked locking pattern
      synchronized (CPU_UPDATES) {
        if (Objects.isNull(CPU_UPDATES[0]) || (long) CPU_UPDATES[0] + 5000L <= now) {
          CPU_UPDATES[0] = now;
          CPU_UPDATES[1] = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class)
              .getProcessCpuLoad();
        }
      }
    }

    this.plugin.getRedisAdapter().sendPacket(new SectorUpdateStatisticsPacket(
        this.plugin.getSectorFactory().getCurrentSector().getName(),
        this.plugin.getServer().spigot().getTPS()[0], (double) CPU_UPDATES[1],
        this.plugin.getServer().getOnlinePlayers().size()), "rhc_global");
  }
}
