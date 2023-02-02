package pl.rosehc.sectors.sector.task;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldBorder;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldBorder.EnumWorldBorderAction;
import net.minecraft.server.v1_8_R3.WorldBorder;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import pl.rosehc.sectors.SectorsPlugin;
import pl.rosehc.sectors.sector.Sector;

public final class SectorBorderUpdateTask implements Runnable {

  private static final int GROWTH = 999999999;
  private static final int OFFSET = 2;
  private static final int GROWTH_IN_RADIUS = (GROWTH * 2) + OFFSET;
  private final SectorsPlugin plugin;

  public SectorBorderUpdateTask(final SectorsPlugin plugin) {
    this.plugin = plugin;
    this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(this.plugin, this, 20L, 20L);
  }

  @Override
  public void run() {
    final Sector currentSector = this.plugin.getSectorFactory().getCurrentSector();
    for (final Player player : this.plugin.getServer().getOnlinePlayers()) {
      final double y = player.getLocation().getY();
      final List<Location> cornerList = Arrays.asList(
          new Location(player.getWorld(), currentSector.getMinX(), y, currentSector.getMinZ()),
          new Location(player.getWorld(), currentSector.getMaxX(), y, currentSector.getMinZ()),
          new Location(player.getWorld(), currentSector.getMinX(), y, currentSector.getMaxZ()),
          new Location(player.getWorld(), currentSector.getMaxX(), y, currentSector.getMaxZ())
      );
      final Location nearestCorner = this.findNearestCorner(player, cornerList);
      if (Objects.isNull(nearestCorner)) {
        continue;
      }

      final Location fixedNearestCorner = this.fixNearestCorner(currentSector, nearestCorner);
      final WorldBorder border = new WorldBorder();
      border.setCenter(fixedNearestCorner.getX(), fixedNearestCorner.getZ());
      border.setSize(GROWTH_IN_RADIUS);
      ((CraftPlayer) player).getHandle().playerConnection.sendPacket(
          new PacketPlayOutWorldBorder(border, EnumWorldBorderAction.SET_CENTER));
      ((CraftPlayer) player).getHandle().playerConnection.sendPacket(
          new PacketPlayOutWorldBorder(border, EnumWorldBorderAction.SET_SIZE));
    }
  }

  private Location fixNearestCorner(final Sector currentSector, final Location nearestCorner) {
    final Location nearestCornerClone = nearestCorner.clone();
    final int borderSize =
        SectorsPlugin.getInstance().getCurrentSectorConfiguration().currentSectorBorderSize != -1
            && SectorsPlugin.getInstance().getSectorFactory().getCurrentSector().getWorld()
            .equals(nearestCornerClone.getWorld()) ? SectorsPlugin.getInstance()
            .getCurrentSectorConfiguration().currentSectorBorderSize
            : SectorsPlugin.getInstance().getSectorsConfiguration().borderSize;
    if (nearestCornerClone.getX() >= borderSize) {
      nearestCornerClone.setX(nearestCornerClone.getX() - OFFSET);
    } else if (nearestCornerClone.getX() <= -borderSize) {
      nearestCornerClone.setX(nearestCornerClone.getX() + OFFSET);
    }

    if (nearestCornerClone.getZ() >= borderSize) {
      nearestCornerClone.setZ(nearestCornerClone.getZ() - OFFSET);
    } else if (nearestCornerClone.getZ() <= -borderSize) {
      nearestCornerClone.setZ(nearestCornerClone.getZ() + OFFSET);
    }

    final boolean isXNotNearCenter =
        nearestCornerClone.getX() < currentSector.getCenterX(), isZNotNearCenter =
        nearestCornerClone.getZ() < currentSector.getCenterZ();
    nearestCornerClone.setX(
        (nearestCornerClone.getX() + (isXNotNearCenter ? GROWTH : -GROWTH)) + (isXNotNearCenter
            ? -OFFSET : OFFSET));
    nearestCornerClone.setZ(
        (nearestCornerClone.getZ() + (isZNotNearCenter ? GROWTH : -GROWTH)) + (isZNotNearCenter
            ? -OFFSET : OFFSET));
    return nearestCornerClone;
  }

  private Location findNearestCorner(final Player player, final List<Location> cornerList) {
    Location nearestCorner = null;
    double nearestCornerDistance = Double.MAX_VALUE;
    for (final Location corner : cornerList) {
      final double cornerDistance = corner.distance(player.getLocation());
      if (cornerDistance < nearestCornerDistance) {
        nearestCorner = corner;
        nearestCornerDistance = cornerDistance;
      }
    }

    return nearestCorner;
  }
}
