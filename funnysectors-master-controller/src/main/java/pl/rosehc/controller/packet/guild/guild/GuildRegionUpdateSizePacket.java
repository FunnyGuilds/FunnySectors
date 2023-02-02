package pl.rosehc.controller.packet.guild.guild;

import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.GuildPacketHandler;

public final class GuildRegionUpdateSizePacket extends Packet {

  private String guildTag;
  private int size;

  private GuildRegionUpdateSizePacket() {
  }

  public GuildRegionUpdateSizePacket(final String guildTag, final int size) {
    this.guildTag = guildTag;
    this.size = size;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((GuildPacketHandler) handler).handle(this);
  }

  public String getGuildTag() {
    return this.guildTag;
  }

  public int getSize() {
    return this.size;
  }
}
