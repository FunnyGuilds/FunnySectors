package pl.rosehc.sectors.proxy;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import pl.rosehc.sectors.SectorsConfiguration;

public final class ProxyFactory {

  private final Map<Integer, Proxy> proxyMap;
  private final int currentProxyIdentifier;
  private Proxy currentProxy;

  public ProxyFactory(final SectorsConfiguration sectorsConfiguration,
      final int currentProxyIdentifier) {
    this.proxyMap = sectorsConfiguration.proxyList.stream().map(Proxy::new)
        .collect(Collectors.toConcurrentMap(Proxy::getIdentifier, proxy -> proxy));
    this.currentProxyIdentifier = currentProxyIdentifier;
    this.updateCurrentProxy();
  }

  public void addProxy(final Proxy proxy) {
    this.proxyMap.put(proxy.getIdentifier(), proxy);
  }

  public synchronized void updateCurrentProxy() {
    this.currentProxy = this.findProxy(this.currentProxyIdentifier).orElseThrow(
        () -> new UnsupportedOperationException(
            "Aktualne proxy nie istnieje? (" + this.currentProxyIdentifier + ")"));
  }

  public Optional<Proxy> findProxy(final int identifier) {
    return Optional.ofNullable(this.proxyMap.get(identifier));
  }

  public Proxy getCurrentProxy() {
    return this.currentProxy;
  }

  public Map<Integer, Proxy> getProxyMap() {
    return this.proxyMap;
  }
}
