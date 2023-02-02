package pl.rosehc.controller.packet.guild.guild;

import java.util.List;
import pl.rosehc.adapter.redis.callback.CallbackPacket;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.wrapper.guild.GuildSerializableWrapper;

public final class GuildGuildsResponsePacket extends CallbackPacket {

  private List<GuildSerializableWrapper> guildSerializableWrapperList;

  private GuildGuildsResponsePacket() {
  }

  public GuildGuildsResponsePacket(
      final List<GuildSerializableWrapper> guildSerializableWrapperList) {
    this.guildSerializableWrapperList = guildSerializableWrapperList;
  }

  @Override
  public void handle(final PacketHandler ignored) {
  }

  public List<GuildSerializableWrapper> getGuildSerializableWrapperList() {
    return this.guildSerializableWrapperList;
  }
}
