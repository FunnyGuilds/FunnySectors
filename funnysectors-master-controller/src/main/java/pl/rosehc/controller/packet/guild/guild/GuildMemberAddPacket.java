package pl.rosehc.controller.packet.guild.guild;

import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.GuildPacketHandler;
import pl.rosehc.controller.wrapper.guild.GuildMemberSerializableWrapper;

public final class GuildMemberAddPacket extends Packet {

  private String guildTag;
  private GuildMemberSerializableWrapper guildMemberWrapper;

  private GuildMemberAddPacket() {
  }

  public GuildMemberAddPacket(final String guildTag,
      final GuildMemberSerializableWrapper guildMemberWrapper) {
    this.guildTag = guildTag;
    this.guildMemberWrapper = guildMemberWrapper;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((GuildPacketHandler) handler).handle(this);
  }

  public String getGuildTag() {
    return this.guildTag;
  }

  public GuildMemberSerializableWrapper getGuildMemberWrapper() {
    return this.guildMemberWrapper;
  }
}
