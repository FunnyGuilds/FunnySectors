package pl.rosehc.platform.user.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import pl.rosehc.platform.user.PlatformUser;

public final class PlatformUserDropLevelUpEvent extends Event {

  private static final HandlerList HANDLER_LIST = new HandlerList();
  private final PlatformUser user;
  private final int previousLevel, newLevel;

  public PlatformUserDropLevelUpEvent(final PlatformUser user, final int previousLevel,
      final int newLevel) {
    this.user = user;
    this.previousLevel = previousLevel;
    this.newLevel = newLevel;
  }

  public static HandlerList getHandlerList() {
    return HANDLER_LIST;
  }

  public PlatformUser getUser() {
    return this.user;
  }

  public int getPreviousLevel() {
    return this.previousLevel;
  }

  public int getNewLevel() {
    return this.newLevel;
  }

  @Override
  public HandlerList getHandlers() {
    return HANDLER_LIST;
  }
}
