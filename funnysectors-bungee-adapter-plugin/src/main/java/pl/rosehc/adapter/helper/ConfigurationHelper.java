package pl.rosehc.adapter.helper;

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
import java.util.stream.Collectors;
import pl.rosehc.adapter.configuration.ConfigurationData;

public final class ConfigurationHelper {

  private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting()
      .create();

  private ConfigurationHelper() {
  }

  public static <T extends ConfigurationData> T load(final File file, final Class<T> type) {
    try {
      final T configuration =
          file.exists() ? deserializeConfiguration(Files.readAllBytes(file.toPath()), type)
              : type.newInstance();
      final File parentFile = file.getParentFile();
      configuration.file = file;
      if (!file.exists()) {
        saveConfiguration(configuration);
      }

      return configuration;
    } catch (Exception e) {
      throw new ExceptionInInitializerError(e);
    }
  }

  public static <T extends ConfigurationData> T deserializeConfiguration(
      final byte[] configurationData, final Class<T> type) {
    try (final BufferedReader reader = new BufferedReader(
        new InputStreamReader(new ByteArrayInputStream(configurationData)))) {
      return GSON.fromJson(reader.lines().collect(Collectors.joining()), (Type) type);
    } catch (IOException e) {
      throw new UnsupportedOperationException(e);
    }
  }

  public static <T extends ConfigurationData> byte[] serializeConfiguration(final T configuration) {
    return GSON.toJson(configuration).getBytes(StandardCharsets.UTF_8);
  }

  public static <T extends ConfigurationData> void saveConfiguration(final T configuration)
      throws IOException {
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
  }
}
