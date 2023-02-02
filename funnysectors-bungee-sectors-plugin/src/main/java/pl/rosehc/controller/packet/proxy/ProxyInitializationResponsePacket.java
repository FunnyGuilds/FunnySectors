package pl.rosehc.controller.packet.proxy;

import java.util.List;
import pl.rosehc.adapter.redis.callback.CallbackPacket;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.wrapper.sector.SectorUserSerializableWrapper;

public final class ProxyInitializationResponsePacket extends CallbackPacket {

  private int proxyIdentifier;
  private List<SectorUserSerializableWrapper> sectorUsers;
  private byte[] sectorsConfigurationData;

  private ProxyInitializationResponsePacket() {
  }

  @Override
  public void handle(final PacketHandler handler) {
  }

  public int getProxyIdentifier() {
    return this.proxyIdentifier;
  }

  public List<SectorUserSerializableWrapper> getSectorUsers() {
    return this.sectorUsers;
  }

  public byte[] getSectorsConfigurationData() {
    return this.sectorsConfigurationData;
  }
}
