package pl.rosehc.controller.packet.guild.guild;

import java.util.UUID;
import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.GuildPacketHandler;

public final class GuildHelpInfoAddPacket extends Packet {

  private String guildTag;
  private UUID uniqueId;
  private String nickname;
  private long time;
  private int x, y, z;
  private boolean isAlly;

  private GuildHelpInfoAddPacket() {
  }

  public GuildHelpInfoAddPacket(final String guildTag, final UUID uniqueId, final String nickname,
      final long time, final int x, final int y, final int z, final boolean isAlly) {
    this.guildTag = guildTag;
    this.uniqueId = uniqueId;
    this.nickname = nickname;
    this.time = time;
    this.x = x;
    this.y = y;
    this.z = z;
    this.isAlly = isAlly;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((GuildPacketHandler) handler).handle(this);
  }

  public String getGuildTag() {
    return this.guildTag;
  }

  public UUID getUniqueId() {
    return this.uniqueId;
  }

  public String getNickname() {
    return this.nickname;
  }

  public long getTime() {
    return this.time;
  }

  public int getX() {
    return this.x;
  }

  public int getY() {
    return this.y;
  }

  public int getZ() {
    return this.z;
  }

  public boolean isAlly() {
    return this.isAlly;
  }
}
