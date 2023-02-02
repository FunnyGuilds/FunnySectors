package pl.rosehc.controller.wrapper.platform.gui.deposit;

import java.util.Arrays;
import java.util.List;
import pl.rosehc.controller.wrapper.spigot.SpigotGuiElementWrapper;

public final class PlatformDepositItemSpigotGuiElementWrapper extends SpigotGuiElementWrapper {

  public String name;
  public List<String> lore = Arrays.asList(
      "",
      "&8>> &7Limit: &d{LIMIT}",
      "&8>> &7Posiadasz w depozycie: &d{AMOUNT_IN_DEPOSIT}",
      "",
      "&8>> &7Kliknij, aby &dwypłacić&7!"
  );
  public byte data;

  {
    this.material = null;
  }
}
