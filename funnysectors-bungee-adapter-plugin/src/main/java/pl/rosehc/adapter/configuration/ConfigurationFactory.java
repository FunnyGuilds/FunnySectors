package pl.rosehc.adapter.configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ConfigurationFactory {

  private final Map<Class<?>, ConfigurationData> cachedConfigurationMap = new ConcurrentHashMap<>();

  public void addConfiguration(final ConfigurationData configuration) {
    this.cachedConfigurationMap.put(configuration.getClass(), configuration);
  }

  public <T extends ConfigurationData> T findConfiguration(final Class<T> type) {
    //noinspection unchecked
    return (T) this.cachedConfigurationMap.get(type);
  }

  public Map<Class<?>, ConfigurationData> getCachedConfigurationMap() {
    return this.cachedConfigurationMap;
  }
}
