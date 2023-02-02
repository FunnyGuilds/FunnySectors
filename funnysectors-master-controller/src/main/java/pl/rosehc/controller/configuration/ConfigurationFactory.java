package pl.rosehc.controller.configuration;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class ConfigurationFactory {

  private final Map<Class<?>, ConfigurationData> cachedConfigurationMap = new ConcurrentHashMap<>();
  private final Map<Class<?>, Set<ConfigurationReloadHandler<?>>> reloadHandlerMap = new ConcurrentHashMap<>();

  public <T extends ConfigurationData> void addReloadHandler(final Class<T> type,
      final ConfigurationReloadHandler<T> handler) {
    final Set<ConfigurationReloadHandler<?>> reloadHandlerSet = this.reloadHandlerMap.computeIfAbsent(
        type, ignored -> ConcurrentHashMap.newKeySet());
    if (!reloadHandlerSet.add(handler)) {
      throw new UnsupportedOperationException(
          "Cannot register reload handler for type " + type + ".");
    }
  }

  public void addConfiguration(final ConfigurationData configuration) {
    this.cachedConfigurationMap.put(configuration.getClass(), configuration);
  }

  public <C extends ConfigurationData, T extends ConfigurationReloadHandler<C>> Set<T> findReloadHandlers(
      final Class<C> type) {
    final Set<T> reloadHandlerSet = (Set<T>) this.reloadHandlerMap.get(type);
    return Objects.nonNull(reloadHandlerSet) ? reloadHandlerSet : new HashSet<>();
  }

  public <T extends ConfigurationData> T findConfiguration(final Class<T> type) {
    return (T) this.cachedConfigurationMap.get(type);
  }

  public Map<Class<?>, ConfigurationData> getCachedConfigurationMap() {
    return this.cachedConfigurationMap;
  }
}
