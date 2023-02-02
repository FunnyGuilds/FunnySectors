package pl.rosehc.controller.packet;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.MasterController;
import pl.rosehc.controller.configuration.ConfigurationHelper;
import pl.rosehc.controller.configuration.impl.configuration.PlatformConfiguration;
import pl.rosehc.controller.configuration.impl.configuration.PlatformConfiguration.ProxyMotdWrapper;
import pl.rosehc.controller.packet.platform.PlatformChatStateChangePacket;
import pl.rosehc.controller.packet.platform.PlatformDropSettingsUpdateTurboDropPacket;
import pl.rosehc.controller.packet.platform.PlatformInitializationRequestPacket;
import pl.rosehc.controller.packet.platform.PlatformInitializationResponsePacket;
import pl.rosehc.controller.packet.platform.PlatformMotdSettingsSynchronizePacket;
import pl.rosehc.controller.packet.platform.PlatformSetFreezeStatePacket;
import pl.rosehc.controller.packet.platform.PlatformSetMotdCounterPlayerLimitPacket;
import pl.rosehc.controller.packet.platform.PlatformSetSlotsPacket;
import pl.rosehc.controller.packet.platform.PlatformSetSpawnPacket;
import pl.rosehc.controller.packet.platform.PlatformSpecialBossBarUpdatePacket;
import pl.rosehc.controller.packet.platform.ban.PlatformBanComputerUidUpdatePacket;
import pl.rosehc.controller.packet.platform.ban.PlatformBanCreatePacket;
import pl.rosehc.controller.packet.platform.ban.PlatformBanDeletePacket;
import pl.rosehc.controller.packet.platform.ban.PlatformBanIpUpdatePacket;
import pl.rosehc.controller.packet.platform.ban.PlatformBansRequestPacket;
import pl.rosehc.controller.packet.platform.ban.PlatformBansResponsePacket;
import pl.rosehc.controller.packet.platform.end.PlatformEndPortalPointCreatePacket;
import pl.rosehc.controller.packet.platform.end.PlatformEndPortalPointDeletePacket;
import pl.rosehc.controller.packet.platform.safe.PlatformSafeContentsModifyPacket;
import pl.rosehc.controller.packet.platform.safe.PlatformSafeCreatePacket;
import pl.rosehc.controller.packet.platform.safe.PlatformSafeDescriptionUpdatePacket;
import pl.rosehc.controller.packet.platform.safe.PlatformSafeLastOpenedTimeUpdatePacket;
import pl.rosehc.controller.packet.platform.safe.PlatformSafeModifierUpdatePacket;
import pl.rosehc.controller.packet.platform.safe.PlatformSafeOwnerUpdatePacket;
import pl.rosehc.controller.packet.platform.safe.request.PlatformSafeDataRequestPacket;
import pl.rosehc.controller.packet.platform.safe.request.PlatformSafeDataResponsePacket;
import pl.rosehc.controller.packet.platform.safe.request.PlatformSafeModificationRequestPacket;
import pl.rosehc.controller.packet.platform.safe.request.PlatformSafeModificationResponsePacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserAddDepositLimitsPacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserCombatTimeUpdatePacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserComputerUidUpdatePacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserCooldownSynchronizePacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserCreatePacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserDisableFirstJoinStatePacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserDropSettingsAddDisabledDropPacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserDropSettingsRemoveDisabledDropPacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserGodStateUpdatePacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserIgnoredPlayerUpdatePacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserLastPrivateMessageUpdatePacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserNicknameUpdatePacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserRankUpdatePacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserReceiveKitPacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserRemoveDepositLimitsPacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserSelectedDiscoEffectTypeUpdatePacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserSetHomePacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserSynchronizeChatSettingsPacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserSynchronizeSomeDropSettingsDataPacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserTeleportRequestUpdatePacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserVanishStateUpdatePacket;
import pl.rosehc.controller.packet.platform.user.PlatformUsersRequestPacket;
import pl.rosehc.controller.packet.platform.user.PlatformUsersResponsePacket;
import pl.rosehc.controller.packet.platform.whitelist.PlatformWhitelistChangeStatePacket;
import pl.rosehc.controller.packet.platform.whitelist.PlatformWhitelistSetReasonPacket;
import pl.rosehc.controller.packet.platform.whitelist.PlatformWhitelistUpdatePlayerPacket;
import pl.rosehc.controller.platform.ban.Ban;
import pl.rosehc.controller.platform.rank.RankEntry;
import pl.rosehc.controller.platform.safe.Safe;
import pl.rosehc.controller.platform.user.PlatformUser;
import pl.rosehc.controller.wrapper.platform.PlatformBanSerializableWrapper;
import pl.rosehc.controller.wrapper.platform.PlatformSafeSerializableWrapper;
import pl.rosehc.controller.wrapper.platform.PlatformUserSerializableWrapper;

