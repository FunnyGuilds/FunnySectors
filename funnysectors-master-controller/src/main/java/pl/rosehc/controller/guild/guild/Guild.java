package pl.rosehc.controller.guild.guild;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import pl.rosehc.controller.MasterController;
import pl.rosehc.controller.configuration.impl.configuration.GuildsConfiguration;
import pl.rosehc.controller.guild.guild.group.GuildGroup;
import pl.rosehc.controller.guild.user.GuildUser;
import pl.rosehc.controller.packet.platform.user.PlatformUserMessagePacket;
import pl.rosehc.controller.sector.Sector;
import pl.rosehc.controller.wrapper.guild.GuildMemberSerializableWrapper;
import pl.rosehc.controller.wrapper.guild.GuildSerializableWrapper;
import pl.rosehc.controller.wrapper.guild.GuildTypeWrapper;

public final class Guild {

  private final String name, tag;
  private final Map<UUID, GuildGroup> guildGroupMap;
  private final GuildMember[] guildMembers;
  private final GuildType guildType;
  private final GuildRegion guildRegion;
  private final Sector creationSector;
  private final long creationTime;

  private volatile LinkedList<GuildRegenerationBlockState> regenerationBlockStateList;
  private Map<UUID, Long> memberInviteMap;
  private Map<UUID, GuildPlayerHelpInfo> guildPlayerHelpInfoMap, allyPlayerHelpInfoMap;
  private Entry<String, Long> allyInviteEntry;
  private Guild alliedGuild;
  private String homeLocation;
  private String joinAlertMessage;
  private long validityTime, protectionTime;
  private boolean pvpGuild, pvpAlly;
  private int lives, health, leaderMemberArrayPosition, pistonsOnGuild;

  public Guild(final ResultSet result) throws SQLException {
    final GuildType guildType = GuildType.valueOf(result.getString("guildType"));
    this.name = result.getString("name");
    this.tag = result.getString("tag");
    this.guildGroupMap = GuildHelper.deserializeGuildGroups(
        (String[]) result.getArray("groups").getArray());
    this.guildMembers = GuildHelper.deserializeGuildMembers(
        (String[]) result.getArray("members").getArray(), this.guildGroupMap, guildType.getSize());
    for (int slot = 0; slot < this.guildMembers.length; slot++) {
      GuildMember member = this.guildMembers[slot];
      if (member != null) {
        member.getUser().setMemberArrayPosition(slot);
        member.getUser().setGuild(this);
      }
    }

    final String creationSector = result.getString("creationSector");
    final Array regenerationBlocksArray = result.getArray("regenerationBlocks");
    this.guildType = guildType;
    this.guildRegion = new GuildRegion(Objects.requireNonNull(result.getString("centerLocation")),
        result.getInt("regionSize"));
    this.creationSector = MasterController.getInstance().getSectorFactory()
        .findSector(creationSector).orElseThrow(() -> new UnsupportedOperationException(
            "Sektor o nazwie " + creationSector + " nie istnieje!"));
    this.regenerationBlockStateList =
        regenerationBlocksArray != null ? GuildHelper.deserializeRegenerationBlocks(
            (String[]) regenerationBlocksArray.getArray()) : null;
    this.homeLocation = result.getString("homeLocation");
    this.joinAlertMessage = result.getString("joinAlertMessage");
    this.creationTime = result.getLong("creationTime");
    this.validityTime = result.getLong("validityTime");
    this.protectionTime = result.getLong("protectionTime");
    this.pvpGuild = result.getBoolean("pvpGuild");
    this.pvpAlly = result.getBoolean("pvpAlly");
    this.lives = result.getInt("lives");
    this.health = result.getInt("health");
    this.pistonsOnGuild = result.getInt("pistonsOnGuild");
    this.updateLeaderMemberPosition();
  }

