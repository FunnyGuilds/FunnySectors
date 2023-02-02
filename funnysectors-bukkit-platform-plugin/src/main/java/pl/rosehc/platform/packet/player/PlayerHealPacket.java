package pl.rosehc.platform.packet.player;

import java.util.UUID;
import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.platform.packet.PlayerPacketHandler;

@SuppressWarnings("SpellCheckingInspection")
public final class PlayerHealPacket extends Packet {

  private UUID uniqueId;

  private PlayerHealPacket() {
  }

  public PlayerHealPacket(final UUID uniqueId) {
    this.uniqueId = uniqueId;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((PlayerPacketHandler) handler).handle(this);
  }

  public UUID getUniqueId() {
    return this.uniqueId;
  }
}
