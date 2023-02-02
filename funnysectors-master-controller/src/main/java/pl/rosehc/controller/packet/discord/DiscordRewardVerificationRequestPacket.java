package pl.rosehc.controller.packet.discord;

import pl.rosehc.adapter.redis.callback.CallbackPacket;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.DiscordPacketHandler;

public final class DiscordRewardVerificationRequestPacket extends CallbackPacket {

  private String nickname;
  private long userId;

  private DiscordRewardVerificationRequestPacket() {
  }

  public DiscordRewardVerificationRequestPacket(final String nickname, final long userId) {
    this.nickname = nickname;
    this.userId = userId;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((DiscordPacketHandler) handler).handle(this);
  }

  public String getNickname() {
    return this.nickname;
  }

  public long getUserId() {
    return this.userId;
  }
}
