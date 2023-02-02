package pl.rosehc.platform.user.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import pl.rosehc.platform.drop.Drop;
import pl.rosehc.platform.user.PlatformUser;

public final class PlatformUserDropChanceEvent extends Event {

  private static final HandlerList HANDLER_LIST = new HandlerList();
  private final PlatformUser user;
  private final Drop drop;
  private double chance;

  public PlatformUserDropChanceEvent(final PlatformUser user, final Drop drop,
      final double chance) {
    this.user = user;
    this.drop = drop;
    this.chance = chance;
  }

  public static HandlerList getHandlerList() {
    return HANDLER_LIST;
  }

  public PlatformUser getUser() {
    return this.user;
  }

  public Drop getDrop() {
    return this.drop;
  }

  public double getChance() {
    return this.chance;
  }

  public void setChance(final double chance) {
    this.chance = chance;
  }

  @Override
  public HandlerList getHandlers() {
    return HANDLER_LIST;
  }
}
