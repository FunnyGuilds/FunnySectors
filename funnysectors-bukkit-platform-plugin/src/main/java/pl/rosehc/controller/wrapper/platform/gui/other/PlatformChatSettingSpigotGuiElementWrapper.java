package pl.rosehc.controller.wrapper.platform.gui.other;

import java.util.Arrays;
import java.util.List;
import pl.rosehc.controller.wrapper.spigot.SpigotGuiElementWrapper;

public final class PlatformChatSettingSpigotGuiElementWrapper extends SpigotGuiElementWrapper {

  public String name;
  public List<String> enabledLore = Arrays.asList(
      "&8>> &7Status: &awłączony",
      "&8>> &7Kliknij, aby &cwyłączyć &7tą wiadomość!"
  );
  public List<String> disabledLore = Arrays.asList(
      "&8>> &7Status: &cwyłączony",
      "&8>> &7Kliknij, aby &awłączyć &7tą wiadomość!"
  );
  public byte enabledData = 5;
  public byte disabledData = 14;
}
