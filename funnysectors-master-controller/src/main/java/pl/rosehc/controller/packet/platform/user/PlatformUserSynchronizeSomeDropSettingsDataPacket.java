package pl.rosehc.controller.packet.platform.user;

import java.util.UUID;
import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.PlatformPacketHandler;

public final class PlatformUserSynchronizeSomeDropSettingsDataPacket extends Packet {

  private UUID uniqueId;
  private double turboDropMultiplier;
  private boolean cobbleStone;
  private long turboDropTime;
  private int currentXP, neededXP;
  private int level;

  private PlatformUserSynchronizeSomeDropSettingsDataPacket() {
  }

  public PlatformUserSynchronizeSomeDropSettingsDataPacket(final UUID uniqueId,
      final double turboDropMultiplier, final boolean cobbleStone, final long turboDropTime,
      final int currentXP, final int neededXP, final int level) {
    this.uniqueId = uniqueId;
    this.turboDropMultiplier = turboDropMultiplier;
    this.cobbleStone = cobbleStone;
    this.turboDropTime = turboDropTime;
    this.currentXP = currentXP;
    this.neededXP = neededXP;
    this.level = level;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((PlatformPacketHandler) handler).handle(this);
  }

  public UUID getUniqueId() {
    return this.uniqueId;
  }

  public double getTurboDropMultiplier() {
    return this.turboDropMultiplier;
  }

  public boolean isCobbleStone() {
    return this.cobbleStone;
  }

  public long getTurboDropTime() {
    return this.turboDropTime;
  }

  public int getCurrentXP() {
    return this.currentXP;
  }

  public int getNeededXP() {
    return this.neededXP;
  }

  public int getLevel() {
    return this.level;
  }
}
