package pl.rosehc.platform.command.player;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Sender;
import org.bukkit.entity.Player;
import pl.rosehc.platform.inventory.player.other.PlayerChatSettingsInventory;

public final class ChatSettingsCommand {

  @Command(value = {"chatsettings", "cc",
      "ustawienia"}, description = "Otwiera GUI od ustawień chatu gracza.")
  public void handleChatSettings(final @Sender Player player) {
    final PlayerChatSettingsInventory inventory = new PlayerChatSettingsInventory(player);
    inventory.open();
  }
}
