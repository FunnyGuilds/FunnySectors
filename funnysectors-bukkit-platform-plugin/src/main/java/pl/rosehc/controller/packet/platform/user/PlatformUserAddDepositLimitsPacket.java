package pl.rosehc.controller.packet.platform.user;

import java.util.Map;
import java.util.UUID;
import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.PlatformPacketHandler;
import pl.rosehc.controller.wrapper.platform.PlatformUserDepositItemTypeWrapper;

public final class PlatformUserAddDepositLimitsPacket extends Packet {

  private UUID uniqueId;
  private Map<PlatformUserDepositItemTypeWrapper, Integer> addedDepositLimitMap;

  private PlatformUserAddDepositLimitsPacket() {
  }

  public PlatformUserAddDepositLimitsPacket(final UUID uniqueId,
      final Map<PlatformUserDepositItemTypeWrapper, Integer> addedDepositLimitMap) {
    this.uniqueId = uniqueId;
    this.addedDepositLimitMap = addedDepositLimitMap;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((PlatformPacketHandler) handler).handle(this);
  }

  public UUID getUniqueId() {
    return this.uniqueId;
  }

  public Map<PlatformUserDepositItemTypeWrapper, Integer> getAddedDepositLimitMap() {
    return this.addedDepositLimitMap;
  }
}
