package pl.rosehc.controller.wrapper.sector;

import java.util.UUID;

public final class SectorUserSerializableWrapper {

  private UUID uniqueId;
  private String nickname, sectorName;
  private int proxyIdentifier;

  private SectorUserSerializableWrapper() {
  }

  public SectorUserSerializableWrapper(final UUID uniqueId, final String nickname,
      final String sectorName, final int proxyIdentifier) {
    this.uniqueId = uniqueId;
    this.nickname = nickname;
    this.sectorName = sectorName;
    this.proxyIdentifier = proxyIdentifier;
  }

  public UUID getUniqueId() {
    return this.uniqueId;
  }

  public String getNickname() {
    return this.nickname;
  }

  public String getSectorName() {
    return this.sectorName;
  }

  public int getProxyIdentifier() {
    return this.proxyIdentifier;
  }
}