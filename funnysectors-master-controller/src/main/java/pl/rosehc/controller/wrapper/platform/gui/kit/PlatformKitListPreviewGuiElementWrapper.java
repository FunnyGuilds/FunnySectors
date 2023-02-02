package pl.rosehc.controller.wrapper.platform.gui.kit;

import java.util.Arrays;
import java.util.List;
import pl.rosehc.controller.wrapper.spigot.SpigotGuiElementWrapper;
import pl.rosehc.controller.wrapper.spigot.SpigotGuiEnchantmentWrapper;

public final class PlatformKitListPreviewGuiElementWrapper extends SpigotGuiElementWrapper {

  public String name;
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
      "&aAby to zrobić, kliknij w kita oraz kliknij",
      "&ana specjalny item w gui od podglądu!"
  );
  public List<SpigotGuiEnchantmentWrapper> enchantments;
  public byte data;
}
