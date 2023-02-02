package pl.rosehc.controller.packet;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.MasterController;
import pl.rosehc.controller.configuration.ConfigurationHelper;
import pl.rosehc.controller.configuration.impl.configuration.GuildsConfiguration;
import pl.rosehc.controller.guild.guild.Guild;
import pl.rosehc.controller.guild.guild.GuildHelper;
import pl.rosehc.controller.guild.guild.GuildMember;
import pl.rosehc.controller.guild.guild.GuildPlayerHelpInfo;
import pl.rosehc.controller.guild.guild.GuildRegenerationBlockState;
import pl.rosehc.controller.guild.guild.GuildRegion;
import pl.rosehc.controller.guild.guild.GuildSchematicCacheHelper;
import pl.rosehc.controller.guild.guild.group.GuildGroup;
import pl.rosehc.controller.guild.user.GuildUser;
import pl.rosehc.controller.packet.guild.GuildsInitializationRequestPacket;
import pl.rosehc.controller.packet.guild.GuildsInitializationResponsePacket;
import pl.rosehc.controller.packet.guild.guild.GuildAddRegenerationBlocksPacket;
import pl.rosehc.controller.packet.guild.guild.GuildAllyInviteEntryUpdatePacket;
import pl.rosehc.controller.packet.guild.guild.GuildCreatePacket;
import pl.rosehc.controller.packet.guild.guild.GuildDeletePacket;
import pl.rosehc.controller.packet.guild.guild.GuildGuildsRequestPacket;
import pl.rosehc.controller.packet.guild.guild.GuildGuildsResponsePacket;
import pl.rosehc.controller.packet.guild.guild.GuildHelpInfoAddPacket;
import pl.rosehc.controller.packet.guild.guild.GuildHelpInfoRemovePacket;
import pl.rosehc.controller.packet.guild.guild.GuildHelpInfoUpdatePacket;
import pl.rosehc.controller.packet.guild.guild.GuildHomeLocationUpdatePacket;
import pl.rosehc.controller.packet.guild.guild.GuildJoinAlertMessageUpdatePacket;
import pl.rosehc.controller.packet.guild.guild.GuildMemberAddPacket;
import pl.rosehc.controller.packet.guild.guild.GuildMemberInviteAddPacket;
import pl.rosehc.controller.packet.guild.guild.GuildMemberInviteRemovePacket;
import pl.rosehc.controller.packet.guild.guild.GuildMemberRemovePacket;
import pl.rosehc.controller.packet.guild.guild.GuildMemberUpdateRankPacket;
import pl.rosehc.controller.packet.guild.guild.GuildPistonsUpdatePacket;
import pl.rosehc.controller.packet.guild.guild.GuildPvPUpdatePacket;
import pl.rosehc.controller.packet.guild.guild.GuildRegionUpdateSizePacket;
import pl.rosehc.controller.packet.guild.guild.GuildUpdateAllyPacket;
import pl.rosehc.controller.packet.guild.guild.GuildValidityTimeUpdatePacket;
import pl.rosehc.controller.packet.guild.user.GuildUserCacheFighterPacket;
import pl.rosehc.controller.packet.guild.user.GuildUserCacheVictimPacket;
import pl.rosehc.controller.packet.guild.user.GuildUserClearFightersPacket;
import pl.rosehc.controller.packet.guild.user.GuildUserCreatePacket;
import pl.rosehc.controller.packet.guild.user.GuildUserSynchronizeRankingPacket;
import pl.rosehc.controller.packet.guild.user.GuildUsersRequestPacket;
import pl.rosehc.controller.packet.guild.user.GuildUsersResponsePacket;
import pl.rosehc.controller.wrapper.guild.GuildMemberSerializableWrapper;
import pl.rosehc.controller.wrapper.guild.GuildPermissionTypeWrapper;
import pl.rosehc.controller.wrapper.guild.GuildSerializableWrapper;
import pl.rosehc.controller.wrapper.guild.GuildUserSerializableWrapper;

public final class GuildPacketHandler implements PacketHandler {

  private final MasterController masterController;

  public GuildPacketHandler(final MasterController masterController) {
    this.masterController = masterController;
  }

