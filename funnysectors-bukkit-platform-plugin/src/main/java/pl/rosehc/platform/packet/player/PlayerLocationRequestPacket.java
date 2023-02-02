package pl.rosehc.platform.packet.player;

import java.util.UUID;
import pl.rosehc.adapter.redis.callback.CallbackPacket;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.platform.packet.PlayerPacketHandler;

public final class PlayerLocationRequestPacket extends CallbackPacket {

  private UUID uniqueId;
  private String sectorName;

  private PlayerLocationRequestPacket() {
  }

  public PlayerLocationRequestPacket(final UUID uniqueId, final String sectorName) {
    this.uniqueId = uniqueId;
    this.sectorName = sectorName;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((PlayerPacketHandler) handler).handle(this);
  }

  public UUID getUniqueId() {
    return this.uniqueId;
  }

  public String getSectorName() {
    return this.sectorName;
  }
}
