package pl.rosehc.controller.guild.guild;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import pl.rosehc.controller.configuration.impl.configuration.GuildsConfiguration;

public final class GuildSchematicCacheHelper {

  private static volatile byte[] schematicData;

  private GuildSchematicCacheHelper() {
  }

  public static synchronized byte[] getSchematicData() {
    return schematicData;
  }

  public static synchronized void cacheSchematicData(
      final GuildsConfiguration guildsConfiguration) {
    final Path path = Paths.get(guildsConfiguration.pluginWrapper.cuboidSchematicFilePath);
    if (!Files.exists(path)) {
      System.err.println("[GILDIE] Cuboid gildii nie został odnaleziony.");
      return;
    }

    try {
      schematicData = Files.readAllBytes(path);
    } catch (final IOException e) {
      System.err.println("[GILDIE] Cuboid gildii nie mógł zostać załadowany poprawnie.");
      e.printStackTrace();
    }
  }
}
