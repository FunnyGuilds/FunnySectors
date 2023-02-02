package pl.rosehc.controller.wrapper.platform.gui.other;

import java.util.Arrays;
import java.util.List;
import pl.rosehc.controller.wrapper.spigot.SpigotGuiElementWrapper;

public final class PlatformHomeSpigotGuiElementWrapper extends SpigotGuiElementWrapper {

  public String name;
  public List<String> noPermissionLore = Arrays.asList(
      "&cNie posiadasz dostępu do tego domu!",
      "&cŻeby uzyskać do niego dostęp, musisz posiadać minimalnie rangę VIP"
  );
  public List<String> notSetLore = Arrays.asList(
      "&cTen domek nie jest aktualnie ustawiony!",
      "&7Kliknij &dRPM &7aby go ustawić!"
  );
  public List<String> alreadySetLore = Arrays.asList(
      "&7Posiadasz już tutaj dom! Kliknij &dLPM &7aby się do niego przeteleportować",
      "&7lub &5RPM &7aby nadpisać lokalizację!"
  );

  public byte noPermissionData = 1;
  public byte notSetData = 8;
  public byte alreadySetData = 10;
}
