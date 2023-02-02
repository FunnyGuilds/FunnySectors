package pl.rosehc.adapter.nametag;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class NameTagPlayerEvent extends Event {

  private static final HandlerList HANDLER_LIST = new HandlerList();
  private final Player player, requester;
  private String prefix, suffix;

  public NameTagPlayerEvent(final Player player, final Player requester, final String prefix,
      final String suffix) {
    this.player = player;
    this.requester = requester;
    this.prefix = prefix;
    this.suffix = suffix;
  }

  public static HandlerList getHandlerList() {
    return HANDLER_LIST;
  }

  public Player getPlayer() {
    return this.player;
  }

  public Player getRequester() {
    return this.requester;
  }

  public String getPrefix() {
    return this.prefix;
  }

  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }

  public String getSuffix() {
    return this.suffix;
  }

  public void setSuffix(String suffix) {
    this.suffix = suffix;
  }

  @Override
  public HandlerList getHandlers() {
    return HANDLER_LIST;
  }
}
