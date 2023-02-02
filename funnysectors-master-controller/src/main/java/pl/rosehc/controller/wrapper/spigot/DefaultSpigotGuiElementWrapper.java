package pl.rosehc.controller.wrapper.spigot;

import java.util.List;

public class DefaultSpigotGuiElementWrapper extends SpigotGuiElementWrapper {

  public String name;
  public List<String> lore;
  public List<SpigotGuiEnchantmentWrapper> enchantments;
  public String skinValue, skinSignature;
  public short data;
  public int amount = 1;
}
