package pl.rosehc.controller.packet.platform.ban;

import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.PlatformPacketHandler;

public final class PlatformBanIpUpdatePacket extends Packet {

  private String playerNickname;
  private String ip;

  private PlatformBanIpUpdatePacket() {
  }

  public PlatformBanIpUpdatePacket(final String playerNickname, final String ip) {
    this.playerNickname = playerNickname;
    this.ip = ip;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((PlatformPacketHandler) handler).handle(this);
  }

  public String getPlayerNickname() {
    return this.playerNickname;
  }

  public String getIp() {
    return this.ip;
  }
}
