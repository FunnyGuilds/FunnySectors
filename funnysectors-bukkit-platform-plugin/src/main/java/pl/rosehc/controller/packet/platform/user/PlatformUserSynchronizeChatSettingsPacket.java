package pl.rosehc.controller.packet.platform.user;

import java.util.UUID;
import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.PlatformPacketHandler;

public final class PlatformUserSynchronizeChatSettingsPacket extends Packet {

  private UUID uniqueId;
  private boolean global, itemShop;
  private boolean kills, deaths;
  private boolean cases, achievements;
  private boolean rewards, privateMessages;

  private PlatformUserSynchronizeChatSettingsPacket() {
  }

  public PlatformUserSynchronizeChatSettingsPacket(final UUID uniqueId, final boolean global,
      final boolean itemShop, final boolean kills, final boolean deaths, final boolean cases,
      final boolean achievements, final boolean rewards, final boolean privateMessages) {
    this.uniqueId = uniqueId;
    this.global = global;
    this.itemShop = itemShop;
    this.kills = kills;
    this.deaths = deaths;
    this.cases = cases;
    this.achievements = achievements;
    this.rewards = rewards;
    this.privateMessages = privateMessages;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((PlatformPacketHandler) handler).handle(this);
  }

  public UUID getUniqueId() {
    return this.uniqueId;
  }

  public boolean isGlobal() {
    return this.global;
  }

  public boolean isItemShop() {
    return this.itemShop;
  }

  public boolean isKills() {
    return this.kills;
  }

  public boolean isDeaths() {
    return this.deaths;
  }

  public boolean isCases() {
    return this.cases;
  }

  public boolean isAchievements() {
    return this.achievements;
  }

  public boolean isRewards() {
    return this.rewards;
  }

  public boolean isPrivateMessages() {
    return this.privateMessages;
  }
}
