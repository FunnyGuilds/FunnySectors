package pl.rosehc.platform.vanishingblock;

import org.bukkit.Location;
import org.bukkit.Material;

public final class VanishingBlock {

  private final Location location;
  private final Material type;
  private final long vanishTime;

  public VanishingBlock(final Location location, final Material type, final long vanishTime) {
    this.location = location;
    this.type = type;
    this.vanishTime = vanishTime;
  }

  public Location getLocation() {
    return this.location;
  }

  public Material getType() {
    return this.type;
  }

  public boolean canVanish() {
    return this.vanishTime <= System.currentTimeMillis() && this.location.getWorld()
        .isChunkLoaded(this.location.getBlockX() >> 4, this.location.getBlockZ() >> 4);
  }

  public void vanish() {
    this.location.getBlock().setType(Material.AIR);
  }
}
