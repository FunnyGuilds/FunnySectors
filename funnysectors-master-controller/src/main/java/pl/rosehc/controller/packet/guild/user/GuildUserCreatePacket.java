package pl.rosehc.controller.packet.guild.user;

import java.util.UUID;
import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.GuildPacketHandler;

public final class GuildUserCreatePacket extends Packet {

  private UUID uniqueId;
  private String nickname;

  private GuildUserCreatePacket() {
  }

  public GuildUserCreatePacket(final UUID uniqueId, final String nickname) {
    this.uniqueId = uniqueId;
    this.nickname = nickname;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((GuildPacketHandler) handler).handle(this);
  }

  public UUID getUniqueId() {
    return this.uniqueId;
  }

  public String getNickname() {
    return this.nickname;
  }
}
