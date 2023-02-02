package pl.rosehc.sectors.proxy;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import pl.rosehc.sectors.SectorsConfiguration;

public final class ProxyFactory {

  private final Map<Integer, Proxy> proxyMap;

  public ProxyFactory(final SectorsConfiguration sectorsConfiguration) {
    this.proxyMap = sectorsConfiguration.proxyList.stream().map(Proxy::new)
        .collect(Collectors.toConcurrentMap(Proxy::getIdentifier, proxy -> proxy));
  }

  public void addProxy(final Proxy proxy) {
    this.proxyMap.put(proxy.getIdentifier(), proxy);
  }

  public Optional<Proxy> findProxy(final int identifier) {
    return Optional.ofNullable(this.proxyMap.get(identifier));
  }

  public Map<Integer, Proxy> getProxyMap() {
    return this.proxyMap;
  }
}
