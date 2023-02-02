package pl.rosehc.sectors.data;

import java.util.UUID;
import pl.rosehc.adapter.redis.callback.CallbackPacket;
import pl.rosehc.adapter.redis.packet.PacketHandler;

/**
 * @author stevimeister on 06/01/2022
 **/
public final class SectorPlayerDataSynchronizeResponsePacket extends CallbackPacket {

  private UUID uniqueId;

  private SectorPlayerDataSynchronizeResponsePacket() {
  }

  public SectorPlayerDataSynchronizeResponsePacket(final UUID uniqueId) {
    this.uniqueId = uniqueId;
  }

  @Override
  public void handle(final PacketHandler handler) {
  }

  public UUID getUniqueId() {
    return this.uniqueId;
  }
}