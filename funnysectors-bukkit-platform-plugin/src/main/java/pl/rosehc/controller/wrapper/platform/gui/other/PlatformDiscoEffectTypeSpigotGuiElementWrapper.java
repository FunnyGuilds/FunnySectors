package pl.rosehc.controller.wrapper.platform.gui.other;

import java.util.Arrays;
import java.util.List;
import pl.rosehc.controller.wrapper.spigot.SpigotGuiElementWrapper;

public final class PlatformDiscoEffectTypeSpigotGuiElementWrapper extends SpigotGuiElementWrapper {

  public String name;
  public List<String> selectedLore = Arrays.asList(
      "&7Aktywny: &atak",
      "&aTen tryb jest aktualnie aktywny!"
  );
  public List<String> notSelectedLore = Arrays.asList(
      "&7Aktywny: &cnie",
      "&aKliknij, aby wybrać ten tryb!"
  );
  public String leatherArmorColor;
}
