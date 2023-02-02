package pl.rosehc.platform.listener.player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.adapter.helper.TimeHelper;
import pl.rosehc.controller.ControllerPanicHelper;
import pl.rosehc.controller.packet.platform.user.PlatformUserMessagePacket;
import pl.rosehc.controller.wrapper.platform.PlatformUserCooldownType;
import pl.rosehc.platform.PlatformPlugin;
import pl.rosehc.platform.rank.Rank;
import pl.rosehc.platform.user.PlatformUser;
import pl.rosehc.platform.user.subdata.PlatformUserChatSettings;
import pl.rosehc.platform.user.subdata.PlatformUserCooldownCache;
import pl.rosehc.sectors.SectorsPlugin;
import pl.rosehc.sectors.sector.user.SectorUser;

/**
 * @author stevimeister on 31/01/2022
 **/
public final class AsyncPlayerChatListener implements Listener {

  private final PlatformPlugin plugin;

  public AsyncPlayerChatListener(final PlatformPlugin plugin) {
    this.plugin = plugin;
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onChatSend(final AsyncPlayerChatEvent event) {
    event.setCancelled(true);
    if (ControllerPanicHelper.isInPanic()) {
      return;
    }

    final Player player = event.getPlayer();
    final boolean verified = this.plugin.getPlatformConfiguration().chatStatusType.getVerifier()
        .verify(player);
    if (!verified) {
      return;
    }

    this.plugin.getPlatformUserFactory()
        .findUserByUniqueId(player.getUniqueId()).ifPresent(platformUser -> {
          final PlatformUserCooldownCache cooldownCache = platformUser.getCooldownCache();
          if (cooldownCache.hasUserCooldown(PlatformUserCooldownType.CHAT) && !player.hasPermission(
              "platform-chat-bypass")) {
            ChatHelper.sendMessage(player,
                this.plugin.getPlatformConfiguration().messagesWrapper.chatMessageIsCooldowned.replace(
                    "{TIME}", TimeHelper.timeToString(
                        cooldownCache.getUserCooldown(PlatformUserCooldownType.CHAT))));
            return;
          }

          final String message = !player.hasPermission("platform-chat-colored") ? ChatColor.stripColor(
              ChatHelper.colored(event.getMessage())) : event.getMessage();
          if (message.trim().isEmpty()) {
            ChatHelper.sendMessage(player,
                this.plugin.getPlatformConfiguration().messagesWrapper.chatMessageCannotBeEmpty);
            return;
          }

          cooldownCache.putUserCooldown(PlatformUserCooldownType.CHAT);
          final Rank currentRank = platformUser.getRank().getCurrentRank();
          final List<UUID> uuidList = new ArrayList<>();
          for (final SectorUser user : SectorsPlugin.getInstance().getSectorUserFactory().getUserMap()
              .values()) {
            if (this.plugin.getPlatformUserFactory().findUserByUniqueId(user.getUniqueId())
                .map(PlatformUser::getChatSettings).filter(PlatformUserChatSettings::isGlobal)
                .isPresent()) {
              uuidList.add(user.getUniqueId());
            }
          }

          if (!uuidList.isEmpty()) {
            this.plugin.getRedisAdapter().sendPacket(new PlatformUserMessagePacket(uuidList,
                String.format(event.getFormat(), platformUser.getNickname(), message)
                    .replace("{CHAT_PREFIX}", currentRank.getChatPrefix())
                    .replace("{CHAT_SUFFIX}", currentRank.getChatSuffix())), "rhc_platform");
          }
        });
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onChatFormat(final AsyncPlayerChatEvent event) {
    event.setFormat(event.getPlayer().hasPermission("platform-chat-bypass")
        ? this.plugin.getPlatformConfiguration().messagesWrapper.chatFormatAdmin
        : this.plugin.getPlatformConfiguration().messagesWrapper.chatFormatPlayer);
  }
}
