package pl.rosehc.controller.platform.user;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import pl.rosehc.controller.MasterController;
import pl.rosehc.controller.platform.rank.Rank;
import pl.rosehc.controller.platform.rank.RankEntry;
import pl.rosehc.controller.platform.rank.RankFactory;
import pl.rosehc.controller.platform.user.subdata.PlatformUserChatSettings;
import pl.rosehc.controller.platform.user.subdata.PlatformUserCooldownCache;
import pl.rosehc.controller.platform.user.subdata.PlatformUserDropSettings;
import pl.rosehc.controller.platform.user.subdata.PlatformUserRewardSettings;
import pl.rosehc.controller.wrapper.platform.PlatformUserDepositItemTypeWrapper;
import pl.rosehc.controller.wrapper.platform.PlatformUserSerializableWrapper;

public final class PlatformUser {

  private final UUID uniqueId;
  private final PlatformUserCooldownCache cooldownCache;
  private final Map<UUID, Long> teleportRequestMap = new ConcurrentHashMap<>();
  private final PlatformUserDropSettings dropSettings;
  private final PlatformUserChatSettings chatSettings;
  private final PlatformUserRewardSettings rewardSettings;

  private String nickname;
  private UUID lastPrivateMessage;
  private RankEntry rank;
  private String[] homes;
  private Map<String, Long> receivedKitMap;
  private Map<PlatformUserDepositItemTypeWrapper, Integer> depositItemMap;
  private Set<UUID> ignoredPlayerSet;
  private String selectedDiscoEffectTypeName;
  private byte[] computerUid;
  private long combatTime;
  private boolean firstJoin, vanish, god;

  public PlatformUser(final ResultSet result) throws SQLException {
    final String previousRankName = result.getString("previousRankName");
    final String currentRankName = result.getString("currentRankName");
    final String disabledDrops = result.getString("disabledDrops");
    final RankFactory rankFactory = MasterController.getInstance().getRankFactory();
    final Optional<Rank> previousRank =
        previousRankName != null ? rankFactory.findRank(previousRankName) : Optional.empty();
    final Optional<Rank> currentRank =
        currentRankName != null ? rankFactory.findRank(currentRankName) : Optional.empty();
    final Set<String> disabledDropSet = ConcurrentHashMap.newKeySet();
    final long rankExpirationTime = result.getLong("rankExpirationTime");
    if (disabledDrops != null) {
      disabledDropSet.addAll(Arrays.stream(disabledDrops.split(";")).collect(Collectors.toList()));
    }

    this.uniqueId = UUID.fromString(result.getString("uniqueId"));
    this.cooldownCache = new PlatformUserCooldownCache(this,
        PlatformUserSerializationHelper.deserializeCooldownMap(result.getString("cooldowns")));
    this.nickname = result.getString("nickname");
    this.lastPrivateMessage = result.getString("lastPrivateMessage") != null ? UUID.fromString(
        result.getString("lastPrivateMessage")) : null;
    this.rank = RankEntry.create(previousRank.orElse(null),
        currentRank.orElse(rankFactory.getDefaultRank()),
        previousRank.map(ignored -> rankExpirationTime).orElse(0L));
    this.homes = result.getString("homes") != null ? result.getString("homes").split("@") : null;
    this.receivedKitMap = PlatformUserSerializationHelper.deserializeReceivedKitMap(
        result.getString("receivedKits"));
    this.depositItemMap = PlatformUserSerializationHelper.deserializeDepositItemMap(
        result.getString("depositItems"));
    this.dropSettings = new PlatformUserDropSettings(disabledDropSet,
        result.getDouble("turboDropMultiplier"), result.getBoolean("cobbleStone"),
        result.getLong("turboDropTime"), result.getInt("currentXP"), result.getInt("neededXP"),
        result.getInt("level"));
    this.chatSettings = new PlatformUserChatSettings(result.getBoolean("globalChatStatus"),
        result.getBoolean("itemShopChatStatus"), result.getBoolean("killsChatStatus"),
        result.getBoolean("deathsKillStatus"), result.getBoolean("casesChatStatus"),
        result.getBoolean("achievementsChatStatus"), result.getBoolean("rewardsChatStatus"),
        result.getBoolean("privateChatMessagesStatus"));
    this.rewardSettings = new PlatformUserRewardSettings(result.getLong("discordUserId"),
        result.getBoolean("discordRewardReceived"));
    this.selectedDiscoEffectTypeName = result.getString("selectedDiscoEffectTypeName");
    this.computerUid = result.getBytes("computerUid");
    this.firstJoin = result.getBoolean("firstJoin");
    this.vanish = result.getBoolean("vanish");
    this.god = result.getBoolean("god");
  }

  public PlatformUser(final UUID uniqueId, final String nickname) {
    this.uniqueId = uniqueId;
    this.cooldownCache = new PlatformUserCooldownCache(this, new ConcurrentHashMap<>());
    this.nickname = nickname;
    this.rank = RankEntry.create(null,
        MasterController.getInstance().getRankFactory().getDefaultRank(), 0L);
    this.dropSettings = new PlatformUserDropSettings(ConcurrentHashMap.newKeySet(), 0D, true, 0L, 0,
        500, 1);
    this.chatSettings = new PlatformUserChatSettings(true, true, true, true, true, true, true,
        true);
    this.rewardSettings = new PlatformUserRewardSettings(0L, false);
  }