  public void handle(final GuildCreatePacket packet) {
    if (!this.masterController.getGuildFactory().findGuildByCredential(packet.getName(), false)
        .isPresent() && !this.masterController.getGuildFactory()
        .findGuildByCredential(packet.getTag()).isPresent()) {
      try {
        final Map<UUID, GuildGroup> defaultGuildGroupMap = GuildHelper.createDefaultGuildGroups();
        final Guild guild = new Guild(packet.getName(), packet.getTag(), defaultGuildGroupMap,
            new GuildMember(packet.getLeader().getUniqueId(),
                this.masterController.getGuildUserFactory()
                    .findUserByUniqueId(packet.getLeader().getUniqueId()).orElseThrow(
                        () -> new UnsupportedOperationException(
                            "Brak użytkownika o identyfikatorze " + packet.getLeader().getUniqueId()
                                + "!")), packet.getLeader().getPermissions().stream()
                .map(GuildPermissionTypeWrapper::toOriginal).collect(Collectors.toSet()),
                defaultGuildGroupMap.get(
                    this.masterController.getGuildGroupFactory().getLeaderGuildGroup()
                        .getUniqueId())), packet.getGuildType().toOriginal(),
            new GuildRegion(Objects.requireNonNull(packet.getGuildRegion().getCenterLocation()),
                packet.getGuildRegion().getSize()),
            this.masterController.getSectorFactory().findSector(packet.getCreationSectorName())
                .orElseThrow(() -> new UnsupportedOperationException(
                    "Brak sektora o nazwie " + packet.getCreationSectorName() + "!")),
            packet.getHomeLocation(), packet.getValidityTime(), packet.getProtectionTime(),
            packet.getLives());
        this.masterController.getGuildRepository().insert(guild);
        this.masterController.getGuildFactory().registerGuild(guild);
      } catch (final SQLException ex) {
        System.err.println(
            "[GILDIE] Wystąpił niespodziewany problem podczas próby stworzenia gildii.");
        ex.printStackTrace();
      }
    }
  }

  public void handle(final GuildDeletePacket packet) {
    this.masterController.getGuildFactory().findGuildByCredential(packet.getTag())
        .ifPresent(guild -> {
          try {
            this.masterController.getGuildRepository().delete(guild);
            this.masterController.getGuildFactory().unregisterGuild(guild);
            for (final GuildMember guildMember : guild.getGuildMembers()) {
              if (guildMember != null) {
                guildMember.getUser().setMemberArrayPosition(-1);
                guildMember.getUser().setGuild(null);
              }
            }

            final Guild alliedGuild = guild.getAlliedGuild();
            if (alliedGuild != null) {
              alliedGuild.setAlliedGuild(null);
            }
          } catch (final SQLException ex) {
            System.err.println(
                "[GILDIE] Wystąpił niespodziewany problem podczas próby usunięcia gildii.");
            ex.printStackTrace();
          }
        });
  }

  public void handle(final GuildUserCreatePacket packet) {
    if (!this.masterController.getGuildUserFactory().findUserByUniqueId(packet.getUniqueId())
        .isPresent()) {
      try {
        final GuildUser user = new GuildUser(packet.getUniqueId(), packet.getNickname());
        this.masterController.getGuildUserRepository().insert(user);
        this.masterController.getGuildUserFactory().addUser(user);
      } catch (final SQLException ex) {
        System.err.println(
            "[GILDIE] Wystąpił niespodziewany problem podczas próby stworzenia użytkownika.");
        ex.printStackTrace();
      }
    }
  }

  public void handle(final GuildGuildsRequestPacket packet) {
    final List<GuildSerializableWrapper> guildList = new ArrayList<>();
    for (final Guild guild : this.masterController.getGuildFactory().getGuildMap().values()) {
      guildList.add(guild.wrap(packet.getSectorName()));
    }

    final GuildGuildsResponsePacket responsePacket = new GuildGuildsResponsePacket(guildList);
    responsePacket.setCallbackId(packet.getCallbackId());
    responsePacket.setResponse(true);
    responsePacket.setSuccess(true);
    this.masterController.getRedisAdapter()
        .sendPacket(responsePacket, "rhc_guilds_" + packet.getSectorName());
  }

  public void handle(final GuildUsersRequestPacket packet) {
    final List<GuildUserSerializableWrapper> userList = new ArrayList<>();
    for (final GuildUser user : this.masterController.getGuildUserFactory().getUserMap().values()) {
      userList.add(user.wrap());
    }

    final GuildUsersResponsePacket responsePacket = new GuildUsersResponsePacket(userList);
    responsePacket.setCallbackId(packet.getCallbackId());
    responsePacket.setResponse(true);
    responsePacket.setSuccess(true);
    this.masterController.getRedisAdapter()
        .sendPacket(responsePacket, "rhc_guilds_" + packet.getSectorName());
  }

