package pl.rosehc.controller.wrapper.platform;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class PlatformUserSerializableWrapper {

  private UUID uniqueId, lastPrivateMessage;
  private Map<PlatformUserCooldownType, Long> cooldownMap;
  private Map<UUID, Long> teleportRequestMap;
  private String nickname;
  private String previousRankName, currentRankName;
  private String[] homes;
  private Map<String, Long> kitMap;
  private Map<PlatformUserDepositItemTypeWrapper, Integer> depositItemMap;
  private Set<UUID> ignoredPlayerSet;
  private PlatformUserDropSettingsSerializableWrapper dropSettings;
  private PlatformUserChatSettingsSerializableWrapper chatSettings;
  private String selectedDiscoEffectTypeName;
  private byte[] computerUid;
  private long rankExpirationTime, combatTime;
  private boolean firstJoin, vanish, god, discordRewardReceived;

  private PlatformUserSerializableWrapper() {
  }

  public PlatformUserSerializableWrapper(final UUID uniqueId, final UUID lastPrivateMessage,
      final Map<PlatformUserCooldownType, Long> cooldownMap,
      final Map<UUID, Long> teleportRequestMap, final String nickname,
      final String previousRankName, final String currentRankName, final String[] homes,
      final Map<String, Long> kitMap,
      final Map<PlatformUserDepositItemTypeWrapper, Integer> depositItemMap,
      final Set<UUID> ignoredPlayerSet,
      final PlatformUserDropSettingsSerializableWrapper dropSettings,
      final PlatformUserChatSettingsSerializableWrapper chatSettings,
      final String selectedDiscoEffectTypeName, final byte[] computerUid,
      final long rankExpirationTime, final long combatTime, final boolean firstJoin,
      final boolean vanish, final boolean god, final boolean discordRewardReceived) {
    this.uniqueId = uniqueId;
    this.lastPrivateMessage = lastPrivateMessage;
    this.cooldownMap = cooldownMap;
    this.teleportRequestMap = teleportRequestMap;
    this.nickname = nickname;
    this.previousRankName = previousRankName;
    this.currentRankName = currentRankName;
    this.homes = homes;
    this.kitMap = kitMap;
    this.depositItemMap = depositItemMap;
    this.ignoredPlayerSet = ignoredPlayerSet;
    this.dropSettings = dropSettings;
    this.chatSettings = chatSettings;
    this.computerUid = computerUid;
    this.rankExpirationTime = rankExpirationTime;
    this.combatTime = combatTime;
    this.firstJoin = firstJoin;
    this.vanish = vanish;
    this.god = god;
    this.discordRewardReceived = discordRewardReceived;
  }

  public UUID getUniqueId() {
    return this.uniqueId;
  }

  public UUID getLastPrivateMessage() {
    return this.lastPrivateMessage;
  }

  public String getNickname() {
    return this.nickname;
  }

  public Map<PlatformUserCooldownType, Long> getCooldownMap() {
    return this.cooldownMap;
  }

  public Map<UUID, Long> getTeleportRequestMap() {
    return this.teleportRequestMap;
  }

  public String getPreviousRankName() {
    return this.previousRankName;
  }

  public String getCurrentRankName() {
    return this.currentRankName;
  }

  public String[] getHomes() {
    return this.homes;
  }

  public Map<String, Long> getKitMap() {
    return this.kitMap;
  }

  public Map<PlatformUserDepositItemTypeWrapper, Integer> getDepositItemMap() {
    return this.depositItemMap;
  }

  public Set<UUID> getIgnoredPlayerSet() {
    return this.ignoredPlayerSet;
  }

  public PlatformUserDropSettingsSerializableWrapper getDropSettings() {
    return this.dropSettings;
  }

  public PlatformUserChatSettingsSerializableWrapper getChatSettings() {
    return this.chatSettings;
  }

  public byte[] getComputerUid() {
    return this.computerUid;
  }

  public long getRankExpirationTime() {
    return this.rankExpirationTime;
  }

  public long getCombatTime() {
    return this.combatTime;
  }

  public boolean isFirstJoin() {
    return this.firstJoin;
  }

  public boolean isVanish() {
    return this.vanish;
  }

  public boolean isGod() {
    return this.god;
  }

  public boolean isDiscordRewardReceived() {
    return this.discordRewardReceived;
  }
}



