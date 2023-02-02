package pl.rosehc.controller.configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import pl.rosehc.controller.wrapper.spigot.SpigotGuiElementAdapter;
import pl.rosehc.controller.wrapper.spigot.SpigotGuiElementWrapper;

public final class ConfigurationHelper {

  private static final Gson GSON = new GsonBuilder().disableHtmlEscaping()
      .registerTypeAdapter(SpigotGuiElementWrapper.class, new SpigotGuiElementAdapter<>())
      .setPrettyPrinting().create();

  private ConfigurationHelper() {
  }

  public static <T extends ConfigurationData> T load(final File file, final Class<T> type,
      final Consumer<T> creationAction) {
    try {
      final T configuration =
          file.exists() ? deserializeConfiguration(Files.readAllBytes(file.toPath()), type)
              : type.newInstance();
      final File parentFile = file.getParentFile();
      configuration.file = file;
      if (!file.exists()) {
        creationAction.accept(configuration);
        saveConfiguration(configuration);
      }

      return configuration;
    } catch (final Exception ex) {
      throw new ExceptionInInitializerError(ex);
    }
  }

  public static <T extends ConfigurationData> T deserializeConfiguration(
      final byte[] configurationData, final Class<T> type) {
    try (final BufferedReader reader = new BufferedReader(
        new InputStreamReader(new ByteArrayInputStream(configurationData),
            StandardCharsets.UTF_8))) {
      return GSON.fromJson(reader.lines().collect(Collectors.joining()), (Type) type);
    } catch (final IOException ex) {
      throw new UnsupportedOperationException(ex);
    }
  }

  public static <T extends ConfigurationData> T load(final File file, final Class<T> type) {
    return load(file, type, ignored -> {
    });
  }

  public static <T extends ConfigurationData> byte[] serializeConfiguration(final T configuration) {
    return GSON.toJson(configuration).getBytes(StandardCharsets.UTF_8);
  }

  public static <T extends ConfigurationData> void saveConfiguration(final T configuration) {
    try {
      final File file = configuration.file;
      final File parentFile = file.getParentFile();
      if (Objects.nonNull(parentFile) && !parentFile.exists()) {
        //noinspection ResultOfMethodCallIgnored
        parentFile.mkdirs();
      }

      if (!file.exists()) {
        //noinspection ResultOfMethodCallIgnored
        file.createNewFile();
      }

      Files.write(file.toPath(), serializeConfiguration(configuration));
    } catch (final IOException ex) {
      throw new UnsupportedOperationException(ex);
    }
  }
}
