package pl.rosehc.controller.packet.sector.user;

import java.util.UUID;
import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.SectorPacketHandler;

public final class SectorUserCreatePacket extends Packet {

  private UUID uniqueId;
  private String nickname, sectorName;
  private int proxyIdentifier;

  private SectorUserCreatePacket() {
  }

  public SectorUserCreatePacket(final UUID uniqueId, final String nickname, final String sectorName,
      final int proxyIdentifier) {
    this.uniqueId = uniqueId;
    this.nickname = nickname;
    this.sectorName = sectorName;
    this.proxyIdentifier = proxyIdentifier;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((SectorPacketHandler) handler).handle(this);
  }

  public UUID getUniqueId() {
    return this.uniqueId;
  }

  public String getNickname() {
    return this.nickname;
  }

  public String getSectorName() {
    return this.sectorName;
  }

  public int getProxyIdentifier() {
    return this.proxyIdentifier;
  }
}
