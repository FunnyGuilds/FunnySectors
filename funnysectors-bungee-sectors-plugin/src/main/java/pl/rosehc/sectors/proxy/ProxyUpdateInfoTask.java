package pl.rosehc.sectors.proxy;

import com.sun.management.OperatingSystemMXBean;
import java.lang.management.ManagementFactory;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import pl.rosehc.controller.packet.proxy.ProxyUpdateStatisticsPacket;
import pl.rosehc.sectors.SectorsPlugin;

public final class ProxyUpdateInfoTask implements Runnable {

  private static final Object[] CPU_UPDATES = new Object[2];
  private final SectorsPlugin plugin;

  public ProxyUpdateInfoTask(final SectorsPlugin plugin) {
    this.plugin = plugin;
    this.plugin.getProxy().getScheduler().schedule(this.plugin, this, 1L, 1L, TimeUnit.SECONDS);
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

    this.plugin.getRedisAdapter().sendPacket(new ProxyUpdateStatisticsPacket(
        this.plugin.getProxyFactory().getCurrentProxy().getIdentifier(), (double) CPU_UPDATES[1],
        this.plugin.getProxy().getOnlineCount()), "rhc_global");
  }
}
