package pl.rosehc.platform.packet.player;

import java.util.UUID;
import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.platform.packet.PlayerPacketHandler;

@SuppressWarnings("SpellCheckingInspection")
public final class PlayerClearInventoryPacket extends Packet {

  private UUID uniqueId;
  private boolean enderchest;

  private PlayerClearInventoryPacket() {
  }

  public PlayerClearInventoryPacket(final UUID uniqueId, final boolean enderchest) {
    this.uniqueId = uniqueId;
    this.enderchest = enderchest;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((PlayerPacketHandler) handler).handle(this);
  }

  public UUID getUniqueId() {
    return this.uniqueId;
  }

  public boolean isEnderchest() {
    return this.enderchest;
  }
}
