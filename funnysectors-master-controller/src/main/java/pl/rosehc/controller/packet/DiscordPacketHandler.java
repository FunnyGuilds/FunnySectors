package pl.rosehc.controller.packet;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.MasterController;
import pl.rosehc.controller.configuration.impl.configuration.PlatformConfiguration;
import pl.rosehc.controller.configuration.impl.configuration.PlatformConfiguration.MessagesWrapper;
import pl.rosehc.controller.packet.discord.DiscordRewardGivePacket;
import pl.rosehc.controller.packet.discord.DiscordRewardVerificationRequestPacket;
import pl.rosehc.controller.packet.discord.DiscordRewardVerificationResponsePacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserDiscordRewardStateUpdatePacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserMessagePacket;
import pl.rosehc.controller.platform.user.PlatformUser;
import pl.rosehc.controller.platform.user.subdata.PlatformUserChatSettings;
import pl.rosehc.controller.platform.user.subdata.PlatformUserRewardSettings;
import pl.rosehc.controller.sector.user.SectorUser;

public final class DiscordPacketHandler implements PacketHandler {

  private final MasterController masterController;

  public DiscordPacketHandler(final MasterController masterController) {
    this.masterController = masterController;
  }

  public void handle(final DiscordRewardVerificationRequestPacket packet) {
    final DiscordRewardVerificationResponsePacket responsePacket = new DiscordRewardVerificationResponsePacket();
    responsePacket.setCallbackId(packet.getCallbackId());
    responsePacket.setResponse(true);
    for (final PlatformUser user : this.masterController.getPlatformUserFactory().getUserMap()
        .values()) {
      if (user.getRewardSettings().getDiscordUserId() == packet.getUserId()
          && user.getRewardSettings().isDiscordRewardReceived()) {
        responsePacket.setResponseText("user_reward_already_received");
        this.masterController.getRedisAdapter().sendPacket(responsePacket, "rhc_discord_rewards");
        return;
      }
    }

    this.masterController.getPlatformUserFactory().findUserByNickname(packet.getNickname())
        .ifPresent(user -> {
          final PlatformUserRewardSettings rewardSettings = user.getRewardSettings();
          if (rewardSettings.isDiscordRewardReceived()) {
            responsePacket.setResponseText("user_reward_already_received");
            this.masterController.getRedisAdapter()
                .sendPacket(responsePacket, "rhc_discord_rewards");
            return;
          }

          final Optional<SectorUser> sectorUserOptional = this.masterController.getSectorUserFactory()
              .findUserByUniqueId(user.getUniqueId());
          if (!sectorUserOptional.isPresent()) {
            responsePacket.setResponseText("user_is_offline");
            this.masterController.getRedisAdapter()
                .sendPacket(responsePacket, "rhc_discord_rewards");
            return;
          }

          final SectorUser sectorUser = sectorUserOptional.get();
          rewardSettings.setDiscordUserId(packet.getUserId());
          rewardSettings.setDiscordRewardReceived(true);
          responsePacket.setSuccess(true);
          this.masterController.getRedisAdapter().sendPacket(responsePacket, "rhc_discord_rewards");
          this.masterController.getRedisAdapter()
              .sendPacket(new PlatformUserDiscordRewardStateUpdatePacket(user.getUniqueId()),
                  "rhc_platform");
          this.masterController.getRedisAdapter()
              .sendPacket(new DiscordRewardGivePacket(user.getUniqueId()),
                  "rhc_platform_" + sectorUser.getSector().getName());
          //noinspection DuplicatedCode
          final List<UUID> uuidList = new ArrayList<>();
          for (final SectorUser targetSectorUser : this.masterController.getSectorUserFactory()
              .getUserMap().values()) {
            if (this.masterController.getPlatformUserFactory()
                .findUserByUniqueId(targetSectorUser.getUniqueId())
                .map(PlatformUser::getChatSettings).filter(PlatformUserChatSettings::isRewards)
                .isPresent()) {
              uuidList.add(targetSectorUser.getUniqueId());
            }
          }

          if (!uuidList.isEmpty()) {
            final MessagesWrapper messagesWrapper = this.masterController.getConfigurationFactory()
                .findConfiguration(PlatformConfiguration.class).messagesWrapper;
            this.masterController.getRedisAdapter().sendPacket(
                new PlatformUserMessagePacket(uuidList,
                    messagesWrapper.discordRewardReceived.replace("{PLAYER_NAME}",
                        user.getNickname())), "rhc_platform");
          }
        });
  }
}
