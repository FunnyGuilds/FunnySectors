package pl.rosehc.controller.packet.guild.guild;

import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;

public final class GuildAlertPacket extends Packet {

  private String guildTag, message;

  private GuildAlertPacket() {
  }

  public GuildAlertPacket(final String guildTag, final String message) {
    this.guildTag = guildTag;
    this.message = message;
  }

  @Override
  public void handle(final PacketHandler ignored) {
  }

  public String getGuildTag() {
    return this.guildTag;
  }

  public String getMessage() {
    return this.message;
  }
}
