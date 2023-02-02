package pl.rosehc.sectors.listener;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.controller.ControllerPanicHelper;
import pl.rosehc.sectors.SectorsPlugin;

public final class PlayerPreLoginListener implements Listener {

  private final SectorsPlugin plugin;

  public PlayerPreLoginListener(final SectorsPlugin plugin) {
    this.plugin = plugin;
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPreLogin(final PreLoginEvent event) {
    if (!this.plugin.isLoaded()) {
      event.setCancelled(true);
      event.setCancelReason(new TextComponent(ChatColor.RED + "Sektory wciąż się ładują."));
      return;
    }

    if (ControllerPanicHelper.isInPanic()) {
      event.setCancelled(true);
      event.setCancelReason(new TextComponent(ChatColor.RED + "Serwer jest w trybie paniki!"));
      return;
    }

    if (this.plugin.getSectorUserFactory().findUserByNickname(event.getConnection().getName())
        .isPresent()) {
      event.setCancelled(true);
      event.setCancelReason(TextComponent.fromLegacyText(ChatHelper.colored(
          this.plugin.getSectorsConfiguration().messagesWrapper.playerIsAlreadyOnline)));
    }
  }
}