public final class PlatformPacketHandler implements PacketHandler {

  private final MasterController masterController;

  public PlatformPacketHandler(final MasterController masterController) {
    this.masterController = masterController;
  }

  public void handle(final PlatformSafeModificationRequestPacket packet) {
    this.masterController.getSafeFactory().findSafe(packet.getUniqueId()).ifPresent(safe -> {
      final PlatformSafeModificationResponsePacket responsePacket = new PlatformSafeModificationResponsePacket();
      final UUID modifierUuid = safe.getModifierUuid();
      responsePacket.setCallbackId(packet.getCallbackId());
      responsePacket.setResponse(true);
      if (Objects.nonNull(modifierUuid)) {
        final PlatformConfiguration configuration = this.masterController.getConfigurationFactory()
            .findConfiguration(PlatformConfiguration.class);
        responsePacket.setResponseText(
            configuration.messagesWrapper.safeIsBeingModified.replace("{PLAYER_NAME}",
                this.masterController.getPlatformUserFactory().findUserByUniqueId(modifierUuid)
                    .map(PlatformUser::getNickname).orElse("Brak danych")));
        this.masterController.getRedisAdapter()
            .sendPacket(responsePacket, "rhc_platform_" + packet.getSectorName());
        return;
      }

      responsePacket.setSuccess(true);
      safe.setModifierUuid(packet.getModifierUniqueId());
      this.masterController.getRedisAdapter()
          .sendPacket(responsePacket, "rhc_platform_" + packet.getSectorName());
    });
  }

  public void handle(final PlatformUserCreatePacket packet) {
    if (!this.masterController.getPlatformUserFactory().findUserByUniqueId(packet.getUniqueId())
        .isPresent()) {
      try {
        final PlatformUser user = new PlatformUser(packet.getUniqueId(), packet.getNickname());
        user.setFirstJoin(true);
        this.masterController.getPlatformUserRepository().insert(user);
        this.masterController.getPlatformUserFactory().addUser(user);
      } catch (final SQLException ex) {
        System.err.println(
            "[PLATFORMA] Wystąpił niespodziewany problem podczas próby stworzenia użytkownika.");
        ex.printStackTrace();
      }
    }
  }

  public void handle(final PlatformUsersRequestPacket packet) {
    final List<PlatformUserSerializableWrapper> userList = new ArrayList<>();
    for (final PlatformUser user : this.masterController.getPlatformUserFactory().getUserMap()
        .values()) {
      userList.add(user.wrap());
    }

    final PlatformUsersResponsePacket responsePacket = new PlatformUsersResponsePacket(userList);
    responsePacket.setCallbackId(packet.getCallbackId());
    responsePacket.setResponse(true);
    responsePacket.setSuccess(true);
    this.masterController.getRedisAdapter()
        .sendPacket(responsePacket, "rhc_platform_" + packet.getFrom());
  }

  public void handle(final PlatformSafeDataRequestPacket packet) {
    final List<PlatformSafeSerializableWrapper> safeList = new ArrayList<>();
    for (final Safe safe : this.masterController.getSafeFactory().getSafeMap().values()) {
      safeList.add(safe.wrap());
    }

    final PlatformSafeDataResponsePacket responsePacket = new PlatformSafeDataResponsePacket(
        safeList);
    responsePacket.setCallbackId(packet.getCallbackId());
    responsePacket.setResponse(true);
    responsePacket.setSuccess(true);
    this.masterController.getRedisAdapter()
        .sendPacket(responsePacket, "rhc_platform_" + packet.getSectorName());
  }

  public void handle(final PlatformBansRequestPacket packet) {
    final List<PlatformBanSerializableWrapper> banList = new ArrayList<>();
    for (final Ban ban : this.masterController.getBanFactory().getBanMap().values()) {
      banList.add(ban.wrap());
    }

    final PlatformBansResponsePacket responsePacket = new PlatformBansResponsePacket(banList);
    responsePacket.setCallbackId(packet.getCallbackId());
    responsePacket.setResponse(true);
    responsePacket.setSuccess(true);
    this.masterController.getRedisAdapter()
        .sendPacket(responsePacket, "rhc_platform_" + packet.getProxyIdentifier());
  }

