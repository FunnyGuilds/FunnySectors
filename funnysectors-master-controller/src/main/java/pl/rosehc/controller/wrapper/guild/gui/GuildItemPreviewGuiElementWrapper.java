package pl.rosehc.controller.wrapper.guild.gui;

import java.util.List;
import pl.rosehc.controller.wrapper.spigot.SpigotGuiElementWrapper;

public final class GuildItemPreviewGuiElementWrapper extends SpigotGuiElementWrapper {

  public String name;
  public List<String> lore;

  {
    this.material = null;
  }
}
