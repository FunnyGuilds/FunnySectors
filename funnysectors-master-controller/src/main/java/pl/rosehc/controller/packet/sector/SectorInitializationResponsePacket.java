package pl.rosehc.controller.packet.sector;

import java.util.List;
import pl.rosehc.adapter.redis.callback.CallbackPacket;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.wrapper.sector.SectorUserSerializableWrapper;

public final class SectorInitializationResponsePacket extends CallbackPacket {

  private String sectorName;
  private List<SectorUserSerializableWrapper> sectorUsers;
  private byte[] sectorsConfigurationData;

  private SectorInitializationResponsePacket() {
  }

  public SectorInitializationResponsePacket(final String sectorName,
      final List<SectorUserSerializableWrapper> sectorUsers,
      final byte[] sectorsConfigurationData) {
    this.sectorName = sectorName;
    this.sectorUsers = sectorUsers;
    this.sectorsConfigurationData = sectorsConfigurationData;
  }

  @Override
  public void handle(final PacketHandler handler) {
  }

  public String getSectorName() {
    return this.sectorName;
  }

  public List<SectorUserSerializableWrapper> getSectorUsers() {
    return this.sectorUsers;
  }

  public byte[] getSectorsConfigurationData() {
    return this.sectorsConfigurationData;
  }
}
