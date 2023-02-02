package pl.rosehc.sectors.helper;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.adapter.redis.RedisAdapter;
import pl.rosehc.adapter.redis.callback.Callback;
import pl.rosehc.adapter.redis.callback.CallbackPacket;
import pl.rosehc.sectors.SectorsPlugin;
import pl.rosehc.sectors.data.SectorPlayerData;
import pl.rosehc.sectors.data.SectorPlayerDataSynchronizeRequestPacket;
import pl.rosehc.sectors.sector.Sector;
import pl.rosehc.sectors.sector.user.SectorUser;
import pl.rosehc.sectors.sector.user.SectorUserConnectPacket;

public final class ConnectHelper {

  private static final Runnable EMPTY_CONNECT_ACTION = () -> {
  };

  private ConnectHelper() {
  }

  public static void connect(final Player player, final SectorUser user,
      final SectorPlayerData data, final Sector sector, final Runnable postConnectAction,
      final Runnable connectionErrorAction) {
    if (player.isInsideVehicle()) {
      player.leaveVehicle();
    }

    final RedisAdapter redisAdapter = SectorsPlugin.getInstance().getRedisAdapter();
    user.setRedirecting(true);
    redisAdapter.sendPacket(new SectorPlayerDataSynchronizeRequestPacket(data,
            SectorsPlugin.getInstance().getSectorFactory().getCurrentSector().getName()),
        new Callback() {

          @Override
          public void done(final CallbackPacket packet) {
            if (player.isOnline()) {
              ChatHelper.sendMessage(player, SectorsPlugin.getInstance()
                  .getSectorsConfiguration().messagesWrapper.connectingInfo.replace("{SECTOR_NAME}",
                      sector.getName()));
              redisAdapter.set("rhc_player_sectors", player.getUniqueId().toString(),
                  sector.getName());
              redisAdapter.sendPacket(
                  new SectorUserConnectPacket(user.getUniqueId(), sector.getName()),
                  "rhc_proxy_" + user.getProxy().getIdentifier());
              postConnectAction.run();
            }
          }

          @Override
          public void error(final String message) {
            if (player.isOnline()) {
              ChatHelper.sendMessage(player, message);
              user.setRedirecting(false);
              connectionErrorAction.run();
            }
          }
        }, "rhc_playerdata_" + sector.getName());
  }

  public static void connect(final Player player, final SectorUser user, final Sector sector,
      final Location targetLocation, final Runnable postConnectAction,
      final Runnable connectionErrorAction) {
    final SectorPlayerData data = SectorPlayerData.of(player);
    data.setLocation(targetLocation);
    connect(player, user, data, sector, postConnectAction, connectionErrorAction);
  }

  public static void connect(final Player player, final SectorUser user, final Sector sector,
      final Location targetLocation) {
    connect(player, user, sector, targetLocation, EMPTY_CONNECT_ACTION, EMPTY_CONNECT_ACTION);
  }

  public static void connect(final Player player, final SectorUser user,
      final SectorPlayerData data, final Sector sector) {
    connect(player, user, data, sector, EMPTY_CONNECT_ACTION, EMPTY_CONNECT_ACTION);
  }
}
