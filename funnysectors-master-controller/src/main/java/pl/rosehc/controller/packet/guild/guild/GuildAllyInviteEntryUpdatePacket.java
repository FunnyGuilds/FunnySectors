package pl.rosehc.controller.packet.guild.guild;

import java.util.Map.Entry;
import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.GuildPacketHandler;

public final class GuildAllyInviteEntryUpdatePacket extends Packet {

  private String guildTag;
  private Entry<String, Long> allyInviteEntry;

  private GuildAllyInviteEntryUpdatePacket() {
  }

  public GuildAllyInviteEntryUpdatePacket(final String guildTag,
      final Entry<String, Long> allyInviteEntry) {
    this.guildTag = guildTag;
    this.allyInviteEntry = allyInviteEntry;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((GuildPacketHandler) handler).handle(this);
  }

  public String getGuildTag() {
    return this.guildTag;
  }

  public Entry<String, Long> getAllyInviteEntry() {
    return this.allyInviteEntry;
  }
}
