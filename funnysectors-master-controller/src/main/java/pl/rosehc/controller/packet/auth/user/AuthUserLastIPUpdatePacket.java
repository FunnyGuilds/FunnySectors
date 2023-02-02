package pl.rosehc.controller.packet.auth.user;

import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.AuthPacketHandler;

public final class AuthUserLastIPUpdatePacket extends Packet {

  private String nickname;
  private String lastIp;

  private AuthUserLastIPUpdatePacket() {
  }

  public AuthUserLastIPUpdatePacket(final String nickname, final String lastIp) {
    this.nickname = nickname;
    this.lastIp = lastIp;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((AuthPacketHandler) handler).handle(this);
  }

  public String getNickname() {
    return this.nickname;
  }

  public String getLastIp() {
    return this.lastIp;
  }
}
