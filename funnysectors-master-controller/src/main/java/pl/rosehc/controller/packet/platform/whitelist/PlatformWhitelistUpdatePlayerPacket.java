package pl.rosehc.controller.packet.platform.whitelist;

import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.PlatformPacketHandler;

public final class PlatformWhitelistUpdatePlayerPacket extends Packet {

  private String playerName;
  private boolean add;

  private PlatformWhitelistUpdatePlayerPacket() {
  }

  public PlatformWhitelistUpdatePlayerPacket(final String playerName, final boolean add) {
    this.playerName = playerName;
    this.add = add;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((PlatformPacketHandler) handler).handle(this);
  }

  public String getPlayerName() {
    return this.playerName;
  }

  public boolean isAdd() {
    return this.add;
  }
}
