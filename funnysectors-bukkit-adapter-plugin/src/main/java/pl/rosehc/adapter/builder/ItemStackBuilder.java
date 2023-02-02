package pl.rosehc.adapter.builder;

import com.mojang.authlib.GameProfile;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import pl.rosehc.adapter.helper.ChatHelper;

public final class ItemStackBuilder {

  private static final Field SKULL_PROFILE_FIELD;

  static {
    try {
      SKULL_PROFILE_FIELD = Class.forName("org.bukkit.craftbukkit.v1_8_R3.inventory.CraftMetaSkull")
          .getDeclaredField("profile");
      SKULL_PROFILE_FIELD.setAccessible(true);
    } catch (final Exception ex) {
      throw new ExceptionInInitializerError(ex);
    }
  }

  private final ItemStack itemStack;
  private ItemMeta itemMeta;

  public ItemStackBuilder(final ItemStack itemStack) {
    this.itemStack = itemStack;
    this.itemMeta = itemStack.getItemMeta();
  }

  public ItemStackBuilder(final Material material, int amount, short durability) {
    this(new ItemStack(material, amount, durability));
  }

  public ItemStackBuilder withName(final String displayName) {
    this.itemMeta.setDisplayName(ChatHelper.colored(displayName));
    return this;
  }

  public ItemStackBuilder withLore(final List<String> lore) {
    this.itemMeta.setLore(ChatHelper.colored(lore));
    return this;
  }

  public ItemStackBuilder withLore(final String... lore) {
    return withLore(Arrays.asList(lore));
  }

  public ItemStackBuilder withHeadOwner(final GameProfile profile) {
    final SkullMeta skullMeta = (SkullMeta) this.itemMeta;
    try {
      SKULL_PROFILE_FIELD.set(skullMeta, profile);
    } catch (final Exception ex) {
      ex.printStackTrace();
      throw new UnsupportedOperationException(ex);
    }

    this.itemMeta = skullMeta;
    return this;
  }

  public ItemStackBuilder withHeadOwner(final String owner) {
    final SkullMeta skullMeta = (SkullMeta) this.itemMeta;
    skullMeta.setOwner(owner);
    this.itemMeta = skullMeta;
    return this;
  }

  public ItemStackBuilder withColor(final Color color) {
    final LeatherArmorMeta meta = (LeatherArmorMeta) this.itemMeta;
    meta.setColor(color);
    this.itemMeta = meta;
    return this;
  }

  public ItemStackBuilder withEnchantments(final Map<Enchantment, Integer> enchantments) {
    enchantments.forEach((key, value) -> this.itemMeta.addEnchant(key, value, true));
    return this;
  }

  public ItemStackBuilder withFlags(final ItemFlag... itemFlags) {
    this.itemMeta.addItemFlags(itemFlags);
    return this;
  }

  public ItemStack build() {
    this.itemStack.setItemMeta(this.itemMeta);
    return this.itemStack;
  }
}
