package pl.rosehc.platform.user;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.rosehc.adapter.redis.callback.Callback;
import pl.rosehc.adapter.redis.callback.CallbackPacket;
import pl.rosehc.platform.PlatformPlugin;
import pl.rosehc.sectors.SectorsPlugin;
import pl.rosehc.sectors.data.SectorPlayerData;
import pl.rosehc.sectors.data.SectorPlayerDataSynchronizeRequestPacket;
import pl.rosehc.sectors.helper.SectorHelper;
import pl.rosehc.sectors.sector.Sector;
import pl.rosehc.sectors.sector.SectorType;
import pl.rosehc.sectors.sector.user.SectorUser;

public final class PlatformUserSectorSpoofHelper {

  private PlatformUserSectorSpoofHelper() {
  }

  public static void spoofAllSectors() {
    for (final Player player : Bukkit.getOnlinePlayers()) {
      final CompletableFuture<?> future = new CompletableFuture<>();
      final Sector sector = SectorHelper.getRandomSector(SectorType.SPAWN).orElseGet(
          () -> SectorHelper.getRandomSector(SectorType.GAME).orElseThrow(
              () -> new UnsupportedOperationException("Nie można było poszukać wolnego sektora!")));
      final SectorUser user = SectorsPlugin.getInstance().getSectorUserFactory()
          .findUserByUniqueId(player.getUniqueId()).orElseThrow(
              () -> new UnsupportedOperationException(
                  "Użytkownik o nicku " + player.getName() + " nie istnieje!"));
      final SectorPlayerData data = SectorPlayerData.of(player);
      data.setLocation(sector.getType().equals(SectorType.SPAWN) ? PlatformPlugin.getInstance()
          .getPlatformConfiguration().spawnLocationWrapper.unwrap() : sector.random());
      PlatformPlugin.getInstance().getRedisAdapter().sendPacket(
          new SectorPlayerDataSynchronizeRequestPacket(data,
              SectorsPlugin.getInstance().getSectorFactory().getCurrentSector().getName()),
          new Callback() {

            @Override
            public void done(final CallbackPacket packet) {
              PlatformPlugin.getInstance().getRedisAdapter()
                  .set("rhc_player_sectors", player.getUniqueId().toString(), sector.getName());
              future.complete(null);
            }

            @Override
            public void error(final String ignored) {
              future.completeExceptionally(
                  new UnsupportedOperationException("Błąd podczas syncu danych."));
            }
          }, "rhc_playerdata_" + sector.getName());
      try {
        future.get(5L, TimeUnit.SECONDS);
      } catch (final InterruptedException | ExecutionException | TimeoutException ex) {
        ex.printStackTrace();
      }
    }
  }
}
