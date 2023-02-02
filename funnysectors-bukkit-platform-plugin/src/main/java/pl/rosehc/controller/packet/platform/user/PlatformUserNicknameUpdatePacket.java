package pl.rosehc.controller.packet.platform.user;

import java.util.UUID;
import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.PlatformPacketHandler;

public final class PlatformUserNicknameUpdatePacket extends Packet {

  private UUID uniqueId;
  private String nickname;

  private PlatformUserNicknameUpdatePacket() {
  }

  public PlatformUserNicknameUpdatePacket(UUID uniqueId, String nickname) {
    this.uniqueId = uniqueId;
    this.nickname = nickname;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((PlatformPacketHandler) handler).handle(this);
  }

  public UUID getUniqueId() {
    return this.uniqueId;
  }

  public String getNickname() {
    return this.nickname;
  }
}
