package pl.rosehc.controller.auth;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import pl.rosehc.controller.wrapper.auth.AuthUserSerializableWrapper;

public final class AuthUser {

  private final String nickname;
  private final long firstJoinTime;

  private String password, lastIP;
  private boolean premium, registered, logged;
  private long lastOnlineTime;

  public AuthUser(final ResultSet result) throws SQLException {
    this.nickname = result.getString("nickname");
    this.password = result.getString("password");
    this.lastIP = result.getString("lastIP");
    this.firstJoinTime = result.getLong("firstJoinTime");
    this.lastOnlineTime = result.getLong("lastOnlineTime");
    this.premium = result.getBoolean("premium");
    this.registered = result.getBoolean("registered");
  }

  public AuthUser(final String nickname, final String lastIP, final long firstJoinTime,
      final long lastOnlineTime, final boolean premium) {
    this.nickname = nickname;
    this.lastIP = lastIP;
    this.premium = premium;
    this.firstJoinTime = firstJoinTime;
    this.lastOnlineTime = lastOnlineTime;
    this.registered = premium;
  }

  public AuthUserSerializableWrapper wrap() {
    return new AuthUserSerializableWrapper(this.nickname, this.password, this.lastIP,
        this.firstJoinTime, this.lastOnlineTime, this.premium, this.registered);
  }

  public String getNickname() {
    return this.nickname;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(final String password) {
    this.password = password;
  }

  public String getLastIP() {
    return lastIP;
  }

  public void setLastIP(final String lastIP) {
    this.lastIP = lastIP;
  }

  public boolean isPremium() {
    return this.premium;
  }

  public void setPremium(final boolean premium) {
    this.premium = premium;
  }

  public boolean isRegistered() {
    return this.registered;
  }

  public void setRegistered(final boolean registered) {
    this.registered = registered;
  }

  public boolean isLogged() {
    return this.logged;
  }

  public void setLogged(final boolean logged) {
    this.logged = logged;
  }

  public long getFirstJoinTime() {
    return this.firstJoinTime;
  }

  public long getLastOnlineTime() {
    return this.lastOnlineTime;
  }

  public void setLastOnlineTime(final long lastOnlineTime) {
    this.lastOnlineTime = lastOnlineTime;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }

    if (other == null || this.getClass() != other.getClass()) {
      return false;
    }

    AuthUser user = (AuthUser) other;
    return this.nickname.equals(user.nickname);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.nickname);
  }
}
