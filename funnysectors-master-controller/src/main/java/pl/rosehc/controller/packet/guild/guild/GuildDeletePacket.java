package pl.rosehc.controller.packet.guild.guild;

import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.GuildPacketHandler;

public final class GuildDeletePacket extends Packet {

  private String tag;

  private GuildDeletePacket() {
  }

  public GuildDeletePacket(final String tag) {
    this.tag = tag;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((GuildPacketHandler) handler).handle(this);
  }

  public String getTag() {
    return this.tag;
  }
}
