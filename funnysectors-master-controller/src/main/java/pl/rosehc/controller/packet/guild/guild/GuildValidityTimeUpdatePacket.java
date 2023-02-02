package pl.rosehc.controller.packet.guild.guild;

import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.GuildPacketHandler;

public final class GuildValidityTimeUpdatePacket extends Packet {

  private String guildTag;
  private long validityTime;

  private GuildValidityTimeUpdatePacket() {
  }

  public GuildValidityTimeUpdatePacket(final String guildTag, final long validityTime) {
    this.guildTag = guildTag;
    this.validityTime = validityTime;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((GuildPacketHandler) handler).handle(this);
  }

  public String getGuildTag() {
    return this.guildTag;
  }

  public long getValidityTime() {
    return this.validityTime;
  }
}
