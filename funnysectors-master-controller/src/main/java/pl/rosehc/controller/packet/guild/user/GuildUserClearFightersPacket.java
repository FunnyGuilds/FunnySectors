package pl.rosehc.controller.packet.guild.user;

import java.util.UUID;
import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.GuildPacketHandler;

public final class GuildUserClearFightersPacket extends Packet {

  private UUID uniqueId;

  private GuildUserClearFightersPacket() {
  }

  public GuildUserClearFightersPacket(final UUID uniqueId) {
    this.uniqueId = uniqueId;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((GuildPacketHandler) handler).handle(this);
  }

  public UUID getUniqueId() {
    return this.uniqueId;
  }
}
