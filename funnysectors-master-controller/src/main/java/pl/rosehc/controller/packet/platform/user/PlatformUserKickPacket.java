package pl.rosehc.controller.packet.platform.user;

import java.util.UUID;
import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;

public final class PlatformUserKickPacket extends Packet {

  private UUID uniqueId;
  private String kickMessage;

  private PlatformUserKickPacket() {
  }

  public PlatformUserKickPacket(final UUID uniqueId, final String kickMessage) {
    this.uniqueId = uniqueId;
    this.kickMessage = kickMessage;
  }

  @Override
  public void handle(final PacketHandler ignored) {
  }

  public UUID getUniqueId() {
    return this.uniqueId;
  }

  public String getKickMessage() {
    return this.kickMessage;
  }
}
