package pl.rosehc.controller.packet.platform.ban;

import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.PlatformPacketHandler;

public final class PlatformBanComputerUidUpdatePacket extends Packet {

  private String playerNickname;
  private byte[] computerUid;

  private PlatformBanComputerUidUpdatePacket() {
  }

  public PlatformBanComputerUidUpdatePacket(final String playerNickname, final byte[] computerUid) {
    this.playerNickname = playerNickname;
    this.computerUid = computerUid;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((PlatformPacketHandler) handler).handle(this);
  }

  public String getPlayerNickname() {
    return this.playerNickname;
  }

  public byte[] getComputerUid() {
    return this.computerUid;
  }
}
