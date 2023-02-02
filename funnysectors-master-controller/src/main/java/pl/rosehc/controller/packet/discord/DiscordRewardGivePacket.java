package pl.rosehc.controller.packet.discord;

import java.util.UUID;
import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;

public final class DiscordRewardGivePacket extends Packet {

  private UUID uniqueId;

  private DiscordRewardGivePacket() {
  }

  public DiscordRewardGivePacket(final UUID uniqueId) {
    this.uniqueId = uniqueId;
  }

  @Override
  public void handle(final PacketHandler ignored) {
  }

  public UUID getUniqueId() {
    return this.uniqueId;
  }
}
