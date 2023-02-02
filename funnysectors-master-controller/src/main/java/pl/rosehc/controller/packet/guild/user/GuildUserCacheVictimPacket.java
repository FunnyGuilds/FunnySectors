package pl.rosehc.controller.packet.guild.user;

import java.util.UUID;
import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.GuildPacketHandler;

public final class GuildUserCacheVictimPacket extends Packet {

  private UUID attackerUniqueId, victimUniqueId;
  private long time;

  private GuildUserCacheVictimPacket() {
  }

  public GuildUserCacheVictimPacket(final UUID attackerUniqueId, final UUID victimUniqueId,
      final long time) {
    this.attackerUniqueId = attackerUniqueId;
    this.victimUniqueId = victimUniqueId;
    this.time = time;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((GuildPacketHandler) handler).handle(this);
  }

  public UUID getAttackerUniqueId() {
    return this.attackerUniqueId;
  }

  public UUID getVictimUniqueId() {
    return this.victimUniqueId;
  }

  public long getTime() {
    return this.time;
  }
}
