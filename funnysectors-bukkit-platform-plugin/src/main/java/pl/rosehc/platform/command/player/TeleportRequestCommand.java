package pl.rosehc.platform.command.player;

import java.util.Collections;
import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Name;
import me.vaperion.blade.annotation.Sender;
import me.vaperion.blade.exception.BladeExitMessage;
import org.bukkit.entity.Player;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.controller.packet.platform.user.PlatformUserMessagePacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserTeleportRequestUpdatePacket;
import pl.rosehc.platform.PlatformPlugin;
import pl.rosehc.platform.packet.player.PlayerSelfTeleportPacket;
import pl.rosehc.platform.user.PlatformUser;
import pl.rosehc.sectors.SectorsPlugin;

public final class TeleportRequestCommand {

  private final PlatformPlugin plugin;

  public TeleportRequestCommand(final PlatformPlugin plugin) {
    this.plugin = plugin;
  }

  @Command(value = {"teleportacacept",
      "tpaccept"}, description = "Akceptuje prośbę o teleportację od graczy(a).")
  public void handleTeleportAccept(final @Sender Player player,
      final @Name("player/*") String targetName) {
    if (targetName.equals("*")) {
      boolean anyAccepted = false;
      for (final PlatformUser targetUser : this.plugin.getPlatformUserFactory().getUserMap()
          .values()) {
        if (targetUser.hasTeleportRequest(player.getUniqueId())) {
          this.acceptTeleportRequest(player, targetUser);
          anyAccepted = true;
        }
      }

      if (!anyAccepted) {
        throw new BladeExitMessage(ChatHelper.colored(
            this.plugin.getPlatformConfiguration().messagesWrapper.teleportRequestsNotFound));
      }

      ChatHelper.sendMessage(player,
          this.plugin.getPlatformConfiguration().messagesWrapper.teleportRequestSuccessfullyAcceptedAll);
      return;
    }

    final PlatformUser targetUser = this.plugin.getPlatformUserFactory()
        .findUserByNickname(targetName).orElseThrow(() -> new BladeExitMessage(ChatHelper.colored(
            this.plugin.getPlatformConfiguration().messagesWrapper.playerNotFound.replace(
                "{PLAYER_NAME}", targetName))));
    if (targetUser.getUniqueId().equals(player.getUniqueId())) {
      throw new BladeExitMessage(ChatHelper.colored(
          this.plugin.getPlatformConfiguration().messagesWrapper.cannotExecuteThisActionOnYourself));
    }

    if (!targetUser.isOnline()) {
      throw new BladeExitMessage(ChatHelper.colored(
          this.plugin.getPlatformConfiguration().messagesWrapper.playerIsOffline.replace(
              "{PLAYER_NAME}", targetUser.getNickname())));
    }

    if (!targetUser.hasTeleportRequest(player.getUniqueId())) {
      throw new BladeExitMessage(ChatHelper.colored(
          this.plugin.getPlatformConfiguration().messagesWrapper.teleportRequestNotSent));
    }

    this.acceptTeleportRequest(player, targetUser);
    ChatHelper.sendMessage(player,
        this.plugin.getPlatformConfiguration().messagesWrapper.teleportRequestSuccessfullyAcceptedOne.replace(
            "{PLAYER_NAME}", targetUser.getNickname()));
  }

