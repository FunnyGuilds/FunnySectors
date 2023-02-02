package pl.rosehc.controller.packet.platform.user;

import java.util.UUID;
import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;

public final class PlatformUserDropSettingsRemoveDisabledDropPacket extends Packet {

  private UUID uniqueId;
  private String dropName;

  private PlatformUserDropSettingsRemoveDisabledDropPacket() {
  }

  public PlatformUserDropSettingsRemoveDisabledDropPacket(final UUID uniqueId,
      final String dropName) {
    this.uniqueId = uniqueId;
    this.dropName = dropName;
  }

  @Override
  public void handle(final PacketHandler handler) {

  }

  public UUID getUniqueId() {
    return this.uniqueId;
  }

  public String getDropName() {
    return this.dropName;
  }
}
