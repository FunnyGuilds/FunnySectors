package pl.rosehc.controller.packet.platform.user;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;

/**
 * @author stevimeister on 31/01/2022
 **/
public final class PlatformUserMessagePacket extends Packet {

  private List<UUID> uniqueIds;
  private String message;

  private PlatformUserMessagePacket() {
  }

  public PlatformUserMessagePacket(final List<UUID> uniqueIds, final String message) {
    this.uniqueIds = uniqueIds;
    this.message = message;
  }

  public PlatformUserMessagePacket(final String message) {
    //noinspection ArraysAsListWithZeroOrOneArgument
    this(Arrays.asList(), message);
  }

  @Override
  public void handle(final PacketHandler ignored) {
  }

  public List<UUID> getUniqueIds() {
    return this.uniqueIds;
  }

  public String getMessage() {
    return this.message;
  }
}
