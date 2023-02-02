package pl.rosehc.controller.packet.platform.user;

import java.util.UUID;
import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.PlatformPacketHandler;

public final class PlatformUserDropSettingsAddDisabledDropPacket extends Packet {

  private UUID uniqueId;
  private String dropName;

  private PlatformUserDropSettingsAddDisabledDropPacket() {
  }

  public PlatformUserDropSettingsAddDisabledDropPacket(final UUID uniqueId, final String dropName) {
    this.uniqueId = uniqueId;
    this.dropName = dropName;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((PlatformPacketHandler) handler).handle(this);
  }

  public UUID getUniqueId() {
    return this.uniqueId;
  }

  public String getDropName() {
    return this.dropName;
  }
}
