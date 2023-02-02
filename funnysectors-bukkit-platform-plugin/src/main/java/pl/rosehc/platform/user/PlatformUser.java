package pl.rosehc.platform.user;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import pl.rosehc.adapter.helper.SerializeHelper;
import pl.rosehc.controller.packet.platform.user.PlatformUserMessagePacket;
import pl.rosehc.controller.wrapper.platform.PlatformUserChatSettingsSerializableWrapper;
import pl.rosehc.controller.wrapper.platform.PlatformUserDropSettingsSerializableWrapper;
import pl.rosehc.controller.wrapper.platform.PlatformUserSerializableWrapper;
import pl.rosehc.platform.PlatformPlugin;
import pl.rosehc.platform.deposit.DepositItemType;
import pl.rosehc.platform.disco.DiscoEffectType;
import pl.rosehc.platform.kit.Kit;
import pl.rosehc.platform.rank.Rank;
import pl.rosehc.platform.rank.RankEntry;
import pl.rosehc.platform.rank.RankFactory;
import pl.rosehc.platform.user.subdata.PlatformUserChatSettings;
import pl.rosehc.platform.user.subdata.PlatformUserCooldownCache;
import pl.rosehc.platform.user.subdata.PlatformUserDropSettings;
import pl.rosehc.platform.user.subdata.PlatformUserRewardSettings;
import pl.rosehc.sectors.SectorsPlugin;

public final class PlatformUser {

  private final UUID uniqueId;
  private final PlatformUserCooldownCache cooldownCache;
  private final Map<UUID, Long> teleportRequestMap;
  private final PlatformUserDropSettings dropSettings;
  private final PlatformUserChatSettings chatSettings;
  private final PlatformUserRewardSettings rewardSettings;

  private String nickname;
  private UUID lastPrivateMessage;
  private RankEntry rank;
  private Location[] homes;
  private Map<Kit, Long> receivedKitMap;
  private Map<DepositItemType, Integer> depositItemMap;
  private Set<UUID> ignoredPlayerSet;
  private DiscoEffectType selectedDiscoEffectType;
  private long combatTime, endPortalChangeTime, groupTeleportsChangeTime;
  private boolean firstJoin, vanish, god, preparedForNameTagCreation, sneaking;

  private PlatformUser(final PlatformUserSerializableWrapper wrapper) {
    final String previousRankName = wrapper.getPreviousRankName();
    final String currentRankName = wrapper.getCurrentRankName();
    final RankFactory rankFactory = PlatformPlugin.getInstance().getRankFactory();
    final Optional<Rank> previousRank =
        Objects.nonNull(previousRankName) ? rankFactory.findRank(previousRankName)
            : Optional.empty();
    final Optional<Rank> currentRank =
        Objects.nonNull(currentRankName) ? rankFactory.findRank(currentRankName) : Optional.empty();
    final long rankExpirationTime = wrapper.getRankExpirationTime();
    this.uniqueId = wrapper.getUniqueId();
    this.cooldownCache = new PlatformUserCooldownCache(this, wrapper.getCooldownMap());
    this.teleportRequestMap = wrapper.getTeleportRequestMap();
    this.nickname = wrapper.getNickname();
    this.lastPrivateMessage = wrapper.getLastPrivateMessage();
    this.rank = RankEntry.create(previousRank.orElse(null),
        currentRank.orElse(rankFactory.getDefaultRank()),
        previousRank.map(ignored -> rankExpirationTime).orElse(0L));
    this.homes = wrapper.getHomes() != null ? Arrays.stream(wrapper.getHomes())
        .map(SerializeHelper::deserializeLocation).toArray(Location[]::new) : null;
    if (wrapper.getKitMap() != null) {
      final Map<Kit, Long> receivedKitMap = new ConcurrentHashMap<>();
      for (final Entry<String, Long> entry : wrapper.getKitMap().entrySet()) {
        PlatformPlugin.getInstance().getKitFactory().findKit(entry.getKey())
            .ifPresent(kit -> receivedKitMap.put(kit, entry.getValue()));
      }

      this.receivedKitMap = receivedKitMap;
    }

    final PlatformUserDropSettingsSerializableWrapper dropSettings = wrapper.getDropSettings();
    final PlatformUserChatSettingsSerializableWrapper chatSettings = wrapper.getChatSettings();
    this.dropSettings = new PlatformUserDropSettings(ConcurrentHashMap.newKeySet(),
        dropSettings.getTurboDropMultiplier(), dropSettings.isCobbleStone(),
        dropSettings.getTurboDropTime(), dropSettings.getCurrentXP(), dropSettings.getNeededXP(),
        dropSettings.getLevel());
    this.dropSettings.getDisabledDropSet().addAll(dropSettings.getDisabledDropSet().stream()
        .map(PlatformPlugin.getInstance().getDropFactory()::findDrop).filter(Optional::isPresent)
        .map(Optional::get).collect(Collectors.toList()));
    this.chatSettings = new PlatformUserChatSettings(chatSettings.isGlobal(),
        chatSettings.isItemShop(), chatSettings.isKills(), chatSettings.isDeaths(),
        chatSettings.isCases(), chatSettings.isAchievements(), chatSettings.isRewards(),
        chatSettings.isPrivateMessages());
    this.depositItemMap =
        wrapper.getDepositItemMap() != null ? wrapper.getDepositItemMap().entrySet().stream()
            .map(entry -> new SimpleEntry<>(entry.getKey().toOriginal(), entry.getValue()))
            .collect(Collectors.toConcurrentMap(SimpleEntry::getKey, SimpleEntry::getValue)) : null;
    this.ignoredPlayerSet = ConcurrentHashMap.newKeySet();
    if (wrapper.getIgnoredPlayerSet() != null) {
      this.ignoredPlayerSet.addAll(wrapper.getIgnoredPlayerSet());
    }

    this.selectedDiscoEffectType =
        wrapper.getSelectedDiscoEffectTypeName() != null ? DiscoEffectType.valueOf(
            wrapper.getSelectedDiscoEffectTypeName()) : null;
    this.rewardSettings = new PlatformUserRewardSettings(wrapper.isDiscordRewardReceived());
    this.combatTime = wrapper.getCombatTime();
    this.firstJoin = wrapper.isFirstJoin();
    this.vanish = wrapper.isVanish();
    this.god = wrapper.isGod();
  }

