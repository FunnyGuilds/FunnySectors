package pl.rosehc.controller.packet.platform.safe;

import java.util.UUID;
import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.PlatformPacketHandler;

public final class PlatformSafeModifierUpdatePacket extends Packet {

  private UUID uniqueId, modifierUniqueId;

  private PlatformSafeModifierUpdatePacket() {
  }

  public PlatformSafeModifierUpdatePacket(final UUID uniqueId, final UUID modifierUniqueId) {
    this.uniqueId = uniqueId;
    this.modifierUniqueId = modifierUniqueId;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((PlatformPacketHandler) handler).handle(this);
  }

  public UUID getUniqueId() {
    return this.uniqueId;
  }

  public UUID getModifierUniqueId() {
    return this.modifierUniqueId;
  }
}
