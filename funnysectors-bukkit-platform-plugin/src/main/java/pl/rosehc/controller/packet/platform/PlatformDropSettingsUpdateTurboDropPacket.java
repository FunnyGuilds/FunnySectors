package pl.rosehc.controller.packet.platform;

import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.PlatformPacketHandler;

public final class PlatformDropSettingsUpdateTurboDropPacket extends Packet {

  private double turboDropMultiplier;
  private long turboDropTime;

  private PlatformDropSettingsUpdateTurboDropPacket() {
  }

  public PlatformDropSettingsUpdateTurboDropPacket(final double turboDropMultiplier,
      final long turboDropTime) {
    this.turboDropMultiplier = turboDropMultiplier;
    this.turboDropTime = turboDropTime;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((PlatformPacketHandler) handler).handle(this);
  }

  public double getTurboDropMultiplier() {
    return this.turboDropMultiplier;
  }

  public long getTurboDropTime() {
    return this.turboDropTime;
  }
}
