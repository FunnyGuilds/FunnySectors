package pl.rosehc.controller.packet.guild.guild;

import java.util.UUID;
import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.GuildPacketHandler;

public final class GuildMemberRemovePacket extends Packet {

  private String guildTag;
  private UUID uniqueId;

  private GuildMemberRemovePacket() {
  }

  public GuildMemberRemovePacket(final String guildTag, final UUID uniqueId) {
    this.guildTag = guildTag;
    this.uniqueId = uniqueId;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((GuildPacketHandler) handler).handle(this);
  }

  public String getGuildTag() {
    return this.guildTag;
  }

  public UUID getUniqueId() {
    return this.uniqueId;
  }
}
