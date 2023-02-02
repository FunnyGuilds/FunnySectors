package pl.rosehc.controller.wrapper.platform;

public final class PlatformBanSerializableWrapper {

  private String playerNickname, staffNickname, reason, ip;
  private byte[] computerUid;
  private long creationTime, leftTime;

  private PlatformBanSerializableWrapper() {
  }

  public PlatformBanSerializableWrapper(final String playerNickname, final String staffNickname,
      final String reason, final String ip, final byte[] computerUid, final long creationTime,
      final long leftTime) {
    this.playerNickname = playerNickname;
    this.staffNickname = staffNickname;
    this.reason = reason;
    this.ip = ip;
    this.computerUid = computerUid;
    this.creationTime = creationTime;
    this.leftTime = leftTime;
  }

  public String getPlayerNickname() {
    return this.playerNickname;
  }

  public String getStaffNickname() {
    return this.staffNickname;
  }

  public String getReason() {
    return this.reason;
  }

  public String getIp() {
    return this.ip;
  }

  public byte[] getComputerUid() {
    return this.computerUid;
  }

  public long getCreationTime() {
    return this.creationTime;
  }

  public long getLeftTime() {
    return this.leftTime;
  }
}
