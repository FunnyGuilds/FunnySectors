package pl.rosehc.controller.packet.guild.guild;

import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.GuildPacketHandler;

public final class GuildHomeLocationUpdatePacket extends Packet {

  private String guildTag, homeLocation;

  private GuildHomeLocationUpdatePacket() {
  }

  public GuildHomeLocationUpdatePacket(final String guildTag, final String homeLocation) {
    this.guildTag = guildTag;
    this.homeLocation = homeLocation;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((GuildPacketHandler) handler).handle(this);
  }

  public String getGuildTag() {
    return this.guildTag;
  }

  public String getHomeLocation() {
    return this.homeLocation;
  }
}
