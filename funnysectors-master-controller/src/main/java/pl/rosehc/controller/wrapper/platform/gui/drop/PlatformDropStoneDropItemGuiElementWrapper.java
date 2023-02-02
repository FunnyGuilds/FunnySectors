package pl.rosehc.controller.wrapper.platform.gui.drop;

import java.util.Arrays;
import java.util.List;
import pl.rosehc.controller.wrapper.spigot.SpigotGuiElementWrapper;

public final class PlatformDropStoneDropItemGuiElementWrapper extends SpigotGuiElementWrapper {

  public String name;
  public List<String> enabledLore = Arrays.asList(
      "&8>> &7Szansa: &d{CHANCE}",
      "&8>> &7Exp: &d{EXP}",
      "&8>> &7Czy posiada fortunę: &atak",
      "&8>> &7Minimalna ilość do wydropienia: &d{MIN_AMOUNT}",
      "&8>> &7Maksymalna ilość do wydropienia: &d{MAX_AMOUNT}",
      "&8>> &7Dropi od &d{MIN_Y} &7do &d{MAX_Y} &7poziomu Y!",
      "&8>> &7Czy włączony: &atak"
  );
  public List<String> disabledLore = Arrays.asList(
      "&8>> &7Szansa: &d{CHANCE}",
      "&8>> &7Exp: &d{EXP}",
      "&8>> &7Czy posiada fortunę: &atak",
      "&8>> &7Minimalna ilość do wydropienia: &d{MIN_AMOUNT}",
      "&8>> &7Maksymalna ilość do wydropienia: &d{MAX_AMOUNT}",
      "&8>> &7Dropi od &d{MIN_Y} &7do &d{MAX_Y} &7poziomu Y!",
      "&8>> &7Czy włączony: &cnie"
  );
}
