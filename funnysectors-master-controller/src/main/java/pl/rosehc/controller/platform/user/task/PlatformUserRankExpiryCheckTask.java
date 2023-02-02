package pl.rosehc.controller.platform.user.task;

import pl.rosehc.controller.MasterController;
import pl.rosehc.controller.packet.platform.user.PlatformUserRankUpdatePacket;
import pl.rosehc.controller.platform.rank.Rank;
import pl.rosehc.controller.platform.rank.RankEntry;
import pl.rosehc.controller.platform.user.PlatformUser;

public final class PlatformUserRankExpiryCheckTask implements Runnable {

  private final MasterController masterController;

  public PlatformUserRankExpiryCheckTask(final MasterController masterController) {
    this.masterController = masterController;
  }

  @Override
  public void run() {
    for (final PlatformUser user : this.masterController.getPlatformUserFactory().getUserMap()
        .values()) {
      final RankEntry rank = user.getRank();
      final Rank previousRank = rank.getPreviousRank();
      if (previousRank != null && rank.getExpirationTime() != 0L
          && rank.getExpirationTime() <= System.currentTimeMillis()) {
        user.setRank(RankEntry.create(null, previousRank, 0L));
        this.masterController.getRedisAdapter().sendPacket(
            new PlatformUserRankUpdatePacket(user.getUniqueId(), null, previousRank.getName(), 0L),
            "rhc_platform");
      }
    }
  }
}
