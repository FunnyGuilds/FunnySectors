package pl.rosehc.controller.configuration;

@FunctionalInterface
public interface ConfigurationReloadHandler<T extends ConfigurationData> {

  void handle(final T configuration);
}
