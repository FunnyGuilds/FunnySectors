package pl.rosehc.controller.packet.auth.user;

import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.AuthPacketHandler;

public final class AuthUserLastOnlineUpdatePacket extends Packet {

  private String nickname;
  private long lastOnlineTime;

  private AuthUserLastOnlineUpdatePacket() {
  }

  public AuthUserLastOnlineUpdatePacket(final String nickname, final long lastOnlineTime) {
    this.nickname = nickname;
    this.lastOnlineTime = lastOnlineTime;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((AuthPacketHandler) handler).handle(this);
  }

  public String getNickname() {
    return this.nickname;
  }

  public long getLastOnlineTime() {
    return this.lastOnlineTime;
  }
}