  public void handle(final PlatformSafeCreatePacket packet) {
    if (!this.masterController.getSafeFactory().findSafe(packet.getUniqueId()).isPresent()) {
      try {
        final Safe safe = new Safe(packet.getUniqueId(), packet.getOwnerUniqueId(),
            packet.getOwnerNickname(), packet.getCreationTime());
        this.masterController.getSafeRepository().insert(safe);
        this.masterController.getSafeFactory().addSafe(safe);
      } catch (final SQLException ex) {
        System.err.println(
            "[PLATFORMA] Wystąpił niespodziewany problem podczas próby stworzenia sejfu.");
        ex.printStackTrace();
      }
    }
  }

  public void handle(final PlatformBanCreatePacket packet) {
    if (!this.masterController.getBanFactory().findBan(packet.getPlayerNickname()).isPresent()) {
      try {
        final Ban ban = new Ban(packet.getPlayerNickname(), packet.getStaffNickname(),
            packet.getIp(), packet.getReason(), packet.getComputerUid(), packet.getCreationTime(),
            packet.getLeftTime());
        this.masterController.getBanFactory().addBan(ban);
        this.masterController.getBanRepository().insert(ban);
      } catch (final SQLException ex) {
        System.err.println(
            "[PLATFORMA] Wystąpił niespodziewany problem podczas próby stworzenia bana.");
        ex.printStackTrace();
      }
    }
  }

  public void handle(final PlatformUserSynchronizeChatSettingsPacket packet) {
    this.masterController.getPlatformUserFactory().findUserByUniqueId(packet.getUniqueId())
        .map(PlatformUser::getChatSettings).ifPresent(platformUserChatSettings -> {
          platformUserChatSettings.setGlobal(packet.isGlobal());
          platformUserChatSettings.setItemShop(packet.isItemShop());
          platformUserChatSettings.setKills(packet.isKills());
          platformUserChatSettings.setDeaths(packet.isDeaths());
          platformUserChatSettings.setCases(packet.isCases());
          platformUserChatSettings.setAchievements(packet.isAchievements());
          platformUserChatSettings.setRewards(packet.isRewards());
          platformUserChatSettings.setPrivateMessages(packet.isPrivateMessages());
        });
  }

  public void handle(final PlatformBanDeletePacket packet) {
    this.masterController.getBanFactory().findBan(packet.getPlayerNickname()).ifPresent(ban -> {
      try {
        this.masterController.getBanFactory().removeBan(ban);
        this.masterController.getBanRepository().delete(ban);
      } catch (final SQLException ex) {
        System.err.println(
            "[PLATFORMA] Wystąpił niespodziewany problem podczas próby usunięcia bana.");
        ex.printStackTrace();
      }
    });
  }

  public void handle(final PlatformUserSynchronizeSomeDropSettingsDataPacket packet) {
    this.masterController.getPlatformUserFactory().findUserByUniqueId(packet.getUniqueId())
        .map(PlatformUser::getDropSettings).ifPresent(platformUserDropSettings -> {
          platformUserDropSettings.setTurboDropMultiplier(packet.getTurboDropMultiplier());
          platformUserDropSettings.setTurboDropTime(packet.getTurboDropTime());
          platformUserDropSettings.setCobbleStone(packet.isCobbleStone());
          platformUserDropSettings.setCurrentXP(packet.getCurrentXP());
          platformUserDropSettings.setNeededXP(packet.getNeededXP());
          platformUserDropSettings.setLevel(packet.getLevel());
        });
  }

  public void handle(final PlatformInitializationRequestPacket packet) {
    final PlatformConfiguration platformConfiguration = this.masterController.getConfigurationFactory()
        .findConfiguration(PlatformConfiguration.class);
    final PlatformInitializationResponsePacket responsePacket = new PlatformInitializationResponsePacket(
        ConfigurationHelper.serializeConfiguration(platformConfiguration));
    responsePacket.setCallbackId(packet.getCallbackId());
    responsePacket.setResponse(true);
    responsePacket.setSuccess(true);
    this.masterController.getRedisAdapter()
        .sendPacket(responsePacket, "rhc_platform_" + packet.getFrom());
  }

