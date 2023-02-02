package pl.rosehc.platform.disco.task;

import java.util.Arrays;
import pl.rosehc.platform.PlatformPlugin;
import pl.rosehc.platform.disco.DiscoEffectType;

public final class DiscoEffectUpdateTask implements Runnable {

  public DiscoEffectUpdateTask(final PlatformPlugin plugin) {
    plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, this, 0L, 20L);
  }

  @Override
  public void run() {
    Arrays.stream(DiscoEffectType.values()).forEach(effectType -> effectType.getEffect().update());
  }
}
