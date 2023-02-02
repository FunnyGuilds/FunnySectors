package pl.rosehc.controller.packet.auth;

import java.util.List;
import pl.rosehc.adapter.redis.callback.CallbackPacket;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.wrapper.auth.AuthUserSerializableWrapper;

public final class AuthInitializationResponsePacket extends CallbackPacket {

  private List<AuthUserSerializableWrapper> users;
  private byte[] serializedConfigurationData;

  private AuthInitializationResponsePacket() {
  }

  public AuthInitializationResponsePacket(final List<AuthUserSerializableWrapper> users,
      final byte[] serializedConfigurationData) {
    this.users = users;
    this.serializedConfigurationData = serializedConfigurationData;
  }

  @Override
  public void handle(final PacketHandler ignored) {
  }

  public List<AuthUserSerializableWrapper> getUsers() {
    return this.users;
  }

  public byte[] getSerializedConfigurationData() {
    return this.serializedConfigurationData;
  }
}
