package pl.rosehc.controller.packet.platform.ban;

import java.util.List;
import pl.rosehc.adapter.redis.callback.CallbackPacket;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.wrapper.platform.PlatformBanSerializableWrapper;

public final class PlatformBansResponsePacket extends CallbackPacket {

  private List<PlatformBanSerializableWrapper> bans;

  private PlatformBansResponsePacket() {
  }

  public PlatformBansResponsePacket(final List<PlatformBanSerializableWrapper> bans) {
    this.bans = bans;
  }

  @Override
  public void handle(final PacketHandler ignored) {
  }

  public List<PlatformBanSerializableWrapper> getBans() {
    return this.bans;
  }
}
