package pl.rosehc.controller.packet.platform.ban;

import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.PlatformPacketHandler;

public final class PlatformBanCreatePacket extends Packet {

  private String playerNickname, staffNickname, reason, ip;
  private byte[] computerUid;
  private long creationTime, leftTime;

  private PlatformBanCreatePacket() {
  }

  public PlatformBanCreatePacket(final String playerNickname, final String staffNickname,
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

  @Override
  public void handle(final PacketHandler handler) {
    ((PlatformPacketHandler) handler).handle(this);
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
