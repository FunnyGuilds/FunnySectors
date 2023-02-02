package pl.rosehc.adapter.helper;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.projectiles.ProjectileSource;

/**
 * @author stevimeister on 09/01/2022
 **/
public final class PlayerHelper {

  public static Player getPlayer(final Entity entity) {
    if (entity instanceof Player) {
      return (Player) entity;
    }

    if (entity instanceof Projectile) {
      final ProjectileSource projectileSource = ((Projectile) entity).getShooter();

      if (!(projectileSource instanceof Player)) {
        return null;
      }

      return (Player) projectileSource;
    }

    return null;
  }

  public static void playSound(final Player player, final Sound sound) {
    player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
  }

  public static void playSound(final Player player, final Location location, final Sound sound) {
    player.playSound(location, sound, 1.0f, 1.0f);
  }

  public static void playSound(final Location location, Sound sound) {
    location.getWorld().playSound(location, sound, 1.0f, 1.0f);
  }
}
