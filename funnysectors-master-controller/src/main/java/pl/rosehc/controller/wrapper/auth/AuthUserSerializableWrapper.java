package pl.rosehc.controller.wrapper.auth;

public final class AuthUserSerializableWrapper {

  private String nickname, password, lastIP;
  private long firstJoinTime, lastOnlineTime;
  private boolean premium, registered;

  private AuthUserSerializableWrapper() {
  }

  public AuthUserSerializableWrapper(final String nickname, final String password,
      final String lastIP, final long firstJoinTime, final long lastOnlineTime,
      final boolean premium, final boolean registered) {
    this.nickname = nickname;
    this.password = password;
    this.lastIP = lastIP;
    this.firstJoinTime = firstJoinTime;
    this.lastOnlineTime = lastOnlineTime;
    this.premium = premium;
    this.registered = registered;
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
