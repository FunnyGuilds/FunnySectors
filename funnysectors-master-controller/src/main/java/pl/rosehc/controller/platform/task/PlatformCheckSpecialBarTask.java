package pl.rosehc.controller.platform.task;

import pl.rosehc.controller.MasterController;
import pl.rosehc.controller.configuration.ConfigurationHelper;
import pl.rosehc.controller.configuration.impl.configuration.PlatformConfiguration;
import pl.rosehc.controller.configuration.impl.configuration.PlatformConfiguration.SpecialBossBarWrapper;
import pl.rosehc.controller.packet.platform.PlatformSpecialBossBarUpdatePacket;

public final class PlatformCheckSpecialBarTask implements Runnable {

  private final MasterController masterController;

  public PlatformCheckSpecialBarTask(final MasterController masterController) {
    this.masterController = masterController;
  }

  @Override
  public void run() {
    final PlatformConfiguration platformConfiguration = this.masterController.getConfigurationFactory()
        .findConfiguration(PlatformConfiguration.class);
    final SpecialBossBarWrapper specialBossBarWrapper = platformConfiguration.specialBossBarWrapper;
    if (specialBossBarWrapper != null && specialBossBarWrapper.expiryTime != 0L
        && specialBossBarWrapper.expiryTime < System.currentTimeMillis()) {
      platformConfiguration.specialBossBarWrapper = null;
      this.masterController.getRedisAdapter()
          .sendPacket(new PlatformSpecialBossBarUpdatePacket(null), "rhc_platform");
      ConfigurationHelper.saveConfiguration(platformConfiguration);
    }
  }
}