  public PlatformUserSerializableWrapper wrap() {
    return new PlatformUserSerializableWrapper(this.uniqueId, this.lastPrivateMessage,
        this.cooldownCache.getUserCooldownMap(), this.teleportRequestMap, this.nickname,
        Optional.ofNullable(this.rank.getPreviousRank()).map(Rank::getName).orElse(null),
        this.rank.getCurrentRank().getName(), this.homes, this.receivedKitMap, this.depositItemMap,
        this.ignoredPlayerSet, this.dropSettings.wrap(), this.chatSettings.wrap(),
        this.selectedDiscoEffectTypeName, this.computerUid, this.rank.getExpirationTime(),
        this.combatTime, this.firstJoin, this.vanish, this.god,
        this.rewardSettings.isDiscordRewardReceived());
  }

  public UUID getUniqueId() {
    return this.uniqueId;
  }

  public PlatformUserCooldownCache getCooldownCache() {
    return this.cooldownCache;
  }

  public boolean hasTeleportRequest(final UUID uniqueId) {
    return this.teleportRequestMap.containsKey(uniqueId)
        && this.teleportRequestMap.get(uniqueId) > System.currentTimeMillis();
  }

  public void addTeleportRequest(final UUID uniqueId) {
    this.teleportRequestMap.put(uniqueId, System.currentTimeMillis() + 120000L);
  }

  public void removeTeleportRequest(final UUID uniqueId) {
    this.teleportRequestMap.remove(uniqueId);
  }

  public String getNickname() {
    return this.nickname;
  }

  public void setNickname(final String nickname) {
    this.nickname = nickname;
  }

  public UUID getLastPrivateMessage() {
    return this.lastPrivateMessage;
  }

  public void setLastPrivateMessage(final UUID lastPrivateMessage) {
    this.lastPrivateMessage = lastPrivateMessage;
  }

  public RankEntry getRank() {
    return this.rank;
  }

  public void setRank(final RankEntry rank) {
    this.rank = rank;
  }

  public String[] getHomes() {
    return this.homes;
  }

  public void setHome(final String location, final int id) {
    if (Objects.isNull(this.homes)) {
      this.homes = new String[4];
    }

    this.homes[id] = location;
  }

  public Map<String, Long> getReceivedKitMap() {
    return this.receivedKitMap;
  }

  public void receiveKit(final String kitName, final long time) {
    if (Objects.isNull(this.receivedKitMap)) {
      this.receivedKitMap = new ConcurrentHashMap<>();
    }

    this.receivedKitMap.put(kitName.toLowerCase(), time);
  }

  public Map<PlatformUserDepositItemTypeWrapper, Integer> getDepositItemMap() {
    return this.depositItemMap;
  }

  public void addItemToDeposit(final PlatformUserDepositItemTypeWrapper type, final int amount) {
    if (Objects.isNull(this.depositItemMap)) {
      this.depositItemMap = new ConcurrentHashMap<>();
    }

    final int currentAmount = this.depositItemMap.getOrDefault(type, 0);
    this.depositItemMap.put(type, currentAmount + amount);
  }

  public void removeItemFromDeposit(final PlatformUserDepositItemTypeWrapper type,
      final int amount) {
    if (Objects.isNull(this.depositItemMap)) {
      return;
    }

    final int currentAmount = this.depositItemMap.getOrDefault(type, 0);
    this.depositItemMap.put(type, Math.max(currentAmount - amount, 0));
  }

  public Set<UUID> getIgnoredPlayerSet() {
    return this.ignoredPlayerSet;
  }

  public void addIgnoredPlayer(final UUID uniqueId) {
    if (Objects.isNull(this.ignoredPlayerSet)) {
      this.ignoredPlayerSet = ConcurrentHashMap.newKeySet();
    }

    this.ignoredPlayerSet.add(uniqueId);
  }

  public void removeIgnoredPlayer(final UUID uniqueId) {
    if (Objects.nonNull(this.ignoredPlayerSet)) {
      this.ignoredPlayerSet.remove(uniqueId);
    }
  }

  public PlatformUserDropSettings getDropSettings() {
    return this.dropSettings;
  }

  public PlatformUserChatSettings getChatSettings() {
    return this.chatSettings;
  }

  public PlatformUserRewardSettings getRewardSettings() {
    return this.rewardSettings;
  }

  public String getSelectedDiscoEffectTypeName() {
    return this.selectedDiscoEffectTypeName;
  }

  public void setSelectedDiscoEffectTypeName(final String selectedDiscoEffectTypeName) {
    this.selectedDiscoEffectTypeName = selectedDiscoEffectTypeName;
  }

  public byte[] getComputerUid() {
    return this.computerUid;
  }

  public void setComputerUid(final byte[] computerUid) {
    this.computerUid = computerUid;
  }

  public long getCombatTime() {
    return this.combatTime;
  }

  public void setCombatTime(final long combatTime) {
    this.combatTime = combatTime;
  }

  public boolean isFirstJoin() {
    return this.firstJoin;
  }

  public void setFirstJoin(final boolean firstJoin) {
    this.firstJoin = firstJoin;
  }

  public boolean isVanish() {
    return this.vanish;
  }

  public void setVanish(final boolean vanish) {
    this.vanish = vanish;
  }

  public boolean isGod() {
    return this.god;
  }

  public void setGod(final boolean god) {
    this.god = god;
  }

  @Override
  public boolean equals(final Object object) {
    if (this == object) {
      return true;
    }

    if (object == null || this.getClass() != object.getClass()) {
      return false;
    }

    final PlatformUser user = (PlatformUser) object;
    return this.uniqueId.equals(user.uniqueId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.uniqueId);
  }
}
