package pl.rosehc.controller.configuration.impl.handler;

import pl.rosehc.controller.MasterController;
import pl.rosehc.controller.configuration.ConfigurationData;
import pl.rosehc.controller.configuration.ConfigurationHelper;
import pl.rosehc.controller.configuration.ConfigurationReloadHandler;
import pl.rosehc.controller.packet.configuration.ConfigurationSynchronizePacket;

public final class OnlySynchronizationReloadHandler<T extends ConfigurationData> implements
    ConfigurationReloadHandler<T> {

  private final MasterController masterController;

  private OnlySynchronizationReloadHandler(final MasterController masterController) {
    this.masterController = masterController;
  }

  public static <T extends ConfigurationData> OnlySynchronizationReloadHandler create(
      final Class<T> type) {
    return new OnlySynchronizationReloadHandler<>(MasterController.getInstance());
  }

  @Override
  public void handle(final T configuration) {
    this.masterController.getRedisAdapter().sendPacket(
        new ConfigurationSynchronizePacket(configuration.getClass().getName(),
            ConfigurationHelper.serializeConfiguration(configuration)), "rhc_global");
  }
}
