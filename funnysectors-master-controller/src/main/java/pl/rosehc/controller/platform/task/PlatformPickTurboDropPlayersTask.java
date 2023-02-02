package pl.rosehc.controller.platform.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;
import pl.rosehc.controller.MasterController;
import pl.rosehc.controller.configuration.impl.configuration.PlatformConfiguration;
import pl.rosehc.controller.packet.platform.user.PlatformUserMessagePacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserSynchronizeSomeDropSettingsDataPacket;
import pl.rosehc.controller.platform.user.PlatformUser;
import pl.rosehc.controller.platform.user.subdata.PlatformUserDropSettings;
import pl.rosehc.controller.sector.Sector;
import pl.rosehc.controller.sector.SectorType;
import pl.rosehc.controller.sector.user.SectorUser;

public final class PlatformPickTurboDropPlayersTask implements Runnable {

  private static final long TURBO_DROP_TIME = 900_000L;
  private final MasterController masterController;

  public PlatformPickTurboDropPlayersTask(final MasterController masterController) {
    this.masterController = masterController;
  }

  @Override
  public void run() {
    final PlatformConfiguration platformConfiguration = this.masterController.getConfigurationFactory()
        .findConfiguration(PlatformConfiguration.class);
    final Map<Sector, PlatformUser> pickedPlayerMap = new HashMap<>();
    final Map<Sector, List<PlatformUser>> playersBySectorMap = new HashMap<>();
    int iterations = 0, maxIterations = ThreadLocalRandom.current().nextInt(5, 12);
    this.masterController.getRedisAdapter().sendPacket(new PlatformUserMessagePacket(
        platformConfiguration.messagesWrapper.turboDropPickingActivatedInfo), "rhc_platform");
    for (final SectorUser user : this.masterController.getSectorUserFactory().getUserMap()
        .values()) {
      if (user.getSector().getType().equals(SectorType.GAME)) {
        final List<PlatformUser> platformUserList = playersBySectorMap.computeIfAbsent(
            user.getSector(), ignored -> new ArrayList<>());
        this.masterController.getPlatformUserFactory().findUserByUniqueId(user.getUniqueId())
            .ifPresent(platformUserList::add);
        if (iterations++ >= maxIterations) {
          break;
        }
      }
    }

    for (final Entry<Sector, List<PlatformUser>> entry : playersBySectorMap.entrySet()) {
      final List<PlatformUser> sectorUserList = entry.getValue();
      pickedPlayerMap.put(entry.getKey(),
          sectorUserList.get(ThreadLocalRandom.current().nextInt(0, sectorUserList.size())));
    }

    if (pickedPlayerMap.isEmpty()) {
      this.masterController.getRedisAdapter().sendPacket(new PlatformUserMessagePacket(
          platformConfiguration.messagesWrapper.turboDropPickingNoPlayersFound), "rhc_platform");
      return;
    }

    final StringBuilder pickedPlayerNameBuilder = new StringBuilder();
    for (final Entry<Sector, PlatformUser> entry : pickedPlayerMap.entrySet()) {
      final PlatformUser user = entry.getValue();
      final PlatformUserDropSettings dropSettings = user.getDropSettings();
      pickedPlayerNameBuilder.append(user.getNickname()).append(" (")
          .append(entry.getKey().getName()).append(")").append(", ");
      dropSettings.setTurboDropMultiplier(
          platformConfiguration.dropSettingsWrapper.turboDropMultiplier);
      dropSettings.setTurboDropTime(System.currentTimeMillis() + TURBO_DROP_TIME);
      this.masterController.getRedisAdapter().sendPacket(
          new PlatformUserSynchronizeSomeDropSettingsDataPacket(user.getUniqueId(),
              dropSettings.getTurboDropMultiplier(), dropSettings.isCobbleStone(),
              dropSettings.getTurboDropTime(), dropSettings.getCurrentXP(),
              dropSettings.getNeededXP(), dropSettings.getLevel()), "rhc_platform");
    }

    final String pickedPlayerNames = pickedPlayerNameBuilder.toString();
    this.masterController.getRedisAdapter().sendPacket(new PlatformUserMessagePacket(
            platformConfiguration.messagesWrapper.turboDropPickingSucceed.replace(
                "{PICKED_PLAYER_NAMES}",
                pickedPlayerNames.endsWith(", ") ? pickedPlayerNames.substring(0,
                    pickedPlayerNames.length() - ", ".length()).trim() : pickedPlayerNames)),
        "rhc_platform");
  }
}
