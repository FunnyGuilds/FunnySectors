package pl.rosehc.controller.packet.sector;

import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.SectorPacketHandler;

public final class SectorUpdateStatisticsPacket extends Packet {

  private String sectorName;
  private double tps, load;
  private int players;

  private SectorUpdateStatisticsPacket() {
  }

  public SectorUpdateStatisticsPacket(final String sectorName, final double tps, final double load,
      final int players) {
    this.sectorName = sectorName;
    this.tps = tps;
    this.load = load;
    this.players = players;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((SectorPacketHandler) handler).handle(this);
  }

  public String getSectorName() {
    return this.sectorName;
  }

  public double getTps() {
    return this.tps;
  }

  public double getLoad() {
    return this.load;
  }

  public int getPlayers() {
    return this.players;
  }
}
