package pl.rosehc.platform.command.player;

import java.util.Objects;
import java.util.UUID;
import me.vaperion.blade.annotation.Combined;
import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Name;
import me.vaperion.blade.annotation.Sender;
import me.vaperion.blade.exception.BladeExitMessage;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.controller.packet.platform.safe.PlatformSafeDescriptionUpdatePacket;
import pl.rosehc.platform.PlatformPlugin;
import pl.rosehc.platform.safe.Safe;

public final class SafeDescriptionCommand {

  private final PlatformPlugin plugin;

  public SafeDescriptionCommand(final PlatformPlugin plugin) {
    this.plugin = plugin;
  }

  @Command(value = {"description", "opis"}, description = "Ustawia opis danego sejfu")
  public void handleSafeDescription(final @Sender Player player,
      @Name("description") @Combined String description) {
    description = ChatColor.stripColor(ChatHelper.colored(description));
    if (description.trim().isEmpty()) {
      throw new BladeExitMessage(ChatHelper.colored(
          this.plugin.getPlatformConfiguration().messagesWrapper.safeDescriptionCannotBeEmpty));
    }

    if (description.length() > 16) {
      throw new BladeExitMessage(ChatHelper.colored(
          this.plugin.getPlatformConfiguration().messagesWrapper.safeDescriptionHasTooManyChars));
    }

    final ItemStack itemInHand = player.getInventory().getItemInHand();
    if (Objects.isNull(itemInHand) || !itemInHand.hasItemMeta() || !itemInHand.getItemMeta()
        .hasDisplayName() || !itemInHand.getItemMeta().getDisplayName().equals(ChatHelper.colored(
        this.plugin.getPlatformConfiguration().customItemsWrapper.safeItemWrapper.name))) {
      throw new BladeExitMessage(ChatHelper.colored(
          this.plugin.getPlatformConfiguration().messagesWrapper.noSafeInInventory));
    }

    final int itemInHandSlot = player.getInventory().first(itemInHand);
    if (itemInHandSlot == -1) {
      throw new BladeExitMessage(ChatHelper.colored(
          this.plugin.getPlatformConfiguration().messagesWrapper.noSafeInInventory));
    }

    final NBTTagCompound tag = CraftItemStack.asNMSCopy(itemInHand).getTag();
    if (Objects.isNull(tag) || !tag.hasKey("SafeUniqueId")) {
      throw new BladeExitMessage(ChatHelper.colored(
          this.plugin.getPlatformConfiguration().messagesWrapper.noSafeInInventory));
    }

    final UUID safeUniqueId = UUID.fromString(tag.getString("SafeUniqueId"));
    final Safe safe = this.plugin.getSafeFactory().findSafe(safeUniqueId).orElseThrow(
        () -> new BladeExitMessage(ChatHelper.colored(
            this.plugin.getPlatformConfiguration().messagesWrapper.safeNotFoundInDatabase)));
    if (!safe.getOwnerUniqueId().equals(player.getUniqueId()) || !safe.getOwnerNickname()
        .equals(player.getName())) {
      throw new BladeExitMessage(ChatHelper.colored(
          this.plugin.getPlatformConfiguration().messagesWrapper.safeIsNotYours));
    }

    safe.setDescription(description);
    player.getInventory().setItem(itemInHandSlot,
        this.plugin.getPlatformConfiguration().customItemsWrapper.safeItemWrapper.asItemStack(
            safe));
    ChatHelper.sendMessage(player,
        this.plugin.getPlatformConfiguration().messagesWrapper.safeDescriptionHasBeenChanged.replace(
            "{DESCRIPTION}", description));
    this.plugin.getRedisAdapter()
        .sendPacket(new PlatformSafeDescriptionUpdatePacket(safeUniqueId, description),
            "rhc_master_controller", "rhc_platform");
  }
}
