package pl.rosehc.controller.wrapper.platform.gui.kit;

import java.util.Arrays;
import java.util.List;
import pl.rosehc.controller.wrapper.spigot.SpigotGuiElementWrapper;

public class PlatformKitPreviewPickupGuiElementWrapper extends SpigotGuiElementWrapper {

  public String name = "&aKliknij, aby odebrać!";
  public List<String> noPermissionLore = Arrays.asList(
      "&cNie posiadasz uprawnień do tego zestawu!",
      "&cŻeby uzyskać do niego dostęp, musisz posiadać minimalnie rangę VIP"
  );
  public List<String> cannotPickupLore = Arrays.asList(
      "&cNie możesz odebrać tego zestawu w tym momencie!",
      "&cŻeby go odebrać musisz odczekać jeszcze {TIME}!"
  );
  public List<String> canPickupLore = Arrays.asList(
      "&aMożesz odebrać ten zestaw!",
      "&aAby to zrobić, kliknij LPM!"
  );

  public byte noPermissionData = 1;
  public byte cannotPickupData = 8;
  public byte canPickupData = 10;
}
