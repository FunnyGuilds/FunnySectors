package pl.rosehc.platform.vanishingblock;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Location;
import org.bukkit.Material;

public final class VanishingBlockFactory {

  private final Set<VanishingBlock> vanishingBlockSet = ConcurrentHashMap.newKeySet();

  public boolean isVanished(final Location blockLocation, final Material type) {
    for (final VanishingBlock vanishingBlock : this.vanishingBlockSet) {
      final Location vanishingBlockLocation = vanishingBlock.getLocation();
      if (vanishingBlockLocation.getBlockX() == blockLocation.getBlockX()
          && vanishingBlockLocation.getBlockY() == blockLocation.getBlockY()
          && vanishingBlockLocation.getBlockZ() == blockLocation.getBlockZ()
          && vanishingBlock.getType().equals(type)) {
        return true;
      }
    }

    return false;
  }

  public boolean unVanish(final Location location, final Material type) {
    return this.vanishingBlockSet.removeIf(
        block -> block.getLocation().getBlockX() == location.getBlockX()
            && block.getLocation().getBlockY() == location.getBlockY()
            && block.getLocation().getBlockZ() == location.getBlockZ() && block.getType()
            .equals(type));
  }

  public void reset(final boolean all) {
    int taken = 0;
    for (final VanishingBlock vanishingBlock : this.vanishingBlockSet) {
      if (!all && taken++ > 35) {
        break;
      }

      if (vanishingBlock.canVanish() || all) {
        vanishingBlock.vanish();
        this.vanishingBlockSet.remove(vanishingBlock);
      }
    }
  }

  public void vanish(final VanishingBlock vanishingBlock) {
    this.vanishingBlockSet.add(vanishingBlock);
  }
}
