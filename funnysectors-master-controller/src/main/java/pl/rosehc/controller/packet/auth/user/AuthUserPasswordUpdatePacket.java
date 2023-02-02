package pl.rosehc.controller.packet.auth.user;

import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.AuthPacketHandler;

public final class AuthUserPasswordUpdatePacket extends Packet {

  private String nickname;
  private String password;

  private AuthUserPasswordUpdatePacket() {
  }

  public AuthUserPasswordUpdatePacket(final String nickname, final String password) {
    this.nickname = nickname;
    this.password = password;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((AuthPacketHandler) handler).handle(this);
  }

  public String getNickname() {
    return this.nickname;
  }

  public String getPassword() {
    return this.password;
  }
}
