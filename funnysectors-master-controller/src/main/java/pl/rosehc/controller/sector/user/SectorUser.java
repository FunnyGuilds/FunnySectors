package pl.rosehc.controller.sector.user;

import java.util.Objects;
import java.util.UUID;
import pl.rosehc.controller.proxy.Proxy;
import pl.rosehc.controller.sector.Sector;
import pl.rosehc.controller.wrapper.sector.SectorUserSerializableWrapper;

public final class SectorUser {

  private final UUID uniqueId;
  private final String nickname;
  private final Proxy proxy;
  private Sector sector;

  public SectorUser(final UUID uniqueId, final String nickname, final Proxy proxy,
      final Sector sector) {
    this.uniqueId = uniqueId;
    this.nickname = nickname;
    this.proxy = proxy;
    this.sector = sector;
  }

  public SectorUserSerializableWrapper wrap() {
    return new SectorUserSerializableWrapper(this.uniqueId, this.nickname,
        Objects.nonNull(this.sector) ? this.sector.getName() : null, this.proxy.getIdentifier());
  }

  public UUID getUniqueId() {
    return this.uniqueId;
  }

  public String getNickname() {
    return this.nickname;
  }

  public Proxy getProxy() {
    return this.proxy;
  }

  public Sector getSector() {
    return this.sector;
  }

  public void setSector(final Sector sector) {
    this.sector = sector;
  }
}
