package pl.rosehc.controller.packet.platform;

import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.PlatformPacketHandler;
import pl.rosehc.controller.wrapper.global.LocationWrapper;

public final class PlatformSetSpawnPacket extends Packet {

  private LocationWrapper spawnLocationWrapper;

  private PlatformSetSpawnPacket() {
  }

  public PlatformSetSpawnPacket(final LocationWrapper spawnLocationWrapper) {
    this.spawnLocationWrapper = spawnLocationWrapper;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((PlatformPacketHandler) handler).handle(this);
  }

  public LocationWrapper getSpawnLocationWrapper() {
    return this.spawnLocationWrapper;
  }
}
