package pl.rosehc.platform.listener.player;

import java.util.Arrays;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.adapter.helper.TimeHelper;
import pl.rosehc.platform.PlatformPlugin;
import pl.rosehc.platform.user.PlatformUser;

public final class PlayerCommandPreprocessListener implements Listener {

  private static final List<String> SERVER_INFO_COMMANDS = Arrays.asList(
      "/bukkit:version",
      "/bukkit:ver",
      "/bukkit:about",
      "/?",
      "/version",
      "/ver",
      "/about"
  );
  private final PlatformPlugin plugin;

  public PlayerCommandPreprocessListener(final PlatformPlugin plugin) {
    this.plugin = plugin;
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onCommand(final PlayerCommandPreprocessEvent event) {
    final Player player = event.getPlayer();
    final String command = event.getMessage().split(" ")[0].toLowerCase();
    if (this.plugin.getServer().getHelpMap().getHelpTopic(command) == null) {
      event.setCancelled(true);
      ChatHelper.sendMessage(player,
          this.plugin.getPlatformConfiguration().messagesWrapper.commandNotFound);
      return;
    }

    if (!player.isOp()) {
      if (SERVER_INFO_COMMANDS.contains(command)) {
        event.setCancelled(true);
        ChatHelper.sendMessage(player,
            "&7Ten serwer używa &ddkKguildTools 5.5-RELEASE &7stworzone przez &5dkcode.eu");
        return;
      }

      if (this.plugin.getPlatformConfiguration().blockedCommandsWrapper.mainBlockedCommandList.contains(
          command)) {
        event.setCancelled(true);
        ChatHelper.sendMessage(player,
            this.plugin.getPlatformConfiguration().messagesWrapper.commandIsBlocked);
        return;
      }
    }

    if (!player.hasPermission("platform-combat-bypass")
        && !this.plugin.getPlatformConfiguration().blockedCommandsWrapper.combatAllowedCommandList.contains(
        command)) {
      this.plugin.getPlatformUserFactory().findUserByUniqueId(player.getUniqueId())
          .filter(PlatformUser::isInCombat).ifPresent(user -> {
            event.setCancelled(true);
            ChatHelper.sendMessage(player,
                this.plugin.getPlatformConfiguration().messagesWrapper.cannotExecuteThisCommandInCombat.replace(
                    "{TIME}",
                    TimeHelper.timeToString(user.getCombatTime() - System.currentTimeMillis())));
          });
    }
  }
}
