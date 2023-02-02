package pl.rosehc.controller.packet.platform.safe.request;

import java.util.List;
import pl.rosehc.adapter.redis.callback.CallbackPacket;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.wrapper.platform.PlatformSafeSerializableWrapper;

public final class PlatformSafeDataResponsePacket extends CallbackPacket {

  private List<PlatformSafeSerializableWrapper> safes;

  private PlatformSafeDataResponsePacket() {
  }

  public PlatformSafeDataResponsePacket(final List<PlatformSafeSerializableWrapper> safes) {
    this.safes = safes;
  }

  @Override
  public void handle(final PacketHandler ignored) {
  }

  public List<PlatformSafeSerializableWrapper> getSafes() {
    return this.safes;
  }
}
