package pl.rosehc.controller.wrapper.platform.gui.drop;

import java.util.List;
import pl.rosehc.controller.wrapper.spigot.SpigotGuiElementWrapper;

public final class PlatformDropPreviewItemGuiElementWrapper extends SpigotGuiElementWrapper {

  public String name;
  public List<String> lore;

  {
    this.material = null;
  }
}
