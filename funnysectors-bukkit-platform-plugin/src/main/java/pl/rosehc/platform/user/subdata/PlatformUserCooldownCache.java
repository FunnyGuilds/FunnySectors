package pl.rosehc.platform.user.subdata;

import java.util.Map;
import java.util.Objects;
import pl.rosehc.controller.packet.platform.user.PlatformUserCooldownSynchronizePacket;
import pl.rosehc.controller.wrapper.platform.PlatformUserCooldownType;
import pl.rosehc.platform.PlatformPlugin;
import pl.rosehc.platform.user.PlatformUser;

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
    PlatformPlugin.getInstance().getRedisAdapter()
        .sendPacket(new PlatformUserCooldownSynchronizePacket(this.user.getUniqueId(), type, time),
            "rhc_master_controller", "rhc_platform");
    this.putUserCooldown(type, time);
  }

  public void putUserCooldown(final PlatformUserCooldownType type, final long time) {
    if (!this.userCooldownMap.containsKey(type)) {
      this.userCooldownMap.put(type, time);
    } else {
      this.userCooldownMap.replace(type, time);
    }
  }

  public void removeUserCooldown(final PlatformUserCooldownType type) {
    this.userCooldownMap.remove(type);
  }

  public boolean hasUserCooldown(final PlatformUserCooldownType type) {
    final Long cooldown = this.userCooldownMap.get(type);
    return Objects.nonNull(cooldown) && cooldown >= System.currentTimeMillis();
  }

  public long getUserCooldown(final PlatformUserCooldownType type) {
    return this.hasUserCooldown(type) ? this.userCooldownMap.get(type) - System.currentTimeMillis()
        : 0L;
  }
}
