package pl.rosehc.controller.packet.guild.user;

import java.util.UUID;
import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.GuildPacketHandler;

public final class GuildUserCacheFighterPacket extends Packet {

  private UUID targetUniqueId, fighterUniqueId;
  private long fightTime;

  private GuildUserCacheFighterPacket() {
  }

  public GuildUserCacheFighterPacket(final UUID targetUniqueId, final UUID fighterUniqueId,
      final long fightTime) {
    this.targetUniqueId = targetUniqueId;
    this.fighterUniqueId = fighterUniqueId;
    this.fightTime = fightTime;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((GuildPacketHandler) handler).handle(this);
  }

  public UUID getTargetUniqueId() {
    return this.targetUniqueId;
  }

  public UUID getFighterUniqueId() {
    return this.fighterUniqueId;
  }

  public long getFightTime() {
    return this.fightTime;
  }
}
