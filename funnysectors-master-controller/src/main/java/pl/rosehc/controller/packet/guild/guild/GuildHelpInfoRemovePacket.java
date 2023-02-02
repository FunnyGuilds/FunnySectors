package pl.rosehc.controller.packet.guild.guild;

import java.util.UUID;
import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.GuildPacketHandler;

public final class GuildHelpInfoRemovePacket extends Packet {

  private String guildTag;
  private UUID uniqueId;
  private boolean isAlly;

  private GuildHelpInfoRemovePacket() {
  }

  public GuildHelpInfoRemovePacket(final String guildTag, final UUID uniqueId,
      final boolean isAlly) {
    this.guildTag = guildTag;
    this.uniqueId = uniqueId;
    this.isAlly = isAlly;
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

  public boolean isAlly() {
    return this.isAlly;
  }
}
