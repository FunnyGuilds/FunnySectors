package pl.rosehc.platform.listener.entity;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import pl.rosehc.platform.PlatformPlugin;
import pl.rosehc.platform.user.PlatformUser;

public final class EntityDamageListener implements Listener {

  private final PlatformPlugin plugin;

  public EntityDamageListener(final PlatformPlugin plugin) {
    this.plugin = plugin;
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onDamage(final EntityDamageEvent event) {
    if (event.getEntity() instanceof Player && this.plugin.getPlatformUserFactory()
        .findUserByUniqueId(event.getEntity().getUniqueId()).filter(PlatformUser::isGod)
        .isPresent()) {
      event.setCancelled(true);
    }
  }
}
