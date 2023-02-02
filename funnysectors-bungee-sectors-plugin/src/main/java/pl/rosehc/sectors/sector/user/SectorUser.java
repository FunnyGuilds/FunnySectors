package pl.rosehc.sectors.sector.user;

import java.util.Objects;
import java.util.UUID;
import pl.rosehc.controller.wrapper.sector.SectorUserSerializableWrapper;
import pl.rosehc.sectors.SectorsPlugin;
import pl.rosehc.sectors.proxy.Proxy;
import pl.rosehc.sectors.sector.Sector;

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

  public static SectorUser unwrap(final SectorUserSerializableWrapper sectorUser) {
    return new SectorUser(sectorUser.getUniqueId(), sectorUser.getNickname(),
        SectorsPlugin.getInstance().getProxyFactory().findProxy(sectorUser.getProxyIdentifier())
            .orElseThrow(() -> new UnsupportedOperationException(
                "Uzytkownik " + sectorUser.getNickname() + " nie posiada proxy.")),
        SectorsPlugin.getInstance().getSectorFactory().findSector(sectorUser.getSectorName())
            .orElseThrow(() -> new UnsupportedOperationException(
                "Gracz " + sectorUser.getSectorName() + " nie posiada sektora.")));
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
