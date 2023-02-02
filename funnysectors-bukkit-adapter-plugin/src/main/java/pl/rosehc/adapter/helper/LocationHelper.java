package pl.rosehc.adapter.helper;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;

/**
 * @author stevimeister on 03/10/2021
 **/
public final class LocationHelper {

  public LocationHelper() {
  }

  public static List<Location> sphere(final Location location, final int radius, final int height,
      final boolean hollow, final boolean sphere) {
    final List<Location> sphereBlockList = new ArrayList<>();
    final double radiusSquared = Math.pow(radius, 2D), hollowRadiusSquared = Math.pow(radius - 1D,
        2D);
    final int startX = location.getBlockX(), startY = location.getBlockY(), startZ = location.getBlockZ();
    for (int x = (startX - radius); x <= (startX + radius); x++) {
      for (int y = (sphere ? startY - radius : startY);
          y < (sphere ? startY + radius : startY + height); y++) {
        for (int z = (startZ - radius); z <= (startZ + radius); z++) {
          final double distance =
              Math.pow(startX - x, 2D) + Math.pow(startZ - z, 2D) + (sphere ? Math.pow(startY - y,
                  2D) : 0D);
          if (distance < radiusSquared && !(hollow && distance < hollowRadiusSquared)) {
            final Location sphereBlockLocation = new Location(location.getWorld(), x, y, z);
            sphereBlockList.add(sphereBlockLocation);
          }
        }
      }
    }

    return sphereBlockList;
  }

  public static boolean isSameLocationXZ(final Location from, final Location to) {
    return from.getBlockX() == to.getBlockX()
        && from.getBlockZ() == to.getBlockZ();
  }

  public static boolean isSameLocationXYZ(final Location from, final Location to) {
    return from.getBlockX() == to.getBlockX()
        && from.getBlockY() == to.getBlockY()
        && from.getBlockZ() == to.getBlockZ();
  }
}