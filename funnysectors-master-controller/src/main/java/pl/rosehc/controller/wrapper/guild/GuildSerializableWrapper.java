package pl.rosehc.controller.wrapper.guild;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

public final class GuildSerializableWrapper {

  private String name, tag, alliedGuild;
  private Map<UUID, GuildGroupSerializableWrapper> guildGroupMap;
  private GuildMemberSerializableWrapper[] guildMembers;
  private GuildTypeWrapper guildType;
  private GuildRegionSerializableWrapper guildRegion;
  private Map<UUID, Long> memberInviteMap;
  private Map<UUID, GuildPlayerHelpInfoSerializableWrapper> guildPlayerHelpInfoMap, allyPlayerHelpInfoMap;
  private Entry<String, Long> allyInviteEntry;
  private List<GuildRegenerationBlockStateSerializationWrapper> regenerationBlockStateList;
  private String creationSectorName, homeLocation, joinAlertMessage;
  private long creationTime, validityTime, protectionTime;
  private boolean pvpGuild, pvpAlly;
  private int lives, health, pistonsOnGuild;

  private GuildSerializableWrapper() {
  }

  public GuildSerializableWrapper(final String name, final String tag, final String alliedGuild,
      final Map<UUID, GuildGroupSerializableWrapper> guildGroupMap,
      final GuildMemberSerializableWrapper[] guildMembers, final GuildTypeWrapper guildType,
      final GuildRegionSerializableWrapper guildRegion, final Map<UUID, Long> memberInviteMap,
      final Map<UUID, GuildPlayerHelpInfoSerializableWrapper> guildPlayerHelpInfoMap,
      final Map<UUID, GuildPlayerHelpInfoSerializableWrapper> allyPlayerHelpInfoMap,
      final Entry<String, Long> allyInviteEntry,
      final List<GuildRegenerationBlockStateSerializationWrapper> regenerationBlockStateList,
      final String creationSectorName, final String homeLocation, final String joinAlertMessage,
      final long creationTime, final long validityTime, final long protectionTime,
      final boolean pvpGuild, final boolean pvpAlly, final int lives, final int health,
      final int pistonsOnGuild) {
    this.name = name;
    this.tag = tag;
    this.guildGroupMap = guildGroupMap;
    this.alliedGuild = alliedGuild;
    this.guildMembers = guildMembers;
    this.allyInviteEntry = allyInviteEntry;
    this.guildType = guildType;
    this.guildRegion = guildRegion;
    this.memberInviteMap = memberInviteMap;
    this.guildPlayerHelpInfoMap = guildPlayerHelpInfoMap;
    this.allyPlayerHelpInfoMap = allyPlayerHelpInfoMap;
    this.regenerationBlockStateList = regenerationBlockStateList;
    this.creationSectorName = creationSectorName;
    this.homeLocation = homeLocation;
    this.joinAlertMessage = joinAlertMessage;
    this.creationTime = creationTime;
    this.validityTime = validityTime;
    this.protectionTime = protectionTime;
    this.pvpGuild = pvpGuild;
    this.pvpAlly = pvpAlly;
    this.lives = lives;
    this.health = health;
    this.pistonsOnGuild = pistonsOnGuild;
  }

  public String getName() {
    return this.name;
  }

  public String getTag() {
    return this.tag;
  }

  public Map<UUID, GuildGroupSerializableWrapper> getGuildGroupMap() {
    return this.guildGroupMap;
  }

  public String getAlliedGuild() {
    return this.alliedGuild;
  }

  public GuildMemberSerializableWrapper[] getGuildMembers() {
    return this.guildMembers;
  }

  public GuildTypeWrapper getGuildType() {
    return this.guildType;
  }

  public GuildRegionSerializableWrapper getGuildRegion() {
    return this.guildRegion;
  }

  public Map<UUID, Long> getMemberInviteMap() {
    return this.memberInviteMap;
  }

  public Map<UUID, GuildPlayerHelpInfoSerializableWrapper> getGuildPlayerHelpInfoMap() {
    return this.guildPlayerHelpInfoMap;
  }

  public Map<UUID, GuildPlayerHelpInfoSerializableWrapper> getAllyPlayerHelpInfoMap() {
    return this.allyPlayerHelpInfoMap;
  }

  public List<GuildRegenerationBlockStateSerializationWrapper> getRegenerationBlockStateList() {
    return this.regenerationBlockStateList;
  }

  public Entry<String, Long> getAllyInviteEntry() {
    return this.allyInviteEntry;
  }

  public String getCreationSectorName() {
    return this.creationSectorName;
  }

  public String getHomeLocation() {
    return this.homeLocation;
  }

  public String getJoinAlertMessage() {
    return this.joinAlertMessage;
  }

  public long getCreationTime() {
    return this.creationTime;
  }

  public long getValidityTime() {
    return this.validityTime;
  }

  public long getProtectionTime() {
    return this.protectionTime;
  }

  public boolean isPvpGuild() {
    return this.pvpGuild;
  }

  public boolean isPvpAlly() {
    return this.pvpAlly;
  }

  public int getLives() {
    return this.lives;
  }

  public int getHealth() {
    return this.health;
  }

  public int getPistonsOnGuild() {
    return this.pistonsOnGuild;
  }
}
