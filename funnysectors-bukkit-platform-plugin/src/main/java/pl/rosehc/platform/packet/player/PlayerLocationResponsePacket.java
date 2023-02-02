package pl.rosehc.platform.packet.player;

import java.util.UUID;
import pl.rosehc.adapter.redis.callback.CallbackPacket;
import pl.rosehc.adapter.redis.packet.PacketHandler;

public final class PlayerLocationResponsePacket extends CallbackPacket {

  private UUID uniqueId;
  private String location;

  private PlayerLocationResponsePacket() {
  }

  public PlayerLocationResponsePacket(final UUID uniqueId) {
    this.uniqueId = uniqueId;
  }

  @Override
  public void handle(final PacketHandler ignored) {
  }

  public UUID getUniqueId() {
    return this.uniqueId;
  }

  public String getLocation() {
    return this.location;
  }

  public void setLocation(String location) {
    this.location = location;
  }
}
