package pl.rosehc.platform.listener.entity;

import java.util.Optional;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;
import pl.rosehc.actionbar.PrioritizedActionBarConstants;
import pl.rosehc.actionbar.PrioritizedActionBarPlugin;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.adapter.helper.TimeHelper;
import pl.rosehc.controller.packet.platform.user.PlatformUserCombatTimeUpdatePacket;
import pl.rosehc.platform.PlatformPlugin;

public final class EntityDamageByEntityListener implements Listener {

  private final PlatformPlugin plugin;

  public EntityDamageByEntityListener(final PlatformPlugin plugin) {
    this.plugin = plugin;
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onDamage(final EntityDamageByEntityEvent event) {
    if (event.getEntity() instanceof Player) {
      final Player victim = (Player) event.getEntity();
      //noinspection SpellCheckingInspection
      final Entity damager = event.getDamager();
      if ((damager instanceof FishHook || damager instanceof Arrow)
          && ((Projectile) damager).getShooter() instanceof Player) {
        if (damager instanceof Arrow && ((Projectile) damager).getShooter().equals(victim)) {
          event.setCancelled(true);
          return;
        }

        this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin,
            () -> PrioritizedActionBarPlugin.getInstance().getPrioritizedActionBarFactory()
                .updateActionBar(((Player) ((Projectile) damager).getShooter()).getUniqueId(),
                    ChatHelper.colored(
                        this.plugin.getPlatformConfiguration().messagesWrapper.healthInfoAfterProjectileHitActionBarInfo.replace(
                                "{PLAYER_NAME}", victim.getName())
                            .replace("{HEALTH}", String.format("%.2f", victim.getHealth()))),
                    PrioritizedActionBarConstants.HEALTH_INFO_AFTER_PROJECTILE_HIT_ACTION_BAR_PRIORITY));
      }

      this.plugin.getPlatformUserFactory().findUserByUniqueId(victim.getUniqueId())
          .filter(victimUser -> !victimUser.isGod()).ifPresent(
              victimUser -> this.findAttacker(event).filter(attacker -> !attacker.equals(victim))
                  .ifPresent(attacker -> this.plugin.getPlatformUserFactory()
                      .findUserByUniqueId(attacker.getUniqueId()).ifPresent(attackerUser -> {
                        final long combatTime = System.currentTimeMillis()
                            + this.plugin.getPlatformConfiguration().parsedCombatTime;
                        if (!victimUser.isInCombat() || !attackerUser.isInCombat()) {
                          if (!victimUser.isInCombat()) {
                            ChatHelper.sendMessage(victim,
                                this.plugin.getPlatformConfiguration().messagesWrapper.combatTaggedInfo.replace(
                                    "{PLAYER_NAME}", attacker.getName()));
                            PrioritizedActionBarPlugin.getInstance().getPrioritizedActionBarFactory()
                                .updateActionBar(victim.getUniqueId(), ChatHelper.colored(
                                        this.plugin.getPlatformConfiguration().messagesWrapper.combatLeftTimeInfo.replace(
                                            "{TIME}", TimeHelper.timeToString(
                                                this.plugin.getPlatformConfiguration().parsedCombatTime))),
                                    PrioritizedActionBarConstants.ANTI_LOGOUT_ACTION_BAR_PRIORITY);
                            this.plugin.getTimerTaskFactory().removeTimer(victim.getUniqueId());
                          }
                          if (!attackerUser.isInCombat()) {
                            ChatHelper.sendMessage(attacker,
                                this.plugin.getPlatformConfiguration().messagesWrapper.combatTaggedInfo.replace(
                                    "{PLAYER_NAME}", victim.getName()));
                            PrioritizedActionBarPlugin.getInstance().getPrioritizedActionBarFactory()
                                .updateActionBar(attacker.getUniqueId(), ChatHelper.colored(
                                        this.plugin.getPlatformConfiguration().messagesWrapper.combatLeftTimeInfo.replace(
                                            "{TIME}", TimeHelper.timeToString(
                                                this.plugin.getPlatformConfiguration().parsedCombatTime))),
                                    PrioritizedActionBarConstants.ANTI_LOGOUT_ACTION_BAR_PRIORITY);
                            this.plugin.getTimerTaskFactory().removeTimer(attacker.getUniqueId());
                          }
                        }

                        victimUser.setCombatTime(combatTime);
                        attackerUser.setCombatTime(combatTime);
                        this.plugin.getRedisAdapter().sendPacket(
                            new PlatformUserCombatTimeUpdatePacket(victimUser.getUniqueId(),
                                victimUser.getCombatTime()), "rhc_master_controller", "rhc_platform");
                        this.plugin.getRedisAdapter().sendPacket(
                            new PlatformUserCombatTimeUpdatePacket(attackerUser.getUniqueId(),
                                attackerUser.getCombatTime()), "rhc_master_controller", "rhc_platform");
                        this.tryUnblock(victim);
                        this.tryUnblock(attacker);
                      })));
    }
  }

  private Optional<Player> findAttacker(final EntityDamageByEntityEvent event) {
    final Entity attacker = event.getDamager();
    if (attacker instanceof Projectile) {
      final ProjectileSource shooter = ((Projectile) attacker).getShooter();
      return Optional.of(shooter).filter(Player.class::isInstance).map(Player.class::cast);
    }

    return Optional.of(attacker).filter(Player.class::isInstance).map(Player.class::cast);
  }

  private void tryUnblock(final Player player) {
    if (player.isBlocking()) {
      final ItemStack itemInHand = player.getItemInHand();
      player.setItemInHand(null);
      player.updateInventory();
      this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
        player.setItemInHand(itemInHand);
        player.updateInventory();
      });
    }
  }
}
