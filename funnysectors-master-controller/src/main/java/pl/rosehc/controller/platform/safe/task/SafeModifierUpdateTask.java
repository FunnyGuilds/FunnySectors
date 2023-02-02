package pl.rosehc.controller.platform.safe.task;

import java.util.Objects;
import java.util.UUID;
import pl.rosehc.controller.MasterController;
import pl.rosehc.controller.packet.platform.safe.PlatformSafeModifierUpdatePacket;
import pl.rosehc.controller.platform.safe.Safe;

public final class SafeModifierUpdateTask implements Runnable {

  private final MasterController masterController;

  public SafeModifierUpdateTask(final MasterController masterController) {
    this.masterController = masterController;
  }

  @Override
  public void run() {
    for (final Safe safe : this.masterController.getSafeFactory().getSafeMap().values()) {
      UUID modifierUUID = safe.getModifierUuid();
      if (Objects.nonNull(modifierUUID) && (
          !this.masterController.getPlatformUserFactory().findUserByUniqueId(modifierUUID)
              .isPresent() || !this.masterController.getSectorUserFactory()
              .findUserByUniqueId(modifierUUID).isPresent())) {
        safe.setModifierUuid(null);
        modifierUUID = null;
      }

      this.masterController.getRedisAdapter()
          .sendPacket(new PlatformSafeModifierUpdatePacket(safe.getUniqueId(), modifierUUID),
              "rhc_platform");
    }
  }
}
