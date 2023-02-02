package pl.rosehc.controller.packet.auth.user;

import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.AuthPacketHandler;

public final class AuthUserMarkRegisteredPacket extends Packet {

  private String nickname;

  private AuthUserMarkRegisteredPacket() {
  }

  public AuthUserMarkRegisteredPacket(final String nickname) {
    this.nickname = nickname;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((AuthPacketHandler) handler).handle(this);
  }

  public String getNickname() {
    return this.nickname;
  }
}
