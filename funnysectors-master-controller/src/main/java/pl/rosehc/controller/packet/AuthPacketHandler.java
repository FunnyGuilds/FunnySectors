package pl.rosehc.controller.packet;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.MasterController;
import pl.rosehc.controller.auth.AuthUser;
import pl.rosehc.controller.configuration.ConfigurationHelper;
import pl.rosehc.controller.configuration.impl.configuration.AuthConfiguration;
import pl.rosehc.controller.packet.auth.AuthInitializationRequestPacket;
import pl.rosehc.controller.packet.auth.AuthInitializationResponsePacket;
import pl.rosehc.controller.packet.auth.user.AuthUserCreatePacket;
import pl.rosehc.controller.packet.auth.user.AuthUserDeletePacket;
import pl.rosehc.controller.packet.auth.user.AuthUserLastIPUpdatePacket;
import pl.rosehc.controller.packet.auth.user.AuthUserLastOnlineUpdatePacket;
import pl.rosehc.controller.packet.auth.user.AuthUserMarkRegisteredPacket;
import pl.rosehc.controller.packet.auth.user.AuthUserPasswordUpdatePacket;
import pl.rosehc.controller.packet.auth.user.AuthUserSetPremiumStatePacket;
import pl.rosehc.controller.wrapper.auth.AuthUserSerializableWrapper;

public final class AuthPacketHandler implements PacketHandler {

  private final MasterController masterController;

  public AuthPacketHandler(final MasterController masterController) {
    this.masterController = masterController;
  }

  public void handle(final AuthInitializationRequestPacket packet) {
    final List<AuthUserSerializableWrapper> userList = new ArrayList<>();
    for (final AuthUser user : this.masterController.getAuthUserFactory().getUserMap().values()) {
      userList.add(user.wrap());
    }

    final AuthConfiguration authConfiguration = this.masterController.getConfigurationFactory()
        .findConfiguration(AuthConfiguration.class);
    final AuthInitializationResponsePacket responsePacket = new AuthInitializationResponsePacket(
        userList, ConfigurationHelper.serializeConfiguration(authConfiguration));
    responsePacket.setCallbackId(packet.getCallbackId());
    responsePacket.setResponse(true);
    responsePacket.setSuccess(true);
    this.masterController.getRedisAdapter()
        .sendPacket(responsePacket, "rhc_auth_" + packet.getProxyIdentifier());
  }

  public void handle(final AuthUserCreatePacket packet) {
    if (!this.masterController.getAuthUserFactory().findUser(packet.getNickname()).isPresent()) {
      try {
        final AuthUser user = new AuthUser(packet.getNickname(), packet.getLastIP(),
            packet.getFirstJoinTime(), packet.getLastOnlineTime(), packet.isPremium());
        user.setRegistered(packet.isRegistered());
        user.setPassword(packet.getPassword());
        this.masterController.getAuthUserFactory().addUser(user);
        this.masterController.getAuthUserRepository().insert(user);
      } catch (final SQLException ex) {
        System.err.println(
            "[LOGOWANIE] Wystąpił niespodziewany problem podczas próby stworzenia użytkownika.");
        ex.printStackTrace();
      }
    }
  }

  public void handle(final AuthUserDeletePacket packet) {
    this.masterController.getAuthUserFactory().findUser(packet.getNickname()).ifPresent(user -> {
      this.masterController.getAuthUserFactory().removeUser(user);
      try {
        this.masterController.getAuthUserRepository().delete(user);
      } catch (final SQLException ex) {
        System.err.println(
            "[LOGOWANIE] Wystąpił niespodziewany problem podczas próby usunięcia gracza z bazy danych.");
        ex.printStackTrace();
      }
    });
  }

  public void handle(final AuthUserPasswordUpdatePacket packet) {
    this.masterController.getAuthUserFactory().findUser(packet.getNickname())
        .ifPresent(user -> user.setPassword(packet.getPassword()));
  }

  public void handle(final AuthUserLastIPUpdatePacket packet) {
    this.masterController.getAuthUserFactory().findUser(packet.getNickname())
        .ifPresent(user -> user.setLastIP(packet.getLastIp()));
  }

  public void handle(final AuthUserLastOnlineUpdatePacket packet) {
    this.masterController.getAuthUserFactory().findUser(packet.getNickname())
        .ifPresent(user -> user.setLastOnlineTime(packet.getLastOnlineTime()));
  }

  public void handle(final AuthUserSetPremiumStatePacket packet) {
    this.masterController.getAuthUserFactory().findUser(packet.getNickname())
        .ifPresent(user -> user.setPremium(packet.getState()));
  }

  public void handle(final AuthUserMarkRegisteredPacket packet) {
    this.masterController.getAuthUserFactory().findUser(packet.getNickname())
        .ifPresent(user -> user.setRegistered(true));
  }
}
