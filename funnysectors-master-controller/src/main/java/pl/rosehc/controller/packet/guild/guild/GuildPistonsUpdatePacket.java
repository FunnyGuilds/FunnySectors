package pl.rosehc.controller.packet.guild.guild;

import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.GuildPacketHandler;

public final class GuildPistonsUpdatePacket extends Packet {

  private String guildTag;
  private int pistons;

  private GuildPistonsUpdatePacket() {
  }

  public GuildPistonsUpdatePacket(final String guildTag, final int pistons) {
    this.guildTag = guildTag;
    this.pistons = pistons;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((GuildPacketHandler) handler).handle(this);
  }

  public String getGuildTag() {
    return this.guildTag;
  }

  public int getPistons() {
    return this.pistons;
  }
}
