package pl.rosehc.controller.wrapper.platform.gui.drop;

import java.util.Arrays;
import java.util.List;
import pl.rosehc.controller.wrapper.spigot.SpigotGuiElementWrapper;

public final class PlatformDropStoneDropCobbleStoneItemGuiElementWrapper extends
    SpigotGuiElementWrapper {

  public String name = "&cCobbleStone";
  public List<String> enabledLore = Arrays.asList(
      "&8>> &7Aktywny: &atak",
      "&8>> &7Kliknij, aby &cwyłączyć&7!"
  );
  public List<String> disabledLore = Arrays.asList(
      "&8>> &7Aktywny: &cnie",
      "&8>> &7Kliknij, aby &awłączyć&7!"
  );

  {
    this.material = null;
  }
}
