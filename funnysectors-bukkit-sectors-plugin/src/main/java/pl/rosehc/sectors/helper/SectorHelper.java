package pl.rosehc.sectors.helper;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.adapter.helper.NumberHelper;
import pl.rosehc.sectors.SectorsPlugin;
import pl.rosehc.sectors.sector.Sector;
import pl.rosehc.sectors.sector.SectorFactory;
import pl.rosehc.sectors.sector.SectorType;
import pl.rosehc.sectors.sector.user.SectorUser;

/**
 * @author stevimeister on 06/01/2022
 **/
public final class SectorHelper {

  private static final Vector KNOCK_Y_VECTOR = new Vector(0.0D, 1.25D, 0.0D);

  private SectorHelper() {
  }

  public static Optional<Sector> getAt(final Location location) {
    final SectorFactory sectorFactory = SectorsPlugin.getInstance().getSectorFactory();
    return sectorFactory.getSectorMap().values().stream()
        .filter(sector -> isInside(location, sector)).filter(
            sector -> !sector.getType().equals(SectorType.GROUP_TELEPORTS) && !sector.getType()
                .equals(SectorType.GUILD_TOURNAMENT) && !sector.getType()
                .equals(SectorType.PVP_TOURNAMENT)).filter(
            sector -> !sector.equals(sectorFactory.getCurrentSector()) && isMultiSectorNotCurrent(
                sector, sectorFactory.getCurrentSector(), SectorType.SPAWN)
                && isMultiSectorNotCurrent(sector, sectorFactory.getCurrentSector(),
                SectorType.END)).findFirst();
  }

  public static Optional<Sector> getRandomSector(final SectorType type,
      final Predicate<Sector> checker) {
    final List<Sector> sectorList = SectorsPlugin.getInstance().getSectorFactory().getSectorMap()
        .values().stream().filter(sector -> sector.getType().equals(type)).filter(checker)
        .collect(Collectors.toList());
    return !sectorList.isEmpty() ? Optional.of(
        sectorList.get(NumberHelper.range(0, sectorList.size()))) : Optional.empty();
  }

  public static Optional<Sector> getRandomSector(final SectorType type) {
    return getRandomSector(type,
        sector -> sector.getStatistics().isOnline() && sector.getStatistics().getLoad() < 68.8D
            && sector.getStatistics().getTps() > 5.58D);
  }

  public static int getDistanceToNearestSector(final Location location) {
    final Sector sector = SectorsPlugin.getInstance().getSectorFactory().getCurrentSector();
    final int minDistanceX = Math.abs(
        location.getBlockX() - sector.getMinX()), maxDistanceX = Math.abs(
        location.getBlockX() - sector.getMaxX());
    final int minDistanceZ = Math.abs(
        location.getBlockZ() - sector.getMinZ()), maxDistanceZ = Math.abs(
        location.getBlockZ() - sector.getMaxZ());
    return Math.min(Math.min(minDistanceX, maxDistanceX), Math.min(minDistanceZ, maxDistanceZ));
  }

  public static boolean isNearSector(final Location location, final int distance) {
    return getDistanceToNearestSector(location) <= distance;
  }

  public static boolean isInsideBorder(final Location location) {
    final int borderSize =
        SectorsPlugin.getInstance().getCurrentSectorConfiguration().currentSectorBorderSize != -1
            && SectorsPlugin.getInstance().getSectorFactory().getCurrentSector().getWorld()
            .equals(location.getWorld()) ? SectorsPlugin.getInstance()
            .getCurrentSectorConfiguration().currentSectorBorderSize
            : SectorsPlugin.getInstance().getSectorsConfiguration().borderSize;
    return location.getBlockX() >= borderSize || location.getBlockX() <= -borderSize
        || location.getBlockZ() >= borderSize || location.getBlockZ() <= -borderSize;
  }

  public static boolean isInside(final Location location, final Sector sector) {
    return location.getWorld().getEnvironment()
        .equals(sector.getType().equals(SectorType.END) ? Environment.THE_END : Environment.NORMAL)
        && location.getBlockX() > sector.getMinX() && location.getBlockX() < sector.getMaxX()
        && location.getBlockZ() > sector.getMinZ() && location.getBlockZ() < sector.getMaxZ();
  }

  public static void knockFromEndPortal(final Player player) {
    final Location sectorLocation = new Location(player.getLocation().getWorld(),
        SectorsPlugin.getInstance().getSectorFactory().getCurrentSector().getCenterX(),
        player.getLocation().getY(),
        SectorsPlugin.getInstance().getSectorFactory().getCurrentSector().getCenterZ());
    double distance = 0.40D / player.getLocation().distance(sectorLocation);
    distance = (Double.isNaN(distance) || distance >= 4D || distance <= -4D) ? -distance : distance;
    player.setVelocity(player.getLocation().subtract(sectorLocation).toVector().add(KNOCK_Y_VECTOR)
        .multiply(distance).setY(0.5D));
  }

  public static void knockFromSector(final Player player) {
    final Location location = new Location(player.getLocation().getWorld(),
        SectorsPlugin.getInstance().getSectorFactory().getCurrentSector().getCenterX(),
        player.getLocation().getY(),
        SectorsPlugin.getInstance().getSectorFactory().getCurrentSector().getCenterZ());
    player.setVelocity(
        location.toVector().subtract(player.getLocation().toVector()).normalize().multiply(1.5D));
  }

  public static void sendMessageIfNoPortalAntiSpam(final Player player, final SectorUser user,
      final String message, final boolean portalCheck) {
    if (!portalCheck || !user.isPortalAntiSpam()) {
      ChatHelper.sendMessage(player, message);
    }

    user.markPortalAntiSpamAsEnabled();
  }

  public static void sendMessageIfNoPortalAntiSpam(final Player player, final String message,
      final boolean portalCheck) {
    final SectorUser user = SectorsPlugin.getInstance().getSectorUserFactory()
        .findUserByPlayer(player);
    if (Objects.nonNull(user)) {
      sendMessageIfNoPortalAntiSpam(player, user, message, portalCheck);
    }
  }

  private static boolean isMultiSectorNotCurrent(Sector sector, Sector currentSector,
      SectorType type) {
    return !sector.getType().equals(type) || !currentSector.getType().equals(type);
  }
}