  public void handle(final GuildMemberAddPacket packet) {
    this.masterController.getGuildFactory().findGuildByCredential(packet.getGuildTag())
        .ifPresent(guild -> {
          final GuildMemberSerializableWrapper guildMemberWrapper = packet.getGuildMemberWrapper();
          guild.addGuildMember(new GuildMember(guildMemberWrapper.getUniqueId(),
              this.masterController.getGuildUserFactory()
                  .findUserByUniqueId(guildMemberWrapper.getUniqueId()).orElseThrow(
                      () -> new UnsupportedOperationException(
                          "Brak użytkownika o identyfikatorze " + guildMemberWrapper.getUniqueId()
                              + "!")), new HashSet<>(), guild.getDefaultGroup()));
        });
  }

  public void handle(final GuildsInitializationRequestPacket packet) {
    final GuildsConfiguration guildsConfiguration = this.masterController.getConfigurationFactory()
        .findConfiguration(GuildsConfiguration.class);
    final GuildsInitializationResponsePacket responsePacket = new GuildsInitializationResponsePacket(
        ConfigurationHelper.serializeConfiguration(guildsConfiguration),
        GuildSchematicCacheHelper.getSchematicData());
    responsePacket.setCallbackId(packet.getCallbackId());
    responsePacket.setResponse(true);
    responsePacket.setSuccess(true);
    this.masterController.getRedisAdapter()
        .sendPacket(responsePacket, "rhc_guilds_" + packet.getSectorName());
  }

  public void handle(final GuildHelpInfoRemovePacket packet) {
    final boolean ally = packet.isAlly();
    this.masterController.getGuildFactory().findGuildByCredential(packet.getGuildTag(), true)
        .ifPresent(guild -> (ally ? guild.findGuildAllyPlayerHelpInfo(packet.getUniqueId())
            : guild.findGuildPlayerHelpInfo(packet.getUniqueId())).ifPresent(helpInfo -> {
          if (ally) {
            guild.removeGuildAllyPlayerHelpInfo(packet.getUniqueId());
          } else {
            guild.removeGuildPlayerHelpInfo(packet.getUniqueId());
          }
        }));
  }

  public void handle(final GuildHelpInfoAddPacket packet) {
    this.masterController.getGuildFactory().findGuildByCredential(packet.getGuildTag())
        .ifPresent(guild -> {
          final GuildPlayerHelpInfo helpInfo = new GuildPlayerHelpInfo(packet.getNickname(),
              packet.getTime(), packet.getX(), packet.getY(), packet.getZ());
          if (packet.isAlly()) {
            guild.addGuildAllyPlayerHelpInfo(packet.getUniqueId(), helpInfo);
          } else {
            guild.addGuildPlayerHelpInfo(packet.getUniqueId(), helpInfo);
          }
        });
  }

  public void handle(final GuildUserSynchronizeRankingPacket packet) {
    this.masterController.getGuildUserFactory().findUserByUniqueId(packet.getUniqueId())
        .map(GuildUser::getUserRanking).ifPresent(guildUserRanking -> {
          guildUserRanking.setPoints(packet.getPoints());
          guildUserRanking.setKills(packet.getKills());
          guildUserRanking.setDeaths(packet.getDeaths());
          guildUserRanking.setKillStreak(packet.getKillStreak());
        });
  }

  public void handle(final GuildMemberUpdateRankPacket packet) {
    this.masterController.getGuildFactory().findGuildByCredential(packet.getGuildTag()).ifPresent(
        guild -> this.masterController.getGuildUserFactory()
            .findUserByUniqueId(packet.getPlayerUniqueId()).ifPresent(user -> {
              final GuildMember member = guild.getGuildMember(user);
              if (member != null) {
                guild.findGuildGroup(packet.getGroupUniqueId()).ifPresent(member::setGroup);
              }
            }));
  }

  public void handle(final GuildHelpInfoUpdatePacket packet) {
    this.masterController.getGuildFactory().findGuildByCredential(packet.getGuildTag()).flatMap(
        guild -> (packet.isAlly() ? guild.findGuildAllyPlayerHelpInfo(packet.getUniqueId())
            : guild.findGuildPlayerHelpInfo(packet.getUniqueId()))).ifPresent(helpInfo -> {
      helpInfo.setX(packet.getX());
      helpInfo.setY(packet.getY());
      helpInfo.setZ(packet.getZ());
    });
  }

  public void handle(final GuildPvPUpdatePacket packet) {
    this.masterController.getGuildFactory().findGuildByCredential(packet.getGuildTag())
        .ifPresent(guild -> {
          if (packet.isAlly()) {
            guild.setPvpAlly(packet.getStatus());
          } else {
            guild.setPvpGuild(packet.getStatus());
          }
        });
  }

