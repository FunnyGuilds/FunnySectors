package pl.rosehc.controller.configuration.impl.handler;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import pl.rosehc.adapter.helper.TimeHelper;
import pl.rosehc.controller.MasterController;
import pl.rosehc.controller.configuration.ConfigurationHelper;
import pl.rosehc.controller.configuration.ConfigurationReloadHandler;
import pl.rosehc.controller.configuration.impl.configuration.PlatformConfiguration;
import pl.rosehc.controller.packet.configuration.ConfigurationSynchronizePacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserRankUpdatePacket;
import pl.rosehc.controller.platform.rank.Rank;
import pl.rosehc.controller.platform.rank.RankEntry;
import pl.rosehc.controller.platform.rank.RankFactory;
import pl.rosehc.controller.platform.user.PlatformUser;
import pl.rosehc.controller.platform.user.task.PlatformUserSendAutoMessageTask;

public final class PlatformConfigurationReloadHandler implements
    ConfigurationReloadHandler<PlatformConfiguration> {

  private final MasterController masterController;

  public PlatformConfigurationReloadHandler(final MasterController masterController) {
    this.masterController = masterController;
  }

  @Override
  public void handle(final PlatformConfiguration configuration) {
    final RankFactory rankFactory = this.masterController.getRankFactory();
    rankFactory.getRankMap().clear();
    rankFactory.getRankMap().putAll(configuration.rankList.stream().map(
            rankWrapper -> new Rank(rankWrapper.name, rankWrapper.chatPrefix, rankWrapper.chatSuffix,
                rankWrapper.nameTagPrefix, rankWrapper.nameTagSuffix, rankWrapper.permissions,
                rankWrapper.priority, rankWrapper.defaultRank))
        .collect(Collectors.toConcurrentMap(rank -> rank.getName().toLowerCase(), rank -> rank)));
    rankFactory.updateDefaultRank();
    for (final PlatformUser user : this.masterController.getPlatformUserFactory().getUserMap()
        .values()) {
      final RankEntry rank = user.getRank();
      final boolean previousRankNotFound =
          rank.getPreviousRank() != null && !rankFactory.getRankMap()
              .containsKey(rank.getPreviousRank().getName().toLowerCase());
      if (previousRankNotFound || !rankFactory.getRankMap()
          .containsKey(rank.getCurrentRank().getNameTagPrefix().toLowerCase())) {
        final RankEntry newRank = RankEntry.create(
            previousRankNotFound ? rankFactory.getDefaultRank() : rank.getPreviousRank(),
            !rankFactory.getRankMap().containsKey(rank.getCurrentRank().getName().toLowerCase())
                ? rankFactory.getDefaultRank() : rank.getCurrentRank(),
            !previousRankNotFound ? rank.getExpirationTime() : 0L);
        user.setRank(newRank);
        this.masterController.getRedisAdapter().sendPacket(
            new PlatformUserRankUpdatePacket(user.getUniqueId(),
                newRank.getPreviousRank() != null ? newRank.getPreviousRank().getName() : null,
                newRank.getCurrentRank().getName(), newRank.getExpirationTime()), "rhc_platform");
      }
    }

    final Set<String> dropNameSet = configuration.dropSettingsWrapper.dropWrapperList.stream()
        .map(dropWrapper -> dropWrapper.name).collect(Collectors.toSet());
    for (final PlatformUser user : this.masterController.getPlatformUserFactory().getUserMap()
        .values()) {
      user.getDropSettings().getDisabledDropSet()
          .removeIf(dropName -> !dropNameSet.contains(dropName));
    }

    configuration.autoMessagesSettingsWrapper.parsedBroadcastTime = TimeHelper.timeFromString(
        configuration.autoMessagesSettingsWrapper.broadcastTime);
    if (this.masterController.getAutoMessageTaskFuture() != null) {
      this.masterController.getAutoMessageTaskFuture().cancel(true);
      this.masterController.setAutoMessageTaskFuture(null);
    }

    this.masterController.setAutoMessageTaskFuture(MasterController.SCHEDULER.scheduleAtFixedRate(
        new PlatformUserSendAutoMessageTask(this.masterController),
        configuration.autoMessagesSettingsWrapper.parsedBroadcastTime,
        configuration.autoMessagesSettingsWrapper.parsedBroadcastTime, TimeUnit.MILLISECONDS));
    this.masterController.getRedisAdapter().sendPacket(
        new ConfigurationSynchronizePacket(configuration.getClass().getName(),
            ConfigurationHelper.serializeConfiguration(configuration)), "rhc_global");
  }
}