  public void handle(final PlatformSetSlotsPacket packet) {
    final PlatformConfiguration platformConfiguration = this.masterController.getConfigurationFactory()
        .findConfiguration(PlatformConfiguration.class);
    if (packet.isProxy()) {
      platformConfiguration.slotWrapper.proxySlots = packet.getSlots();
    } else {
      platformConfiguration.slotWrapper.spigotSlots = packet.getSlots();
    }

    ConfigurationHelper.saveConfiguration(platformConfiguration);
  }

  public void handle(final PlatformMotdSettingsSynchronizePacket packet) {
    final PlatformConfiguration platformConfiguration = this.masterController.getConfigurationFactory()
        .findConfiguration(PlatformConfiguration.class);
    final ProxyMotdWrapper proxyMotdWrapper = platformConfiguration.proxyMotdWrapper;
    proxyMotdWrapper.firstLine = packet.getFirstLine();
    proxyMotdWrapper.secondLine = packet.getSecondLine();
    proxyMotdWrapper.thirdLine = packet.getThirdLine();
    proxyMotdWrapper.thirdLineSpacing = packet.getThirdLineSpacing();
    ConfigurationHelper.saveConfiguration(platformConfiguration);
  }

  public void handle(final PlatformWhitelistUpdatePlayerPacket packet) {
    final PlatformConfiguration platformConfiguration = this.masterController.getConfigurationFactory()
        .findConfiguration(PlatformConfiguration.class);
    platformConfiguration.proxyWhitelistWrapper.players.remove(packet.getPlayerName());
    if (packet.isAdd()) {
      platformConfiguration.proxyWhitelistWrapper.players.add(packet.getPlayerName());
    }

    ConfigurationHelper.saveConfiguration(platformConfiguration);
  }

  public void handle(final PlatformUserIgnoredPlayerUpdatePacket packet) {
    this.masterController.getPlatformUserFactory().findUserByUniqueId(packet.getSenderUniqueId())
        .ifPresent(user -> {
          if (packet.isAdd()) {
            user.addIgnoredPlayer(packet.getTargetUniqueId());
          } else {
            user.removeIgnoredPlayer(packet.getTargetUniqueId());
          }
        });
  }

  public void handle(final PlatformUserTeleportRequestUpdatePacket packet) {
    this.masterController.getPlatformUserFactory().findUserByUniqueId(packet.getTargetUniqueId())
        .ifPresent(user -> {
          if (packet.isRemove()) {
            user.removeTeleportRequest(packet.getFromUniqueId());
          } else {
            user.addTeleportRequest(packet.getFromUniqueId());
          }
        });
  }

  public void handle(final PlatformUserRankUpdatePacket packet) {
    this.masterController.getPlatformUserFactory().findUserByUniqueId(packet.getUniqueId())
        .ifPresent(user -> user.setRank(RankEntry.create(
            packet.getPreviousRankName() != null ? this.masterController.getRankFactory()
                .findRank(packet.getPreviousRankName()).orElse(null) : null,
            this.masterController.getRankFactory().findRank(packet.getCurrentRankName())
                .orElse(this.masterController.getRankFactory().getDefaultRank()),
            packet.getExpirationTime())));
  }

  public void handle(final PlatformUserLastPrivateMessageUpdatePacket packet) {
    this.masterController.getPlatformUserFactory().findUserByUniqueId(packet.getFromUniqueId())
        .ifPresent(fromUser -> this.masterController.getPlatformUserFactory()
            .findUserByUniqueId(packet.getTargetUniqueId()).ifPresent(targetUser -> {
              fromUser.setLastPrivateMessage(targetUser.getUniqueId());
              targetUser.setLastPrivateMessage(fromUser.getUniqueId());
            }));
  }

  public void handle(final PlatformEndPortalPointCreatePacket packet) {
    final PlatformConfiguration platformConfiguration = this.masterController.getConfigurationFactory()
        .findConfiguration(PlatformConfiguration.class);
    if (!platformConfiguration.endPortalPointWrapperList.contains(packet.getWrapper())) {
      platformConfiguration.endPortalPointWrapperList.add(packet.getWrapper());
      ConfigurationHelper.saveConfiguration(platformConfiguration);
    }
  }

  public void handle(final PlatformEndPortalPointDeletePacket packet) {
    final PlatformConfiguration platformConfiguration = this.masterController.getConfigurationFactory()
        .findConfiguration(PlatformConfiguration.class);
    if (packet.getPointId() < platformConfiguration.endPortalPointWrapperList.size()) {
      platformConfiguration.endPortalPointWrapperList.remove(packet.getPointId());
      ConfigurationHelper.saveConfiguration(platformConfiguration);
    }
  }

