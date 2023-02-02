package pl.rosehc.platform.user.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import pl.rosehc.platform.PlatformConfiguration.CustomItemType;

public final class PlatformUserUseCustomItemEvent extends PlayerEvent {

  private static final HandlerList HANDLER_LIST = new HandlerList();
  private final CustomItemType customItemType;

  public PlatformUserUseCustomItemEvent(final Player player, final CustomItemType customItemType) {
    super(player);
    this.customItemType = customItemType;
  }

  public static HandlerList getHandlerList() {
    return HANDLER_LIST;
  }

  public CustomItemType getCustomItemType() {
    return this.customItemType;
  }

  @Override
  public HandlerList getHandlers() {
    return HANDLER_LIST;
  }
}
