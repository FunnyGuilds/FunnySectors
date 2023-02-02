package pl.rosehc.controller.packet.auth.user;

import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.AuthPacketHandler;

public final class AuthUserCreatePacket extends Packet {

  private String nickname;
  private String password;
  private String lastIP;
  private long firstJoinTime, lastOnlineTime;
  private boolean premium, registered;

  private AuthUserCreatePacket() {
  }

  public AuthUserCreatePacket(final String nickname, final String password, final String lastIP,
      final long firstJoinTime, final long lastOnlineTime, final boolean premium,
      final boolean registered) {
    this.nickname = nickname;
    this.password = password;
    this.lastIP = lastIP;
    this.firstJoinTime = firstJoinTime;
    this.lastOnlineTime = lastOnlineTime;
    this.premium = premium;
    this.registered = registered;
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

  public String getLastIP() {
    return this.lastIP;
  }

  public long getFirstJoinTime() {
    return this.firstJoinTime;
  }

  public long getLastOnlineTime() {
    return this.lastOnlineTime;
  }

  public boolean isPremium() {
    return this.premium;
  }

  public boolean isRegistered() {
    return this.registered;
  }
}
