package pl.rosehc.controller.packet.guild.guild;

import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.GuildPacketHandler;

public final class GuildPvPUpdatePacket extends Packet {

  private String guildTag;
  private boolean status, ally;

  private GuildPvPUpdatePacket() {
  }

  public GuildPvPUpdatePacket(final String guildTag, final boolean status, final boolean ally) {
    this.guildTag = guildTag;
    this.status = status;
    this.ally = ally;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((GuildPacketHandler) handler).handle(this);
  }

  public String getGuildTag() {
    return this.guildTag;
  }

  public boolean getStatus() {
    return this.status;
  }

  public boolean isAlly() {
    return this.ally;
  }
}
