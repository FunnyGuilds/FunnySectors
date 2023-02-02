package pl.rosehc.platform.vanishingblock.antigrief;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockEvent;

public final class AntiGriefBlockPlaceEvent extends BlockEvent implements Cancellable {

  private static final HandlerList HANDLER_LIST = new HandlerList();
  private final Player player;
  private boolean cancelled;

  public AntiGriefBlockPlaceEvent(final Block block, final Player player) {
    super(block);
    this.player = player;
  }

  public static HandlerList getHandlerList() {
    return HANDLER_LIST;
  }

  public Player getPlayer() {
    return this.player;
  }

  @Override
  public HandlerList getHandlers() {
    return HANDLER_LIST;
  }

  @Override
  public boolean isCancelled() {
    return this.cancelled;
  }

  @Override
  public void setCancelled(final boolean cancelled) {
    this.cancelled = cancelled;
  }
}
