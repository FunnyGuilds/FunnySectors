package pl.rosehc.controller.packet.platform.safe.request;

import java.util.UUID;
import pl.rosehc.adapter.redis.callback.CallbackPacket;
import pl.rosehc.adapter.redis.packet.PacketHandler;

public final class PlatformSafeModificationRequestPacket extends CallbackPacket {

  private UUID uniqueId, modifierUniqueId;
  private String sectorName;

  private PlatformSafeModificationRequestPacket() {
  }

  public PlatformSafeModificationRequestPacket(final UUID uniqueId, final UUID modifierUniqueId,
      final String sectorName) {
    this.uniqueId = uniqueId;
    this.modifierUniqueId = modifierUniqueId;
    this.sectorName = sectorName;
  }

  @Override
  public void handle(final PacketHandler ignored) {
  }

  public UUID getUniqueId() {
    return this.uniqueId;
  }

  public UUID getModifierUniqueId() {
    return this.modifierUniqueId;
  }

  public String getSectorName() {
    return this.sectorName;
  }
}
