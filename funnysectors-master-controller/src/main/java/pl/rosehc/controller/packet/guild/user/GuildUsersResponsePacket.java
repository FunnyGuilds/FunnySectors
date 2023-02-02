package pl.rosehc.controller.packet.guild.user;

import java.util.List;
import pl.rosehc.adapter.redis.callback.CallbackPacket;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.wrapper.guild.GuildUserSerializableWrapper;

public final class GuildUsersResponsePacket extends CallbackPacket {

  private List<GuildUserSerializableWrapper> users;

  private GuildUsersResponsePacket() {
  }

  public GuildUsersResponsePacket(final List<GuildUserSerializableWrapper> users) {
    this.users = users;
  }

  @Override
  public void handle(final PacketHandler ignored) {
  }

  public List<GuildUserSerializableWrapper> getUsers() {
    return this.users;
  }
}
