package pl.rosehc.controller.packet.platform.user;

import java.util.UUID;
import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.PlatformPacketHandler;

public final class PlatformUserCombatTimeUpdatePacket extends Packet {

  private UUID uniqueId;
  private long combatTime;

  private PlatformUserCombatTimeUpdatePacket() {
  }

  public PlatformUserCombatTimeUpdatePacket(final UUID uniqueId, final long combatTime) {
    this.uniqueId = uniqueId;
    this.combatTime = combatTime;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((PlatformPacketHandler) handler).handle(this);
  }

  public UUID getUniqueId() {
    return this.uniqueId;
  }

  public long getCombatTime() {
    return this.combatTime;
  }
}
