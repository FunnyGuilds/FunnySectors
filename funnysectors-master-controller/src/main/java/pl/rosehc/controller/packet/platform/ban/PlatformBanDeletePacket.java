package pl.rosehc.controller.packet.platform.ban;

import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.PlatformPacketHandler;

public final class PlatformBanDeletePacket extends Packet {

  private String playerNickname;

  private PlatformBanDeletePacket() {
  }

  public PlatformBanDeletePacket(final String playerNickname) {
    this.playerNickname = playerNickname;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((PlatformPacketHandler) handler).handle(this);
  }

  public String getPlayerNickname() {
    return this.playerNickname;
  }
}
