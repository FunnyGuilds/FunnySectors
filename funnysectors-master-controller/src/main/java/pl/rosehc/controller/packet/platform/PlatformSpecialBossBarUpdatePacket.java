package pl.rosehc.controller.packet.platform;

import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.configuration.impl.configuration.PlatformConfiguration.SpecialBossBarWrapper;
import pl.rosehc.controller.packet.PlatformPacketHandler;

public final class PlatformSpecialBossBarUpdatePacket extends Packet {

  private SpecialBossBarWrapper specialBossBarWrapper;

  private PlatformSpecialBossBarUpdatePacket() {
  }

  public PlatformSpecialBossBarUpdatePacket(final SpecialBossBarWrapper specialBossBarWrapper) {
    this.specialBossBarWrapper = specialBossBarWrapper;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((PlatformPacketHandler) handler).handle(this);
  }

  public SpecialBossBarWrapper getSpecialBossBarWrapper() {
    return this.specialBossBarWrapper;
  }
}
