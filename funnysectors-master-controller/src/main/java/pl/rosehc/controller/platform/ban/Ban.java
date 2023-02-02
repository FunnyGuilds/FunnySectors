package pl.rosehc.controller.platform.ban;

import java.sql.ResultSet;
import java.sql.SQLException;
import pl.rosehc.controller.wrapper.platform.PlatformBanSerializableWrapper;

public final class Ban {

  private final String playerNickname, staffNickname, reason;
  private final long creationTime, leftTime;

  private String ip;
  private byte[] computerUid;

  public Ban(final String playerNickname, final String staffNickname, final String ip,
      final String reason, final byte[] computerUid, final long creationTime, final long leftTime) {
    this.playerNickname = playerNickname;
    this.staffNickname = staffNickname;
    this.ip = ip;
    this.reason = reason;
    this.computerUid = computerUid;
    this.creationTime = creationTime;
    this.leftTime = leftTime;
  }

  public Ban(final ResultSet result) throws SQLException {
    this.playerNickname = result.getString("playerNickname");
    this.staffNickname = result.getString("staffNickname");
    this.ip = result.getString("ip");
    this.reason = result.getString("reason");
    this.computerUid = result.getBytes("computerUid");
    this.creationTime = result.getLong("creationTime");
    this.leftTime = result.getLong("leftTime");
  }

  public PlatformBanSerializableWrapper wrap() {
    return new PlatformBanSerializableWrapper(this.playerNickname, this.staffNickname, this.reason,
        this.ip, this.computerUid, this.creationTime, this.leftTime);
  }

  public String getPlayerNickname() {
    return this.playerNickname;
  }

  public String getStaffNickname() {
    return this.staffNickname;
  }

  public String getIp() {
    return this.ip;
  }

  public void setIp(final String ip) {
    this.ip = ip;
  }

  public String getReason() {
    return this.reason;
  }

  public byte[] getComputerUid() {
    return this.computerUid;
  }

  public void setComputerUid(final byte[] computerUid) {
    this.computerUid = computerUid;
  }

  public long getCreationTime() {
    return this.creationTime;
  }

  public long getLeftTime() {
    return this.leftTime;
  }
}
