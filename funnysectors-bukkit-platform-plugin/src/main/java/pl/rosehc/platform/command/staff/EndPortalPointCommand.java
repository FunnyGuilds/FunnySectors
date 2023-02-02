package pl.rosehc.platform.command.staff;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Name;
import me.vaperion.blade.annotation.Permission;
import me.vaperion.blade.annotation.Sender;
import me.vaperion.blade.exception.BladeExitMessage;
import org.bukkit.entity.Player;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.controller.packet.platform.end.PlatformEndPortalPointCreatePacket;
import pl.rosehc.controller.packet.platform.end.PlatformEndPortalPointDeletePacket;
import pl.rosehc.controller.wrapper.platform.PlatformEndPortalPointWrapper;
import pl.rosehc.platform.PlatformPlugin;
import pl.rosehc.platform.end.EndHelper;
import pl.rosehc.platform.end.session.EndPortalPointEditingSession;
import pl.rosehc.platform.end.session.EndPortalPointEditingSessionStatus;

public final class EndPortalPointCommand {

  private final PlatformPlugin plugin;

  public EndPortalPointCommand(final PlatformPlugin plugin) {
    this.plugin = plugin;
  }

  @Permission("platform-command-portalpoint")
  @Command(value = "portalpoint setpoint", description = "Ustawia punkt portalu w aktualnej sesji")
  public void handlePortalPointSetPoint(final @Sender Player player) {
    final EndPortalPointEditingSession session = this.plugin.getEndPortalPointEditingSessionFactory()
        .findSession(player.getUniqueId()).orElseThrow(() -> new BladeExitMessage(
            ChatHelper.colored(
                this.plugin.getPlatformConfiguration().messagesWrapper.endPortalPointEditingSessionIsNotActive)));
    if (EndHelper.checkPortalPoint(player.getLocation()) == 1) {
      throw new BladeExitMessage(ChatHelper.colored(
          this.plugin.getPlatformConfiguration().messagesWrapper.endPortalPointCannotBeSetHere));
    }

    final EndPortalPointEditingSessionStatus status = session.createPoint(player.getLocation());
    if (!status.equals(EndPortalPointEditingSessionStatus.FIRST_POINT_SUCCESSFULLY_SET)
        && !status.equals(EndPortalPointEditingSessionStatus.SECOND_POINT_SUCCESSFULLY_SET)) {
      throw new BladeExitMessage(ChatHelper.colored(
          status.equals(EndPortalPointEditingSessionStatus.SECOND_POINT_ALREADY_SET)
              ? this.plugin.getPlatformConfiguration().messagesWrapper.endPortalPointEditingSecondPointIsAlreadySet
              : this.plugin.getPlatformConfiguration().messagesWrapper.endPortalPointEditingFirstPointIsAlreadySet));
    }

    ChatHelper.sendMessage(player,
        status.equals(EndPortalPointEditingSessionStatus.SECOND_POINT_SUCCESSFULLY_SET)
            ? this.plugin.getPlatformConfiguration().messagesWrapper.endPortalPointEditingSecondPointHasBeenSuccessfullySet
            : this.plugin.getPlatformConfiguration().messagesWrapper.endPortalPointEditingFirstPointHasBeenSuccessfullySet);
  }

  @Permission("platform-command-portalpoint")
  @Command(value = "portalpoint edit stop", description = "Kończy sesję od edytowania punktu portalu")
  public void handlePortalPointEditStop(final @Sender Player player) {
    final EndPortalPointEditingSession session = this.plugin.getEndPortalPointEditingSessionFactory()
        .findSession(player.getUniqueId()).orElseThrow(() -> new BladeExitMessage(
            ChatHelper.colored(
                this.plugin.getPlatformConfiguration().messagesWrapper.endPortalPointEditingSessionIsNotActive)));
    this.plugin.getEndPortalPointEditingSessionFactory().removeSession(session);
    if (!session.arePointsSet()) {
      throw new BladeExitMessage(ChatHelper.colored(
          this.plugin.getPlatformConfiguration().messagesWrapper.endPortalPointEditingSessionPointsAreNotSet));
    }

    ChatHelper.sendMessage(player,
        this.plugin.getPlatformConfiguration().messagesWrapper.endPortalPointEditingSessionHasBeenSuccessfullyEnded);
    final PlatformEndPortalPointWrapper wrapper = session.wrap();
    this.plugin.getPlatformConfiguration().endPortalPointWrapperList.add(wrapper);
    this.plugin.getRedisAdapter()
        .sendPacket(new PlatformEndPortalPointCreatePacket(wrapper), "rhc_master_controller",
            "rhc_platform");
  }

  @Permission("platform-command-portalpoint")
  @Command(value = "portalpoint delete", description = "Usuwa dany punkt portalu z listy")
  public void handlePortalPointDelete(final @Sender Player player, final @Name("id") int id) {
    if (id >= this.plugin.getPlatformConfiguration().endPortalPointWrapperList.size()) {
      throw new BladeExitMessage(ChatHelper.colored(
          this.plugin.getPlatformConfiguration().messagesWrapper.endPortalPointDoesNotExists.replace(
              "{IDENTIFIER}", String.valueOf(id))));
    }

    ChatHelper.sendMessage(player,
        this.plugin.getPlatformConfiguration().messagesWrapper.endPortalPointHasBeenSuccessfullyDeleted.replace(
            "{IDENTIFIER}", String.valueOf(id)));
    this.plugin.getPlatformConfiguration().endPortalPointWrapperList.remove(id);
    this.plugin.getRedisAdapter()
        .sendPacket(new PlatformEndPortalPointDeletePacket(id), "rhc_master_controller",
            "rhc_platform");
  }

  @Permission("platform-command-portalpoint")
  @Command(value = "portalpoint edit start", description = "Tworzy sesję edytowania punktu portalu")
  public void handlePortalPointEditStart(final @Sender Player player) {
    if (this.plugin.getEndPortalPointEditingSessionFactory().findSession(player.getUniqueId())
        .isPresent()) {
      throw new BladeExitMessage(ChatHelper.colored(
          this.plugin.getPlatformConfiguration().messagesWrapper.endPortalPointEditingSessionIsAlreadyActive));
    }

    final EndPortalPointEditingSession session = new EndPortalPointEditingSession(
        player.getUniqueId());
    this.plugin.getEndPortalPointEditingSessionFactory().addSession(session);
    ChatHelper.sendMessage(player,
        this.plugin.getPlatformConfiguration().messagesWrapper.endPortalPointEditingSessionHasBeenActivated);
  }

  @Permission("platform-command-portalpoint")
  @Command(value = {"portalpoint",
      "portalpoint edit"}, description = "Wyświetla użycie komendy od punktów portali")
  public void handlePortalPointUsage(final @Sender Player player) {
    ChatHelper.sendMessage(player,
        this.plugin.getPlatformConfiguration().messagesWrapper.endPortalPointCommandUsage);
  }
}
