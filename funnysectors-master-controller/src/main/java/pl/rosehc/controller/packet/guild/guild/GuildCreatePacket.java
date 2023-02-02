package pl.rosehc.controller.packet.guild.guild;

import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.GuildPacketHandler;
import pl.rosehc.controller.wrapper.guild.GuildMemberSerializableWrapper;
import pl.rosehc.controller.wrapper.guild.GuildRegionSerializableWrapper;
import pl.rosehc.controller.wrapper.guild.GuildTypeWrapper;

public final class GuildCreatePacket extends Packet {

  private String name, tag;
  private GuildMemberSerializableWrapper leader;
  private GuildTypeWrapper guildType;
  private GuildRegionSerializableWrapper guildRegion;
  private String creationSectorName, homeLocation;
  private long validityTime, protectionTime;
  private int lives;

  private GuildCreatePacket() {
  }

  public GuildCreatePacket(final String name, final String tag,
      final GuildMemberSerializableWrapper leader, final GuildTypeWrapper guildType,
      final GuildRegionSerializableWrapper guildRegion, final String creationSectorName,
      final String homeLocation, final long validityTime, final long protectionTime,
      final int lives) {
    this.name = name;
    this.tag = tag;
    this.leader = leader;
    this.guildType = guildType;
    this.guildRegion = guildRegion;
    this.creationSectorName = creationSectorName;
    this.homeLocation = homeLocation;
    this.validityTime = validityTime;
    this.protectionTime = protectionTime;
    this.lives = lives;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((GuildPacketHandler) handler).handle(this);
  }

  public String getName() {
    return this.name;
  }

  public String getTag() {
    return this.tag;
  }

  public GuildMemberSerializableWrapper getLeader() {
    return this.leader;
  }

  public GuildTypeWrapper getGuildType() {
    return this.guildType;
  }

  public GuildRegionSerializableWrapper getGuildRegion() {
    return this.guildRegion;
  }

  public String getCreationSectorName() {
    return this.creationSectorName;
  }

  public String getHomeLocation() {
    return this.homeLocation;
  }

  public long getValidityTime() {
    return this.validityTime;
  }

  public long getProtectionTime() {
    return this.protectionTime;
  }

  public int getLives() {
    return this.lives;
  }
}
