package pl.rosehc.platform.hologram;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import org.bukkit.entity.Player;
import pl.rosehc.platform.PlatformPlugin;

public final class HologramChannelHandler extends ChannelDuplexHandler {

  private static final Field ENTITY_USE_ACTION_FIELD, ENTITY_ID_FIELD;

  static {
    try {
      ENTITY_USE_ACTION_FIELD = PacketPlayInUseEntity.class.getDeclaredField("action");
      ENTITY_ID_FIELD = PacketPlayInUseEntity.class.getDeclaredField("a");
      ENTITY_USE_ACTION_FIELD.setAccessible(true);
      ENTITY_ID_FIELD.setAccessible(true);
    } catch (final Exception e) {
      throw new ExceptionInInitializerError(e);
    }
  }

  private final PlatformPlugin plugin;
  private final Player owner;

  public HologramChannelHandler(final PlatformPlugin plugin, final Player owner) {
    this.plugin = plugin;
    this.owner = owner;
  }

  @Override
  public void channelRead(final ChannelHandlerContext context, final Object message)
      throws Exception {
    if (message instanceof PacketPlayInUseEntity) {
      final List<Hologram> hologramList = this.plugin.getHologramFactory()
          .findHologramList(this.owner.getUniqueId());
      final HologramActionType actionType = HologramActionType.fromOriginal(
          (Enum<?>) ENTITY_USE_ACTION_FIELD.get(message));
      final int entityId = (int) ENTITY_ID_FIELD.get(message);
      if (!hologramList.isEmpty()) {
        for (final Hologram hologram : hologramList) {
          final AtomicBoolean anyFound = new AtomicBoolean();
          hologram.findEntryBySlimeId(entityId).filter(entry -> Objects.nonNull(entry.getAction()))
              .ifPresent(entry -> {
                entry.getAction().accept(this.owner, hologram, entry, actionType);
                anyFound.set(true);
              });
          if (anyFound.get()) {
            break;
          }
        }
      }
    }

    super.channelRead(context, message);
  }
}
