package pl.rosehc.controller.platform.user.task;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import pl.rosehc.controller.MasterController;
import pl.rosehc.controller.configuration.impl.configuration.PlatformConfiguration;
import pl.rosehc.controller.configuration.impl.configuration.PlatformConfiguration.AutoMessagesSettingsWrapper;
import pl.rosehc.controller.packet.platform.user.PlatformUserMessagePacket;
import pl.rosehc.controller.platform.user.PlatformUser;
import pl.rosehc.controller.platform.user.subdata.PlatformUserChatSettings;
import pl.rosehc.controller.sector.user.SectorUser;

public final class PlatformUserSendAutoMessageTask implements Runnable {

  private final MasterController masterController;
  private final AtomicInteger messageCount = new AtomicInteger();

  public PlatformUserSendAutoMessageTask(final MasterController masterController) {
    this.masterController = masterController;
  }

  @Override
  public void run() {
    //noinspection DuplicatedCode
    final List<UUID> uuidList = new ArrayList<>();
    for (final SectorUser user : this.masterController.getSectorUserFactory().getUserMap()
        .values()) {
      if (this.masterController.getPlatformUserFactory().findUserByUniqueId(user.getUniqueId())
          .map(PlatformUser::getChatSettings).filter(PlatformUserChatSettings::isGlobal)
          .isPresent()) {
        uuidList.add(user.getUniqueId());
      }
    }

    if (!uuidList.isEmpty()) {
      final AutoMessagesSettingsWrapper autoMessagesSettingsWrapper = this.masterController.getConfigurationFactory()
          .findConfiguration(PlatformConfiguration.class).autoMessagesSettingsWrapper;
      final String message = autoMessagesSettingsWrapper.messages.get(
          this.messageCount.incrementAndGet() % autoMessagesSettingsWrapper.messages.size());
      this.masterController.getRedisAdapter()
          .sendPacket(new PlatformUserMessagePacket(uuidList, message), "rhc_platform");
    }
  }
}
