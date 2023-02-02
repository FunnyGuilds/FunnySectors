package pl.rosehc.sectors.sector.user;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public final class SectorUserJoinEvent extends PlayerEvent {

  private static final HandlerList HANDLER_LIST = new HandlerList();
  private final SectorUser user;

  public SectorUserJoinEvent(SectorUser user, Player player) {
    super(player);
    this.user = user;
  }

  public static HandlerList getHandlerList() {
    return HANDLER_LIST;
  }

  public SectorUser getUser() {
    return this.user;
  }

  @Override
  public HandlerList getHandlers() {
    return HANDLER_LIST;
  }
}
