package pl.rosehc.controller.packet.platform.user;

import java.util.UUID;
import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.PlatformPacketHandler;

public final class PlatformUserSelectedDiscoEffectTypeUpdatePacket extends Packet {

  private UUID uniqueId;
  private String selectedDiscoEffectTypeName;

  private PlatformUserSelectedDiscoEffectTypeUpdatePacket() {
  }

  public PlatformUserSelectedDiscoEffectTypeUpdatePacket(final UUID uniqueId,
      final String selectedDiscoEffectTypeName) {
    this.uniqueId = uniqueId;
    this.selectedDiscoEffectTypeName = selectedDiscoEffectTypeName;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((PlatformPacketHandler) handler).handle(this);
  }

  public UUID getUniqueId() {
    return this.uniqueId;
  }

  public String getSelectedDiscoEffectTypeName() {
    return this.selectedDiscoEffectTypeName;
  }
}
