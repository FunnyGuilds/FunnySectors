package pl.rosehc.platform.packet.player;

import java.util.UUID;
import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.platform.packet.PlayerPacketHandler;

public final class PlayerGiveMagicCasePacket extends Packet {

  private UUID targetUniqueId;
  private int amount;

  private PlayerGiveMagicCasePacket() {
  }

  public PlayerGiveMagicCasePacket(final UUID targetUniqueId, final int amount) {
    this.targetUniqueId = targetUniqueId;
    this.amount = amount;
  }

  public PlayerGiveMagicCasePacket(final int amount) {
    this(null, amount);
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((PlayerPacketHandler) handler).handle(this);
  }

  public UUID getTargetUniqueId() {
    return this.targetUniqueId;
  }

  public int getAmount() {
    return this.amount;
  }
}
