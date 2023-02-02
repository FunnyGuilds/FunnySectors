package pl.rosehc.platform.end;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import pl.rosehc.adapter.helper.ItemHelper;
import pl.rosehc.platform.PlatformPlugin;

public final class EndShockwaveTask implements Runnable {

  private static final ItemStack BUCKET_ITEM = new ItemStack(Material.WATER_BUCKET);
  private static final Vector KNOCK_VECTOR = new Vector(0D, 3.5D, 0D);
  private final PlatformPlugin plugin;
  private long nextShockwaveRun, nextTourRun;
  private int tourIndex;

  public EndShockwaveTask(final PlatformPlugin plugin) {
    this.plugin = plugin;
    this.nextShockwaveRun = System.currentTimeMillis() + 900_000L;
    this.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(this.plugin, this, 20L, 20L);
  }

  @Override
  public void run() {
    if (this.nextShockwaveRun <= System.currentTimeMillis()
        && this.nextTourRun <= System.currentTimeMillis()) {
      this.nextTourRun = System.currentTimeMillis() + 150_00L;
      for (final Player player : this.plugin.getServer().getOnlinePlayers()) {
        if (player.hasPermission("platform-end-bypass")) {
          continue;
        }

        if (!ItemHelper.hasItem(player, BUCKET_ITEM, BUCKET_ITEM.getAmount())) {
          ItemHelper.addItem(player, BUCKET_ITEM);
        }

        EndHelper.enableWaterSkill(player);
        player.getWorld().strikeLightningEffect(player.getLocation());
        player.setVelocity(KNOCK_VECTOR);
      }

      this.tourIndex++;
      if (this.tourIndex >= 3) {
        this.nextShockwaveRun = System.currentTimeMillis() + 900_000L;
        this.tourIndex = 0;
        this.nextTourRun = 0L;
      }
    }
  }
}