  public Guild(final String name, final String tag, final Map<UUID, GuildGroup> guildGroupMap,
      final GuildMember leader, final GuildType guildType, final GuildRegion guildRegion,
      final Sector creationSector, final String homeLocation, final long validityTime,
      final long protectionTime, final int lives) {
    this.name = name;
    this.tag = tag;
    this.guildGroupMap = guildGroupMap;
    this.guildMembers = new GuildMember[guildType.getSize()];
    this.guildMembers[0] = leader;
    this.guildMembers[0].getUser().setMemberArrayPosition(0);
    this.guildMembers[0].getUser().setGuild(this);
    this.guildType = guildType;
    this.guildRegion = guildRegion;
    this.homeLocation = homeLocation;
    this.creationSector = creationSector;
    this.creationTime = System.currentTimeMillis();
    this.validityTime = validityTime;
    this.protectionTime = protectionTime;
    this.pvpGuild = true;
    this.pvpAlly = true;
    this.lives = lives;
    this.health = MasterController.getInstance().getConfigurationFactory()
        .findConfiguration(GuildsConfiguration.class).pluginWrapper.guildStartHealth;
  }

  public GuildSerializableWrapper wrap(final String sectorName) {
    final GuildMemberSerializableWrapper[] serializedMembers = new GuildMemberSerializableWrapper[this.guildMembers.length];
    for (int index = 0; index < this.guildMembers.length; index++) {
      serializedMembers[index] =
          this.guildMembers[index] != null ? this.guildMembers[index].wrap() : null;
    }

    return new GuildSerializableWrapper(this.name, this.tag,
        this.alliedGuild != null ? this.alliedGuild.getTag() : null,
        this.guildGroupMap.entrySet().stream()
            .map(entry -> new SimpleEntry<>(entry.getKey(), entry.getValue().wrap()))
            .collect(Collectors.toMap(Entry::getKey, Entry::getValue)), serializedMembers,
        GuildTypeWrapper.fromOriginal(this.guildType), this.guildRegion.wrap(),
        this.memberInviteMap,
        this.guildPlayerHelpInfoMap != null ? this.guildPlayerHelpInfoMap.entrySet().stream()
            .map(entry -> new SimpleEntry<>(entry.getKey(), entry.getValue().wrap()))
            .collect(Collectors.toMap(Entry::getKey, Entry::getValue)) : null,
        this.allyPlayerHelpInfoMap != null ? this.allyPlayerHelpInfoMap.entrySet().stream()
            .map(entry -> new SimpleEntry<>(entry.getKey(), entry.getValue().wrap()))
            .collect(Collectors.toMap(Entry::getKey, Entry::getValue)) : null, this.allyInviteEntry,
        this.regenerationBlockStateList != null && this.creationSector.getName().equals(sectorName)
            ? this.regenerationBlockStateList.stream().map(GuildRegenerationBlockState::wrap)
            .collect(Collectors.toList()) : null, this.creationSector.getName(), this.homeLocation,
        this.joinAlertMessage, this.creationTime, this.validityTime, this.protectionTime,
        this.pvpGuild, this.pvpAlly, this.lives, this.health, this.pistonsOnGuild);
  }

  public String getName() {
    return this.name;
  }

  public String getTag() {
    return this.tag;
  }

  public Map<UUID, GuildGroup> getGuildGroupMap() {
    return this.guildGroupMap;
  }

  public Optional<GuildGroup> findGuildGroup(final UUID uniqueId) {
    return Optional.ofNullable(this.guildGroupMap.get(uniqueId));
  }

  public GuildGroup getDefaultGroup() {
    final GuildGroup defaultGuildGroup = MasterController.getInstance().getGuildGroupFactory()
        .getDefaultGuildGroup();
    return Optional.ofNullable(this.guildGroupMap.get(defaultGuildGroup.getUniqueId())).orElseThrow(
        () -> new UnsupportedOperationException(
            "Domyślna grupa w gildii " + this.tag + " nie istnieje!"));
  }

  public GuildGroup getLeaderGroup() {
    final GuildGroup leaderGroup = MasterController.getInstance().getGuildGroupFactory()
        .getLeaderGuildGroup();
    return Optional.ofNullable(this.guildGroupMap.get(leaderGroup.getUniqueId())).orElseThrow(
        () -> new UnsupportedOperationException(
            "Domyślna grupa lidera w gildii " + this.tag + " nie istnieje!"));
  }

