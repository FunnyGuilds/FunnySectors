package pl.rosehc.controller.packet.platform.user;

import java.util.List;
import pl.rosehc.adapter.redis.callback.CallbackPacket;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.wrapper.platform.PlatformUserSerializableWrapper;

public final class PlatformUsersResponsePacket extends CallbackPacket {

  private List<PlatformUserSerializableWrapper> users;

  private PlatformUsersResponsePacket() {
  }

  public PlatformUsersResponsePacket(final List<PlatformUserSerializableWrapper> users) {
    this.users = users;
  }

  @Override
  public void handle(final PacketHandler ignored) {
  }

  public List<PlatformUserSerializableWrapper> getUsers() {
    return this.users;
  }
}
