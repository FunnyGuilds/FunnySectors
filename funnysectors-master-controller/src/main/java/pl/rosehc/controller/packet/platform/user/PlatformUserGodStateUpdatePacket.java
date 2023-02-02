package pl.rosehc.controller.packet.platform.user;

import java.util.UUID;
import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.PlatformPacketHandler;

public final class PlatformUserGodStateUpdatePacket extends Packet {

  private UUID uniqueId;
  private boolean state;

  private PlatformUserGodStateUpdatePacket() {
  }

  public PlatformUserGodStateUpdatePacket(final UUID uniqueId, final boolean state) {
    this.uniqueId = uniqueId;
    this.state = state;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((PlatformPacketHandler) handler).handle(this);
  }

  public UUID getUniqueId() {
    return this.uniqueId;
  }

  public boolean getState() {
    return this.state;
  }
}