  public void handle(final GuildMemberInviteAddPacket packet) {
    this.masterController.getGuildUserFactory().findUserByUniqueId(packet.getUniqueId()).ifPresent(
        user -> this.masterController.getGuildFactory().findGuildByCredential(packet.getGuildTag())
            .filter(guild -> !guild.isMemberInvited(user))
            .ifPresent(guild -> guild.addMemberInvite(user, packet.getTime())));
  }

  public void handle(final GuildMemberInviteRemovePacket packet) {
    this.masterController.getGuildUserFactory().findUserByUniqueId(packet.getUniqueId()).ifPresent(
        user -> this.masterController.getGuildFactory().findGuildByCredential(packet.getGuildTag())
            .filter(guild -> guild.isMemberInvited(user))
            .ifPresent(guild -> guild.removeMemberInvite(user)));
  }

  public void handle(final GuildUpdateAllyPacket packet) {
    this.masterController.getGuildFactory().findGuildByCredential(packet.getFirstGuildTag())
        .ifPresent(firstGuild -> this.masterController.getGuildFactory()
            .findGuildByCredential(packet.getSecondGuildTag()).ifPresent(secondGuild -> {
              firstGuild.setAlliedGuild(packet.isAdd() ? secondGuild : null);
              secondGuild.setAlliedGuild(packet.isAdd() ? firstGuild : null);
            }));
  }

  public void handle(final GuildMemberRemovePacket packet) {
    this.masterController.getGuildUserFactory().findUserByUniqueId(packet.getUniqueId()).ifPresent(
        user -> this.masterController.getGuildFactory().findGuildByCredential(packet.getGuildTag())
            .ifPresent(guild -> guild.removeGuildMember(user)));
  }

  public void handle(final GuildAddRegenerationBlocksPacket packet) {
    this.masterController.getGuildFactory().findGuildByCredential(packet.getTag()).ifPresent(
        guild -> guild.addRegenerationBlocks(packet.getBlockList().stream().map(
            wrapper -> new GuildRegenerationBlockState(wrapper.getMaterial(), wrapper.getData(),
                wrapper.getX(), wrapper.getY(), wrapper.getZ())).collect(Collectors.toList())));
  }

  public void handle(final GuildUserCacheFighterPacket packet) {
    this.masterController.getGuildUserFactory().findUserByUniqueId(packet.getTargetUniqueId())
        .ifPresent(user -> user.cacheFighter(packet.getTargetUniqueId(), packet.getFightTime()));
  }

  public void handle(final GuildUserCacheVictimPacket packet) {
    this.masterController.getGuildUserFactory().findUserByUniqueId(packet.getAttackerUniqueId())
        .ifPresent(user -> user.cacheVictim(packet.getVictimUniqueId(), packet.getTime()));
  }

  public void handle(final GuildUserClearFightersPacket packet) {
    this.masterController.getGuildUserFactory().findUserByUniqueId(packet.getUniqueId())
        .ifPresent(GuildUser::clearFighters);
  }

  public void handle(final GuildAllyInviteEntryUpdatePacket packet) {
    this.masterController.getGuildFactory().findGuildByCredential(packet.getGuildTag())
        .ifPresent(guild -> guild.setAllyInviteEntry(packet.getAllyInviteEntry()));
  }

  public void handle(final GuildJoinAlertMessageUpdatePacket packet) {
    this.masterController.getGuildFactory().findGuildByCredential(packet.getGuildTag())
        .ifPresent(guild -> guild.setJoinAlertMessage(packet.getJoinAlertMessage()));
  }

  public void handle(final GuildHomeLocationUpdatePacket packet) {
    this.masterController.getGuildFactory().findGuildByCredential(packet.getGuildTag())
        .ifPresent(guild -> guild.setHomeLocation(packet.getHomeLocation()));
  }

  public void handle(final GuildPistonsUpdatePacket packet) {
    this.masterController.getGuildFactory().findGuildByCredential(packet.getGuildTag())
        .ifPresent(guild -> guild.setPistonsOnGuild(packet.getPistons()));
  }

  public void handle(final GuildRegionUpdateSizePacket packet) {
    this.masterController.getGuildFactory().findGuildByCredential(packet.getGuildTag())
        .ifPresent(guild -> guild.getGuildRegion().setSize(packet.getSize()));
  }

  public void handle(final GuildValidityTimeUpdatePacket packet) {
    this.masterController.getGuildFactory().findGuildByCredential(packet.getGuildTag())
        .ifPresent(guild -> guild.setValidityTime(packet.getValidityTime()));
  }
}
