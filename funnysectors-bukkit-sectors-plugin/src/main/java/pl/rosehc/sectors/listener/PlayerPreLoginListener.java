package pl.rosehc.sectors.listener;

import java.util.Objects;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.controller.ControllerPanicHelper;
import pl.rosehc.sectors.SectorsPlugin;

public final class PlayerPreLoginListener implements Listener {

  private final SectorsPlugin plugin;

  public PlayerPreLoginListener(final SectorsPlugin plugin) {
    this.plugin = plugin;
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPreLogin(final AsyncPlayerPreLoginEvent event) {
    if (!this.plugin.isLoaded()) {
      event.setLoginResult(Result.KICK_OTHER);
      event.setKickMessage(ChatColor.RED + "Sektory wciąż się ładują.");
      return;
    }

    if (ControllerPanicHelper.isInPanic()) {
      event.setLoginResult(Result.KICK_OTHER);
      event.setKickMessage(ChatColor.RED + "Serwer jest w trybie paniki!");
      return;
    }

    if (this.plugin.getSectorUserFactory().findUserByUniqueId(event.getUniqueId())
        .filter(user -> user.getSector().equals(this.plugin.getSectorFactory().getCurrentSector()))
        .isPresent() || Objects.nonNull(this.plugin.getServer().getPlayer(event.getName()))) {
      event.setLoginResult(Result.KICK_OTHER);
      event.setKickMessage(ChatHelper.colored(
          this.plugin.getSectorsConfiguration().messagesWrapper.playerIsAlreadyOnlineOnThisSector));
    }
  }
}
