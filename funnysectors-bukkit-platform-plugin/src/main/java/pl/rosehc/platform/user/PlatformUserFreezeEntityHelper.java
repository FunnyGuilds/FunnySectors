package pl.rosehc.platform.user;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.server.v1_8_R3.EntityHorse;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftHorse;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;

public final class PlatformUserFreezeEntityHelper {

  private static final Map<UUID, Horse> SPAWNED_HORSE_MAP = new HashMap<>();

  private PlatformUserFreezeEntityHelper() {
  }

  public static void spawnAndMountEntity(final Player player) {
    if (player.hasPermission("platform-freeze-bypass")) {
      return;
    }

    final Location location = player.getLocation().clone();
    location.setY(
        location.getWorld().getHighestBlockYAt(location.getBlockX(), location.getBlockZ()));
    final Horse horse = (Horse) location.getWorld().spawnEntity(location, EntityType.HORSE);
    final EntityHorse handle = ((CraftHorse) horse).getHandle();
    horse.setAdult();
    horse.setCarryingChest(false);
    horse.setAgeLock(true);
    horse.setCanPickupItems(false);
    handle.getDataWatcher().watch(15, (byte) 1);
    handle.setInvisible(true);
    handle.setVariant(-1);
    player.teleport(location);
    horse.setPassenger(player);
    SPAWNED_HORSE_MAP.put(player.getUniqueId(), horse);
  }

  public static void removeAndDisMountEntity(final Player player) {
    final Horse horse = SPAWNED_HORSE_MAP.remove(player.getUniqueId());
    if (Objects.nonNull(horse)) {
      if (player.isInsideVehicle()) {
        player.leaveVehicle();
      }

      horse.remove();
    }
  }
}