  @Command(value = {"teleportrequest", "tprequest",
      "tpa"}, description = "Wysyła prośbę o teleportację do gracza.")
  public void handleTeleportRequest(final @Sender Player player,
      final @Name("player") PlatformUser targetUser) {
    if (targetUser.getUniqueId().equals(player.getUniqueId())) {
      throw new BladeExitMessage(ChatHelper.colored(
          this.plugin.getPlatformConfiguration().messagesWrapper.cannotExecuteThisActionOnYourself));
    }

    if (!targetUser.isOnline()) {
      throw new BladeExitMessage(ChatHelper.colored(
          this.plugin.getPlatformConfiguration().messagesWrapper.playerIsOffline.replace(
              "{PLAYER_NAME}", targetUser.getNickname())));
    }

    final PlatformUser playerUser = this.plugin.getPlatformUserFactory()
        .findUserByUniqueId(player.getUniqueId()).orElseThrow(() -> new BladeExitMessage(
            ChatHelper.colored(
                this.plugin.getPlatformConfiguration().messagesWrapper.playerNotFound.replace(
                    "{PLAYER_NAME}", player.getName()))));
    if (playerUser.hasTeleportRequest(targetUser.getUniqueId())) {
      throw new BladeExitMessage(ChatHelper.colored(
          this.plugin.getPlatformConfiguration().messagesWrapper.teleportRequestAlreadySent));
    }

    playerUser.addTeleportRequest(targetUser.getUniqueId());
    targetUser.sendMessage(
        this.plugin.getPlatformConfiguration().messagesWrapper.teleportRequestSuccessfullySentTarget.replace(
            "{PLAYER_NAME}", player.getName()));
    ChatHelper.sendMessage(player,
        this.plugin.getPlatformConfiguration().messagesWrapper.teleportRequestSuccessfullySentPlayer.replace(
            "{PLAYER_NAME}", targetUser.getNickname()));
    this.plugin.getRedisAdapter().sendPacket(
        new PlatformUserTeleportRequestUpdatePacket(targetUser.getUniqueId(), player.getUniqueId(),
            false), "rhc_master_controller", "rhc_platform");
  }

  @Command(value = {"teleportdeny",
      "tpdeny"}, description = "Odrzuca prośbę o teleportację od danego gracza.")
  public void handleTeleportDeny(final @Sender Player player,
      final @Name("player") PlatformUser targetUser) {
    if (targetUser.getUniqueId().equals(player.getUniqueId())) {
      throw new BladeExitMessage(ChatHelper.colored(
          this.plugin.getPlatformConfiguration().messagesWrapper.cannotExecuteThisActionOnYourself));
    }

    if (!targetUser.isOnline()) {
      throw new BladeExitMessage(ChatHelper.colored(
          this.plugin.getPlatformConfiguration().messagesWrapper.playerIsOffline.replace(
              "{PLAYER_NAME}", targetUser.getNickname())));
    }

    if (!targetUser.hasTeleportRequest(player.getUniqueId())) {
      throw new BladeExitMessage(ChatHelper.colored(
          this.plugin.getPlatformConfiguration().messagesWrapper.teleportRequestNotSent));
    }

    targetUser.removeTeleportRequest(player.getUniqueId());
    targetUser.sendMessage(
        this.plugin.getPlatformConfiguration().messagesWrapper.teleportRequestSuccessfullyDeniedPlayer.replace(
            "{PLAYER_NAME}", targetUser.getNickname()));
    ChatHelper.sendMessage(player,
        this.plugin.getPlatformConfiguration().messagesWrapper.teleportRequestSuccessfullyDeniedTarget.replace(
            "{PLAYER_NAME}", player.getName()));
    this.plugin.getRedisAdapter().sendPacket(
        new PlatformUserTeleportRequestUpdatePacket(player.getUniqueId(), targetUser.getUniqueId(),
            true), "rhc_master_controller", "rhc_platform");
  }

  private void acceptTeleportRequest(final Player player, final PlatformUser targetUser) {
    SectorsPlugin.getInstance().getSectorUserFactory().findUserByUniqueId(targetUser.getUniqueId())
        .ifPresent(targetSectorUser -> {
          targetUser.removeTeleportRequest(player.getUniqueId());
          this.plugin.getRedisAdapter().sendPacket(
              new PlatformUserTeleportRequestUpdatePacket(player.getUniqueId(),
                  targetUser.getUniqueId(), true), "rhc_master_controller", "rhc_platform");
          this.plugin.getRedisAdapter().sendPacket(
              new PlayerSelfTeleportPacket(player.getUniqueId(), targetUser.getUniqueId(), true),
              "rhc_platform_" + targetSectorUser.getSector().getName());
          this.plugin.getRedisAdapter().sendPacket(
              new PlatformUserMessagePacket(Collections.singletonList(targetUser.getUniqueId()),
                  this.plugin.getPlatformConfiguration().messagesWrapper.teleportRequestSuccessfullyAcceptedTarget.replace(
                      "{PLAYER_NAME}", player.getName())),
              "rhc_platform_" + targetSectorUser.getSector().getName());
        });
  }
}