  public GuildGroup getDeputyGroup() {
    final GuildGroup deputyGroup = MasterController.getInstance().getGuildGroupFactory()
        .getDeputyGuildGroup();
    return Optional.ofNullable(this.guildGroupMap.get(deputyGroup.getUniqueId())).orElseThrow(
        () -> new UnsupportedOperationException(
            "Domyślna grupa zastępcy w gildii " + this.tag + " nie istnieje!"));
  }

  public GuildMember[] getGuildMembers() {
    return this.guildMembers;
  }

  public GuildMember getGuildMember(final GuildUser user) {
    return this.guildMembers[user.getMemberArrayPosition()];
  }

  public GuildMember getLeaderMember() {
    return this.guildMembers[this.leaderMemberArrayPosition];
  }

  public boolean addGuildMember(final GuildMember member) {
    int freeSlot = -1;
    for (final GuildMember targetMember : this.guildMembers) {
      if (targetMember != null && targetMember.getUniqueId().equals(member.getUniqueId())) {
        return false;
      }
    }

    for (int slot = 0; slot < this.guildMembers.length; slot++) {
      if (Objects.isNull(this.guildMembers[slot])) {
        freeSlot = slot;
        break;
      }
    }

    if (freeSlot != -1) {
      this.guildMembers[freeSlot] = member;
      this.guildMembers[freeSlot].getUser().setMemberArrayPosition(freeSlot);
      this.guildMembers[freeSlot].getUser().setGuild(this);
      return true;
    }

    return false;
  }

  public int getCurrentMembersSize() {
    int size = 0;
    for (final GuildMember member : this.guildMembers) {
      if (member != null) {
        size++;
      }
    }

    return size;
  }

  public boolean isFull() {
    return this.getCurrentMembersSize() >= this.guildMembers.length;
  }

  public void removeGuildMember(final GuildUser member) {
    final int memberArrayPosition = member.getMemberArrayPosition();
    if (memberArrayPosition != -1) {
      this.guildMembers[memberArrayPosition].getUser().setGuild(null);
      this.guildMembers[memberArrayPosition].getUser().setMemberArrayPosition(-1);
      this.guildMembers[memberArrayPosition] = null;
    }
  }

  public void updateLeaderMemberPosition() {
    for (int position = 0; position < this.guildMembers.length; position++) {
      final GuildMember member = this.guildMembers[position];
      if (member != null && member.isLeader()) {
        this.leaderMemberArrayPosition = position;
        break;
      }
    }
  }

  public GuildType getGuildType() {
    return this.guildType;
  }

  public GuildRegion getGuildRegion() {
    return this.guildRegion;
  }

  public String getHomeLocation() {
    return this.homeLocation;
  }

  public void setHomeLocation(final String homeLocation) {
    this.homeLocation = homeLocation;
  }

  public String getJoinAlertMessage() {
    return this.joinAlertMessage;
  }

  public void setJoinAlertMessage(final String joinAlertMessage) {
    this.joinAlertMessage = joinAlertMessage;
  }

  public void broadcastChatMessage(final String message) {
    final List<UUID> uniqueIdList = new ArrayList<>();
    for (final GuildMember member : this.guildMembers) {
      if (member != null && MasterController.getInstance().getSectorUserFactory()
          .findUserByUniqueId(member.getUniqueId()).isPresent()) {
        uniqueIdList.add(member.getUniqueId());
      }
    }

    if (!uniqueIdList.isEmpty()) {
      MasterController.getInstance().getRedisAdapter()
          .sendPacket(new PlatformUserMessagePacket(uniqueIdList, message), "rhc_platform");
    }
  }

  public boolean isMemberInvited(final GuildUser user) {
    if (this.memberInviteMap == null) {
      return false;
    }

    return this.memberInviteMap.containsKey(user.getUniqueId())
        && this.memberInviteMap.get(user.getUniqueId()) > System.currentTimeMillis();
  }

  public void addMemberInvite(final GuildUser user, final long time) {
    if (this.memberInviteMap == null) {
      this.memberInviteMap = new ConcurrentHashMap<>();
    }

    this.memberInviteMap.put(user.getUniqueId(), time);
  }

