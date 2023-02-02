package pl.rosehc.controller.packet.guild.guild;

import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.GuildPacketHandler;

public final class GuildUpdateAllyPacket extends Packet {

  private String firstGuildTag, secondGuildTag;
  private boolean add;

  private GuildUpdateAllyPacket() {
  }

  public GuildUpdateAllyPacket(final String firstGuildTag, final String secondGuildTag,
      final boolean add) {
    this.firstGuildTag = firstGuildTag;
    this.secondGuildTag = secondGuildTag;
    this.add = add;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((GuildPacketHandler) handler).handle(this);
  }

  public String getFirstGuildTag() {
    return this.firstGuildTag;
  }

  public String getSecondGuildTag() {
    return this.secondGuildTag;
  }

  public boolean isAdd() {
    return this.add;
  }
}
