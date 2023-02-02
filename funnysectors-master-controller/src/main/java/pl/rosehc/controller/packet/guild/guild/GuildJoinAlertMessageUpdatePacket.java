package pl.rosehc.controller.packet.guild.guild;

import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.GuildPacketHandler;

public final class GuildJoinAlertMessageUpdatePacket extends Packet {

  private String guildTag, joinAlertMessage;

  private GuildJoinAlertMessageUpdatePacket() {
  }

  public GuildJoinAlertMessageUpdatePacket(final String guildTag, final String joinAlertMessage) {
    this.guildTag = guildTag;
    this.joinAlertMessage = joinAlertMessage;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((GuildPacketHandler) handler).handle(this);
  }

  public String getGuildTag() {
    return this.guildTag;
  }

  public String getJoinAlertMessage() {
    return this.joinAlertMessage;
  }
}