  public PlatformUser(final UUID uniqueId, final String nickname) {
    this.uniqueId = uniqueId;
    this.cooldownCache = new PlatformUserCooldownCache(this, new ConcurrentHashMap<>());
    this.teleportRequestMap = new ConcurrentHashMap<>();
    this.nickname = nickname;
    this.rank = RankEntry.create(null,
        PlatformPlugin.getInstance().getRankFactory().getDefaultRank(), 0L);
    this.dropSettings = new PlatformUserDropSettings(ConcurrentHashMap.newKeySet(), 0D, true, 0L, 0,
        500, 1);
    this.chatSettings = new PlatformUserChatSettings(true, true, true, true, true, true, true,
        true);
    this.rewardSettings = new PlatformUserRewardSettings(false);
    this.firstJoin = true;
  }

  public static PlatformUser create(final PlatformUserSerializableWrapper wrapper) {
    return new PlatformUser(wrapper);
  }

  public UUID getUniqueId() {
    return this.uniqueId;
  }

  public PlatformUserCooldownCache getCooldownCache() {
    return this.cooldownCache;
  }

  public Map<UUID, Long> getTeleportRequestMap() {
    return this.teleportRequestMap;
  }

  public boolean isOnline() {
    return SectorsPlugin.getInstance().getSectorUserFactory().getUserMap()
        .containsKey(this.uniqueId);
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

  public Location[] getHomes() {
    return this.homes;
  }

  public boolean doesNotHaveHome(int id) {
    return this.homes == null || this.homes[id] == null;
  }

  public void setHome(Location location, int id) {
    if (Objects.isNull(this.homes)) {
      this.homes = new Location[4];
    }

    this.homes[id] = location;
  }

  public Map<Kit, Long> getReceivedKitMap() {
    return this.receivedKitMap;
  }

  public boolean hasReceivedKit(final Kit kit) {
    if (Objects.isNull(this.receivedKitMap)) {
      return false;
    }

    return this.receivedKitMap.containsKey(kit)
        && this.receivedKitMap.get(kit) + kit.getTime() > System.currentTimeMillis();
  }

  public long getKitLeftTime(final Kit kit) {
    if (Objects.isNull(this.receivedKitMap)) {
      return 0L;
    }

    final Long duration = this.receivedKitMap.get(kit);
    return duration != null ? (duration + kit.getTime()) - System.currentTimeMillis() : 0L;
  }

  public void receiveKit(final Kit kit, final long time) {
    if (Objects.isNull(this.receivedKitMap)) {
      this.receivedKitMap = new ConcurrentHashMap<>();
    }

    this.receivedKitMap.put(kit, time);
  }

  public Map<DepositItemType, Integer> getDepositItemMap() {
    return this.depositItemMap;
  }

  public int getItemAmountInDeposit(final DepositItemType type) {
    if (Objects.isNull(this.depositItemMap)) {
      return 0;
    }

    return this.depositItemMap.getOrDefault(type, 0);
  }

  public void addItemToDeposit(final DepositItemType type, final int amount) {
    if (Objects.isNull(this.depositItemMap)) {
      this.depositItemMap = new ConcurrentHashMap<>();
    }

    final int currentAmount = this.depositItemMap.getOrDefault(type, 0);
    this.depositItemMap.put(type, currentAmount + amount);
  }

  public void removeItemFromDeposit(final DepositItemType type, final int amount) {
    if (Objects.isNull(this.depositItemMap)) {
      return;
    }

    final int currentAmount = this.depositItemMap.getOrDefault(type, 0);
    this.depositItemMap.put(type, Math.max(currentAmount - amount, 0));
  }

  public boolean updateIgnoredPlayer(final UUID uniqueId) {
    if (Objects.isNull(this.ignoredPlayerSet)) {
      this.ignoredPlayerSet = ConcurrentHashMap.newKeySet();
    }

    if (!this.ignoredPlayerSet.add(uniqueId)) {
      this.ignoredPlayerSet.remove(uniqueId);
      return false;
    }

    return true;
  }

  public boolean isIgnored(final UUID uniqueId) {
    return Objects.nonNull(this.ignoredPlayerSet) && this.ignoredPlayerSet.contains(uniqueId);
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

  public DiscoEffectType getSelectedDiscoEffectType() {
    return this.selectedDiscoEffectType;
  }

  public void setSelectedDiscoEffectType(final DiscoEffectType selectedDiscoEffectType) {
    this.selectedDiscoEffectType = selectedDiscoEffectType;
  }

  public long getCombatTime() {
    return this.combatTime;
  }

  public void setCombatTime(final long combatTime) {
    this.combatTime = combatTime;
  }

  public boolean isInCombat() {
    return this.combatTime != 0L && this.combatTime > System.currentTimeMillis();
  }

  public boolean isEndPortalChange() {
    return this.endPortalChangeTime + 2500L > System.currentTimeMillis();
  }

  public void setEndPortalChange(final boolean endPortalChange) {
    this.endPortalChangeTime = endPortalChange ? System.currentTimeMillis() : 0L;
  }

  public boolean isGroupTeleportsChange() {
    return this.groupTeleportsChangeTime + 3500L > System.currentTimeMillis();
  }

  public void setGroupTeleportsChange(final boolean groupTeleportsChange) {
    this.groupTeleportsChangeTime = groupTeleportsChange ? System.currentTimeMillis() : 0L;
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

  public boolean isPreparedForNameTagCreation() {
    return this.preparedForNameTagCreation;
  }

  public void setPreparedForNameTagCreation(final boolean preparedForNameTagCreation) {
    this.preparedForNameTagCreation = preparedForNameTagCreation;
  }

  public boolean isSneaking() {
    return this.sneaking;
  }

  public void setSneaking(final boolean sneaking) {
    this.sneaking = sneaking;
  }

  public void sendMessage(final String message) {
    SectorsPlugin.getInstance().getSectorUserFactory().findUserByUniqueId(this.uniqueId).ifPresent(
        user -> PlatformPlugin.getInstance().getRedisAdapter().sendPacket(
            new PlatformUserMessagePacket(Collections.singletonList(this.uniqueId), message),
            "rhc_platform_" + user.getSector().getName()));
  }

  public void reloadPermissions(final Player player) {
    final boolean containsAsterisk = this.rank.getCurrentRank().getPermissions().contains("*");
    if (player.isOp() != containsAsterisk) {
      player.setOp(containsAsterisk);
    }

    if (containsAsterisk) {
      return;
    }

    final PlatformPlugin plugin = PlatformPlugin.getInstance();
    for (final PermissionAttachmentInfo permission : new ArrayList<>(
        player.getEffectivePermissions())) {
      final PermissionAttachment attachment = permission.getAttachment();
      if (Objects.nonNull(attachment) && Objects.nonNull(attachment.getPlugin())
          && attachment.getPlugin().equals(plugin)) {
        player.addAttachment(plugin, permission.getPermission(), false);
      }
    }

    for (final String permission : this.rank.getCurrentRank().getPermissions()) {
      final boolean isAllowed = !permission.startsWith("-");
      player.addAttachment(plugin, !isAllowed ? permission.substring(1) : permission, isAllowed);
    }

    player.recalculatePermissions();
  }

  public void reloadPermissions() {
    final Player player = Bukkit.getPlayer(this.uniqueId);
    if (Objects.nonNull(player)) {
      this.reloadPermissions(player);
    }
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
