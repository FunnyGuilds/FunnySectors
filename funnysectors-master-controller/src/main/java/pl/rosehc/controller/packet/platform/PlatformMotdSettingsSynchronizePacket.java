package pl.rosehc.controller.packet.platform;

import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.PlatformPacketHandler;

public final class PlatformMotdSettingsSynchronizePacket extends Packet {

  private String firstLine;
  private String secondLine;
  private String thirdLine;
  private int thirdLineSpacing;

  private PlatformMotdSettingsSynchronizePacket() {
  }

  public PlatformMotdSettingsSynchronizePacket(final String firstLine, final String secondLine,
      final String thirdLine, final int thirdLineSpacing) {
    this.firstLine = firstLine;
    this.secondLine = secondLine;
    this.thirdLine = thirdLine;
    this.thirdLineSpacing = thirdLineSpacing;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((PlatformPacketHandler) handler).handle(this);
  }

  public String getFirstLine() {
    return this.firstLine;
  }

  public String getSecondLine() {
    return this.secondLine;
  }

  public String getThirdLine() {
    return this.thirdLine;
  }

  public int getThirdLineSpacing() {
    return this.thirdLineSpacing;
  }
}
