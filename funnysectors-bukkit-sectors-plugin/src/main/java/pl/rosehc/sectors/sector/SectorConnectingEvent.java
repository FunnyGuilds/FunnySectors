package pl.rosehc.sectors.sector;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import pl.rosehc.adapter.helper.EventCompletionStage;

public final class SectorConnectingEvent extends PlayerEvent implements Cancellable {

  private static final HandlerList HANDLERS = new HandlerList();
  private final boolean teleport;
  private final boolean portal;

  private Sector sector;
  private EventCompletionStage completionStage;
  private boolean cancelled;

  public SectorConnectingEvent(final Player player, final Sector sector, final boolean teleport,
      final boolean portal) {
    super(player);
    this.sector = sector;
    this.teleport = teleport;
    this.portal = portal;
  }

  public static HandlerList getHandlerList() {
    return HANDLERS;
  }

  @Override
  public HandlerList getHandlers() {
    return HANDLERS;
  }

  public Sector getSector() {
    return this.sector;
  }

  public void setSector(final Sector sector) {
    this.sector = sector;
  }

  public EventCompletionStage getCompletionStage() {
    return this.completionStage;
  }

  public void setCompletionStage(final EventCompletionStage completionStage) {
    this.completionStage = completionStage;
  }

  public boolean isTeleport() {
    return this.teleport;
  }

  public boolean isPortal() {
    return this.portal;
  }

  public boolean isCancelled() {
    return this.cancelled;
  }

  public void setCancelled(final boolean cancelled) {
    this.cancelled = cancelled;
  }
}