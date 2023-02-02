package pl.rosehc.controller.platform.user.subdata;

import java.util.Map;
import pl.rosehc.controller.MasterController;
import pl.rosehc.controller.packet.platform.user.PlatformUserCooldownSynchronizePacket;
import pl.rosehc.controller.platform.user.PlatformUser;
import pl.rosehc.controller.wrapper.platform.PlatformUserCooldownType;

public final class PlatformUserCooldownCache {

  private final PlatformUser user;
  private final Map<PlatformUserCooldownType, Long> userCooldownMap;

  public PlatformUserCooldownCache(final PlatformUser user,
      final Map<PlatformUserCooldownType, Long> userCooldownMap) {
    this.user = user;
    this.userCooldownMap = userCooldownMap;
  }

  public void putUserCooldown(final PlatformUserCooldownType type) {
    final long time = System.currentTimeMillis() + type.getDefaultValue();
    MasterController.getInstance().getRedisAdapter()
        .sendPacket(new PlatformUserCooldownSynchronizePacket(this.user.getUniqueId(), type, time),
            "rhc_platform");
    this.putUserCooldown(type, time);
  }

  public void putUserCooldown(final PlatformUserCooldownType type, final long time) {
    if (!this.userCooldownMap.containsKey(type)) {
      this.userCooldownMap.put(type, time);
    } else {
      this.userCooldownMap.replace(type, time);
    }
  }

  public boolean hasUserCooldown(final PlatformUserCooldownType type) {
    final Long cooldown = this.userCooldownMap.get(type);
    return cooldown != null && cooldown >= System.currentTimeMillis();
  }

  public long getUserCooldown(final PlatformUserCooldownType type) {
    return this.hasUserCooldown(type) ? this.userCooldownMap.get(type) : 0L;
  }

  public Map<PlatformUserCooldownType, Long> getUserCooldownMap() {
    return this.userCooldownMap;
  }
}
