package pl.rosehc.platform.end;

import java.util.Objects;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import pl.rosehc.platform.PlatformPlugin;

public final class EndHelper {

  private EndHelper() {
  }

  public static boolean canWaterSkill(final Player player) {
    if (player.hasMetadata("shockwave-water-skill")) {
      for (final MetadataValue value : player.getMetadata("shockwave-water-skill")) {
        if (value.value() instanceof Long) {
          if (value.asLong() > System.currentTimeMillis()) {
            return true;
          }

          value.invalidate();
        }
      }
    }

    return false;
  }

  public static int checkPortalPoint(final Location location) {
    final EndPortalPoint currentPortalPoint = PlatformPlugin.getInstance().getEndPointFactory()
        .getCurrentPortalPoint();
    return Objects.nonNull(currentPortalPoint) ? currentPortalPoint.isInside(location) ? 1 : 0 : -1;
  }

  public static void enableWaterSkill(final Player player) {
    player.setMetadata("shockwave-water-skill",
        new FixedMetadataValue(PlatformPlugin.getInstance(), System.currentTimeMillis() + 10_000L));
  }

  public static void disableWaterSkill(final Player player) {
    player.removeMetadata("shockwave-water-skill", PlatformPlugin.getInstance());
  }
}