  public void handle(final PlatformDropSettingsUpdateTurboDropPacket packet) {
    final PlatformConfiguration platformConfiguration = this.masterController.getConfigurationFactory()
        .findConfiguration(PlatformConfiguration.class);
    platformConfiguration.dropSettingsWrapper.turboDropMultiplier = packet.getTurboDropMultiplier();
    platformConfiguration.dropSettingsWrapper.turboDropTime = packet.getTurboDropTime();
    ConfigurationHelper.saveConfiguration(platformConfiguration);
  }

  public void handle(final PlatformSetSpawnPacket packet) {
    final PlatformConfiguration platformConfiguration = this.masterController.getConfigurationFactory()
        .findConfiguration(PlatformConfiguration.class);
    platformConfiguration.spawnLocationWrapper = packet.getSpawnLocationWrapper();
    ConfigurationHelper.saveConfiguration(platformConfiguration);
  }

  public void handle(final PlatformSpecialBossBarUpdatePacket packet) {
    final PlatformConfiguration platformConfiguration = this.masterController.getConfigurationFactory()
        .findConfiguration(PlatformConfiguration.class);
    platformConfiguration.specialBossBarWrapper = packet.getSpecialBossBarWrapper();
    ConfigurationHelper.saveConfiguration(platformConfiguration);
  }

  public void handle(final PlatformChatStateChangePacket packet) {
    final PlatformConfiguration platformConfiguration = this.masterController.getConfigurationFactory()
        .findConfiguration(PlatformConfiguration.class);
    platformConfiguration.chatStatusType = packet.getType();
    ConfigurationHelper.saveConfiguration(platformConfiguration);
  }

  public void handle(final PlatformSetMotdCounterPlayerLimitPacket packet) {
    final PlatformConfiguration platformConfiguration = this.masterController.getConfigurationFactory()
        .findConfiguration(PlatformConfiguration.class);
    platformConfiguration.proxyMotdWrapper.counterPlayersLimit = packet.getLimit();
    ConfigurationHelper.saveConfiguration(platformConfiguration);
  }

  public void handle(final PlatformWhitelistChangeStatePacket packet) {
    final PlatformConfiguration platformConfiguration = this.masterController.getConfigurationFactory()
        .findConfiguration(PlatformConfiguration.class);
    platformConfiguration.proxyWhitelistWrapper.enabled = packet.getState();
    ConfigurationHelper.saveConfiguration(platformConfiguration);
  }

  public void handle(final PlatformWhitelistSetReasonPacket packet) {
    final PlatformConfiguration platformConfiguration = this.masterController.getConfigurationFactory()
        .findConfiguration(PlatformConfiguration.class);
    platformConfiguration.proxyWhitelistWrapper.reason = packet.getReason();
    ConfigurationHelper.saveConfiguration(platformConfiguration);
  }

  public void handle(final PlatformSetFreezeStatePacket packet) {
    final PlatformConfiguration platformConfiguration = this.masterController.getConfigurationFactory()
        .findConfiguration(PlatformConfiguration.class);
    platformConfiguration.serverFreezeState = packet.getState();
    ConfigurationHelper.saveConfiguration(platformConfiguration);
  }

  public void handle(final PlatformSafeContentsModifyPacket packet) {
    this.masterController.getSafeFactory().findSafe(packet.getUniqueId()).ifPresent(safe -> {
      safe.setContents(packet.getContents());
      safe.setModifierUuid(null);
    });
  }

  public void handle(final PlatformSafeOwnerUpdatePacket packet) {
    this.masterController.getSafeFactory().findSafe(packet.getUniqueId()).ifPresent(safe -> {
      safe.setOwnerUniqueId(packet.getOwnerUniqueId());
      safe.setOwnerNickname(packet.getOwnerNickname());
    });
  }

  public void handle(final PlatformUserCooldownSynchronizePacket packet) {
    this.masterController.getPlatformUserFactory().findUserByUniqueId(packet.getUniqueId())
        .ifPresent(
            user -> user.getCooldownCache().putUserCooldown(packet.getType(), packet.getTime()));
  }

  public void handle(final PlatformUserDropSettingsRemoveDisabledDropPacket packet) {
    this.masterController.getPlatformUserFactory().findUserByUniqueId(packet.getUniqueId())
        .ifPresent(
            user -> user.getDropSettings().getDisabledDropSet().remove(packet.getDropName()));
  }

