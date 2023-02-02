package pl.rosehc.controller.packet.platform.user;

import java.util.List;
import java.util.UUID;
import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.PlatformPacketHandler;

/**
 * @author stevimeister on 31/01/2022
 **/
public final class PlatformUserMessagePacket extends Packet {

  private List<UUID> uniqueIds;
  private String message;

  private PlatformUserMessagePacket() {
  }

  public PlatformUserMessagePacket(final List<UUID> uniqueIds, final String message) {
    this.uniqueIds = uniqueIds;
    this.message = message;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((PlatformPacketHandler) handler).handle(this);
  }

  public List<UUID> getUniqueIds() {
    return this.uniqueIds;
  }

  public String getMessage() {
    return this.message;
  }
}
