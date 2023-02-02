package pl.rosehc.platform.command.player;

import java.util.Objects;
import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Sender;
import me.vaperion.blade.exception.BladeExitMessage;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.adapter.helper.TimeHelper;
import pl.rosehc.controller.packet.platform.user.PlatformUserSynchronizeSomeDropSettingsDataPacket;
import pl.rosehc.controller.wrapper.platform.PlatformUserCooldownType;
import pl.rosehc.platform.PlatformPlugin;
import pl.rosehc.platform.user.PlatformUser;
import pl.rosehc.platform.user.subdata.PlatformUserDropSettings;

public final class RepairPickaxeCommand {

  private static final ItemStack COST_ITEM_STACK = new ItemStack(Material.DIAMOND, 16);
  private final PlatformPlugin plugin;

  public RepairPickaxeCommand(final PlatformPlugin plugin) {
    this.plugin = plugin;
  }

  @Command(value = {"repairpickaxe", "naprawkilof"}, description = "Naprawia kilof w twojej ręce.")
  public void handleRepairPickaxe(final @Sender Player player) {
    final ItemStack itemInHand = player.getItemInHand();
    if (Objects.isNull(itemInHand) || !itemInHand.getType().name().endsWith("PICKAXE")) {
      throw new BladeExitMessage(ChatHelper.colored(
          this.plugin.getPlatformConfiguration().messagesWrapper.repairPickaxeCannotRepairItemThatIsNotAPickaxe));
    }

    if (!player.getInventory().containsAtLeast(COST_ITEM_STACK, COST_ITEM_STACK.getAmount())) {
      throw new BladeExitMessage(ChatHelper.colored(
          this.plugin.getPlatformConfiguration().messagesWrapper.repairPickaxeNoRequiredDiamonds));
    }

    final PlatformUser user = this.plugin.getPlatformUserFactory()
        .findUserByUniqueId(player.getUniqueId()).orElseThrow(() -> new BladeExitMessage(
            ChatHelper.colored(
                this.plugin.getPlatformConfiguration().messagesWrapper.playerNotFound.replace(
                    "{PLAYER_NAME}", player.getName()))));
    if (user.getCooldownCache().hasUserCooldown(PlatformUserCooldownType.SECTOR_CHANGE)) {
      throw new BladeExitMessage(ChatHelper.colored(
          this.plugin.getPlatformConfiguration().messagesWrapper.repairPickaxeIsCooldowned.replace(
              "{TIME}", TimeHelper.timeToString(user.getCooldownCache()
                  .getUserCooldown(PlatformUserCooldownType.REPAIR_PICKAXE)))));
    }

    final PlatformUserDropSettings dropSettings = user.getDropSettings();
    if (dropSettings.getLevel() < 30) {
      throw new BladeExitMessage(ChatHelper.colored(
          this.plugin.getPlatformConfiguration().messagesWrapper.repairPickaxeNoRequiredLevel));
    }

    dropSettings.setLevel(dropSettings.getLevel() - 30);
    dropSettings.setNeededXP(
        dropSettings.getNeededXP() - (int) (dropSettings.getNeededXP() * 0.1D));
    player.playSound(player.getLocation(), Sound.ANVIL_USE, 50F, 50F);
    itemInHand.setDurability((short) 0);
    player.getInventory().removeItem(COST_ITEM_STACK);
    ChatHelper.sendMessage(player,
        this.plugin.getPlatformConfiguration().messagesWrapper.repairPickaxeHasBeenSuccessfullyRepaired);
    user.getCooldownCache().putUserCooldown(PlatformUserCooldownType.REPAIR_PICKAXE);
    this.plugin.getRedisAdapter().sendPacket(
        new PlatformUserSynchronizeSomeDropSettingsDataPacket(user.getUniqueId(),
            dropSettings.getTurboDropMultiplier(), dropSettings.isCobbleStone(),
            dropSettings.getTurboDropTime(), dropSettings.getCurrentXP(),
            dropSettings.getNeededXP(), dropSettings.getLevel()), "rhc_master_controller",
        "rhc_platform");
  }
}