  public void removeMemberInvite(final GuildUser user) {
    if (this.memberInviteMap != null) {
      this.memberInviteMap.remove(user.getUniqueId());
    }
  }

  public Optional<GuildPlayerHelpInfo> findGuildPlayerHelpInfo(final UUID uniqueId) {
    return this.guildPlayerHelpInfoMap != null ? Optional.ofNullable(
        this.guildPlayerHelpInfoMap.get(uniqueId)) : Optional.empty();
  }

  public void addGuildPlayerHelpInfo(final UUID uniqueId, final GuildPlayerHelpInfo info) {
    if (this.guildPlayerHelpInfoMap == null) {
      this.guildPlayerHelpInfoMap = new ConcurrentHashMap<>();
    }

    this.guildPlayerHelpInfoMap.put(uniqueId, info);
  }

  public void removeGuildPlayerHelpInfo(final UUID uniqueId) {
    if (this.guildPlayerHelpInfoMap != null) {
      this.guildPlayerHelpInfoMap.remove(uniqueId);
    }
  }

  public Optional<GuildPlayerHelpInfo> findGuildAllyPlayerHelpInfo(final UUID uniqueId) {
    return this.allyPlayerHelpInfoMap != null ? Optional.ofNullable(
        this.allyPlayerHelpInfoMap.get(uniqueId)) : Optional.empty();
  }

  public void addGuildAllyPlayerHelpInfo(final UUID uniqueId, final GuildPlayerHelpInfo info) {
    if (this.allyPlayerHelpInfoMap == null) {
      this.allyPlayerHelpInfoMap = new ConcurrentHashMap<>();
    }

    this.allyPlayerHelpInfoMap.put(uniqueId, info);
  }

  public void removeGuildAllyPlayerHelpInfo(final UUID uniqueId) {
    if (this.allyPlayerHelpInfoMap != null) {
      this.allyPlayerHelpInfoMap.remove(uniqueId);
    }
  }

  public synchronized LinkedList<GuildRegenerationBlockState> getRegenerationBlockStateList() {
    return this.regenerationBlockStateList;
  }

  public synchronized void addRegenerationBlocks(
      final Collection<GuildRegenerationBlockState> blocks) {
    if (Objects.isNull(this.regenerationBlockStateList)) {
      this.regenerationBlockStateList = new LinkedList<>();
    }

    this.regenerationBlockStateList.addAll(blocks);
  }

  public Entry<String, Long> getAllyInviteEntry() {
    return this.allyInviteEntry;
  }

  public void setAllyInviteEntry(final Entry<String, Long> allyInviteEntry) {
    this.allyInviteEntry = allyInviteEntry;
  }

  public Guild getAlliedGuild() {
    return this.alliedGuild;
  }

  public void setAlliedGuild(final Guild alliedGuild) {
    this.alliedGuild = alliedGuild;
  }

  public Sector getCreationSector() {
    return this.creationSector;
  }

  public long getCreationTime() {
    return this.creationTime;
  }

  public long getValidityTime() {
    return this.validityTime;
  }

  public void setValidityTime(final long validityTime) {
    this.validityTime = validityTime;
  }

  public long getProtectionTime() {
    return this.protectionTime;
  }

  public void setProtectionTime(final long protectionTime) {
    this.protectionTime = protectionTime;
  }

  public boolean isPvpGuild() {
    return this.pvpGuild;
  }

  public void setPvpGuild(final boolean pvpGuild) {
    this.pvpGuild = pvpGuild;
  }

  public boolean isPvpAlly() {
    return this.pvpAlly;
  }

  public void setPvpAlly(final boolean pvpAlly) {
    this.pvpAlly = pvpAlly;
  }

  public int getLives() {
    return this.lives;
  }

  public void setLives(final int lives) {
    this.lives = lives;
  }

  public int getHealth() {
    return this.health;
  }

  public void setHealth(final int health) {
    this.health = health;
  }

  public int getPistonsOnGuild() {
    return this.pistonsOnGuild;
  }

  public void setPistonsOnGuild(final int pistonsOnGuild) {
    this.pistonsOnGuild = pistonsOnGuild;
  }
}
