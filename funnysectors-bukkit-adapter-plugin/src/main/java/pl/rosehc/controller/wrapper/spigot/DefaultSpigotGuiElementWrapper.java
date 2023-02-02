package pl.rosehc.controller.wrapper.spigot;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import pl.rosehc.adapter.builder.ItemStackBuilder;

public class DefaultSpigotGuiElementWrapper extends SpigotGuiElementWrapper {

  public String name;
  public List<String> lore;
  public List<SpigotGuiEnchantmentWrapper> enchantments;
  public String skinValue, skinSignature;
  public int amount = 1;
  public short data;
  private transient GameProfile profile;

  @Override
  public ItemStack asItemStack() {
    if ((this.skinValue != null && this.skinSignature != null && this.material.equals("SKULL_ITEM"))
        && (this.profile == null || this.doesNotHaveRightProperties(this.profile, this.skinValue,
        this.skinSignature))) {
      this.profile = new GameProfile(
          UUID.nameUUIDFromBytes("fake-profile".getBytes(StandardCharsets.UTF_8)), "fake-profile");
      this.profile.getProperties().removeAll("textures");
      this.profile.getProperties()
          .put("textures", new Property("textures", this.skinValue, this.skinSignature));
    }

    final ItemStackBuilder builder = new ItemStackBuilder(Material.matchMaterial(this.material),
        this.amount, this.data);
    if (this.enchantments != null && !this.enchantments.isEmpty()) {
      final Map<Enchantment, Integer> enchantmentMap = new HashMap<>();
      for (final SpigotGuiEnchantmentWrapper enchantment : this.enchantments) {
        enchantmentMap.put(Enchantment.getByName(enchantment.enchantmentName),
            enchantment.enchantmentLevel);
      }

      builder.withEnchantments(enchantmentMap);
    }

    if (this.profile != null) {
      builder.withHeadOwner(this.profile);
    }
    if (this.name != null) {
      builder.withName(this.name);
    }
    if (this.lore != null && !this.lore.isEmpty()) {
      builder.withLore(this.lore);
    }

    return builder.build();
  }

  private boolean doesNotHaveRightProperties(final GameProfile profile, final String skinValue,
      final String skinSignature) {
    final Collection<Property> propertyCollection = profile.getProperties().get("textures");
    boolean hasRightProperties = false;
    if (Objects.nonNull(propertyCollection) && !propertyCollection.isEmpty()) {
      for (final Property property : propertyCollection) {
        if (property.getName().equals("textures") && property.getValue().equals(skinValue)
            && property.getSignature().equals(skinValue)) {
          hasRightProperties = true;
          break;
        }
      }
    }

    return !hasRightProperties;
  }
}
