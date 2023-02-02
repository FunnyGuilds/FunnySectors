package pl.rosehc.controller.packet.guild.user;

import java.util.UUID;
import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.GuildPacketHandler;

public final class GuildUserSynchronizeRankingPacket extends Packet {

  private UUID uniqueId;
  private int points, kills, deaths;
  private int killStreak;

  private GuildUserSynchronizeRankingPacket() {
  }

  public GuildUserSynchronizeRankingPacket(final UUID uniqueId, final int points, final int kills,
      final int deaths, final int killStreak) {
    this.uniqueId = uniqueId;
    this.points = points;
    this.kills = kills;
    this.deaths = deaths;
    this.killStreak = killStreak;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((GuildPacketHandler) handler).handle(this);
  }

  public UUID getUniqueId() {
    return this.uniqueId;
  }

  public int getPoints() {
    return this.points;
  }

  public int getKills() {
    return this.kills;
  }

  public int getDeaths() {
    return this.deaths;
  }

  public int getKillStreak() {
    return this.killStreak;
  }
}