  public void handle(final PlatformUserSelectedDiscoEffectTypeUpdatePacket packet) {
    this.masterController.getPlatformUserFactory().findUserByUniqueId(packet.getUniqueId())
        .ifPresent(
            user -> user.setSelectedDiscoEffectTypeName(packet.getSelectedDiscoEffectTypeName()));
  }

  public void handle(final PlatformUserDropSettingsAddDisabledDropPacket packet) {
    this.masterController.getPlatformUserFactory().findUserByUniqueId(packet.getUniqueId())
        .ifPresent(user -> user.getDropSettings().getDisabledDropSet().add(packet.getDropName()));
  }

  public void handle(final PlatformUserAddDepositLimitsPacket packet) {
    this.masterController.getPlatformUserFactory().findUserByUniqueId(packet.getUniqueId())
        .ifPresent(user -> packet.getAddedDepositLimitMap().forEach(user::addItemToDeposit));
  }

  public void handle(final PlatformUserRemoveDepositLimitsPacket packet) {
    this.masterController.getPlatformUserFactory().findUserByUniqueId(packet.getUniqueId())
        .ifPresent(user -> packet.getRemovedDepositLimitMap().forEach(user::removeItemFromDeposit));
  }

  public void handle(final PlatformUserReceiveKitPacket packet) {
    this.masterController.getPlatformUserFactory().findUserByUniqueId(packet.getUniqueId())
        .ifPresent(user -> user.receiveKit(packet.getKitName(), packet.getKitTime()));
  }

  public void handle(final PlatformUserSetHomePacket packet) {
    this.masterController.getPlatformUserFactory().findUserByUniqueId(packet.getUniqueId())
        .ifPresent(user -> user.setHome(packet.getHomeLocation(), packet.getHomeId()));
  }

  public void handle(final PlatformUserCombatTimeUpdatePacket packet) {
    this.masterController.getPlatformUserFactory().findUserByUniqueId(packet.getUniqueId())
        .ifPresent(user -> user.setCombatTime(packet.getCombatTime()));
  }

  public void handle(final PlatformSafeDescriptionUpdatePacket packet) {
    this.masterController.getSafeFactory().findSafe(packet.getUniqueId())
        .ifPresent(safe -> safe.setDescription(packet.getDescription()));
  }

  public void handle(final PlatformSafeModifierUpdatePacket packet) {
    this.masterController.getSafeFactory().findSafe(packet.getUniqueId())
        .ifPresent(safe -> safe.setModifierUuid(packet.getModifierUniqueId()));
  }

  public void handle(final PlatformSafeLastOpenedTimeUpdatePacket packet) {
    this.masterController.getSafeFactory().findSafe(packet.getUniqueId())
        .ifPresent(safe -> safe.setLastOpenedTime(packet.getLastOpenedTime()));
  }

  public void handle(final PlatformBanComputerUidUpdatePacket packet) {
    this.masterController.getBanFactory().findBan(packet.getPlayerNickname())
        .ifPresent(ban -> ban.setComputerUid(packet.getComputerUid()));
  }

  public void handle(final PlatformBanIpUpdatePacket packet) {
    this.masterController.getBanFactory().findBan(packet.getPlayerNickname())
        .ifPresent(ban -> ban.setIp(packet.getIp()));
  }

  public void handle(final PlatformUserComputerUidUpdatePacket packet) {
    this.masterController.getPlatformUserFactory().findUserByUniqueId(packet.getUniqueId())
        .ifPresent(user -> user.setComputerUid(packet.getComputerUid()));
  }

  public void handle(final PlatformUserNicknameUpdatePacket packet) {
    this.masterController.getPlatformUserFactory().findUserByUniqueId(packet.getUniqueId())
        .ifPresent(user -> user.setNickname(packet.getNickname()));
  }

  public void handle(final PlatformUserVanishStateUpdatePacket packet) {
    this.masterController.getPlatformUserFactory().findUserByUniqueId(packet.getUniqueId())
        .ifPresent(user -> user.setVanish(packet.getState()));
  }

  public void handle(final PlatformUserGodStateUpdatePacket packet) {
    this.masterController.getPlatformUserFactory().findUserByUniqueId(packet.getUniqueId())
        .ifPresent(user -> user.setGod(packet.getState()));
  }

  public void handle(final PlatformUserDisableFirstJoinStatePacket packet) {
    this.masterController.getPlatformUserFactory().findUserByUniqueId(packet.getUniqueId())
        .ifPresent(user -> user.setFirstJoin(false));
  }
}
