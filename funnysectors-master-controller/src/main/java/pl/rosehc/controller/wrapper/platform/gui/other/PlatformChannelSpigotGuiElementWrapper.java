package pl.rosehc.controller.wrapper.platform.gui.other;

import java.util.Arrays;
import java.util.List;
import pl.rosehc.controller.wrapper.spigot.SpigotGuiElementWrapper;

public final class PlatformChannelSpigotGuiElementWrapper extends SpigotGuiElementWrapper {

  public String name = "&7Sektor: &d{SECTOR_NAME}";
  public List<String> enabledLore = Arrays.asList(
      "",
      " &7Graczy: &d{ONLINE_PLAYERS}",
      " &7TPS: &r{TPS}",
      " &7Ostatnia aktualizacja danych: &d{LAST_UPDATE} temu.",
      "",
      " &dKliknij, aby się połączyć!"
  );
  public List<String> currentLore = Arrays.asList(
      "",
      " &7Graczy: &d{ONLINE_PLAYERS}",
      " &7TPS: &r{TPS}",
      " &7Ostatnia aktualizacja danych: &d{LAST_UPDATE} temu.",
      "",
      " &dTutaj się znajdujesz!"
  );
  public List<String> heavilyLoadedLore = Arrays.asList(
      "",
      " &7Graczy: &d{ONLINE_PLAYERS}",
      " &7TPS: &r{TPS}",
      " &7Ostatnia aktualizacja danych: &d{LAST_UPDATE} temu.",
      "",
      " &cSektor jest aktualnie obciążony!"
  );
  public List<String> disabledLore = Arrays.asList(
      "",
      " &7Ostatnia aktualizacja danych: &d{LAST_UPDATE} temu.",
      " &cSektor jest aktualnie niedostępny!"
  );

  public byte enabledData = 5;
  public byte currentData = 4;
  public byte heavilyLoadedData = 7;
  public byte disabledData = 14;
}
