package pl.rosehc.controller.packet.auth.user;

import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.AuthPacketHandler;

public final class AuthUserSetPremiumStatePacket extends Packet {

  private String nickname;
  private boolean state;

  private AuthUserSetPremiumStatePacket() {
  }

  public AuthUserSetPremiumStatePacket(final String nickname, final boolean state) {
    this.nickname = nickname;
    this.state = state;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((AuthPacketHandler) handler).handle(this);
  }

  public String getNickname() {
    return this.nickname;
  }

  public boolean getState() {
    return this.state;
  }
}
