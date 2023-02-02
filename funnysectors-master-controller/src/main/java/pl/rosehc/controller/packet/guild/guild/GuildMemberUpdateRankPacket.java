package pl.rosehc.controller.packet.guild.guild;

import java.util.UUID;
import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.GuildPacketHandler;

public final class GuildMemberUpdateRankPacket extends Packet {

  private String guildTag;
  private UUID playerUniqueId, groupUniqueId;

  private GuildMemberUpdateRankPacket() {
  }

  public GuildMemberUpdateRankPacket(final String guildTag, final UUID playerUniqueId,
      final UUID groupUniqueId) {
    this.guildTag = guildTag;
    this.playerUniqueId = playerUniqueId;
    this.groupUniqueId = groupUniqueId;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((GuildPacketHandler) handler).handle(this);
  }

  public String getGuildTag() {
    return this.guildTag;
  }

  public UUID getPlayerUniqueId() {
    return this.playerUniqueId;
  }

  public UUID getGroupUniqueId() {
    return this.groupUniqueId;
  }
}
