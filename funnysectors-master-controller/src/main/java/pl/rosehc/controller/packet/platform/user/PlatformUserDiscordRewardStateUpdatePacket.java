package pl.rosehc.controller.packet.platform.user;

import java.util.UUID;
import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;

public final class PlatformUserDiscordRewardStateUpdatePacket extends Packet {

  private UUID uniqueId;

  private PlatformUserDiscordRewardStateUpdatePacket() {
  }

  public PlatformUserDiscordRewardStateUpdatePacket(final UUID uniqueId) {
    this.uniqueId = uniqueId;
  }

  @Override
  public void handle(final PacketHandler ignored) {
  }

  public UUID getUniqueId() {
    return this.uniqueId;
  }
}
