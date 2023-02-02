package pl.rosehc.platform.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import me.vaperion.blade.argument.BladeArgument;
import me.vaperion.blade.argument.BladeProvider;
import me.vaperion.blade.bindings.Binding;
import me.vaperion.blade.context.BladeContext;
import me.vaperion.blade.exception.BladeExitMessage;
import me.vaperion.blade.service.BladeCommandService;
import org.bukkit.GameMode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.adapter.helper.StringHelper;
import pl.rosehc.controller.wrapper.global.BarColorWrapper;
import pl.rosehc.controller.wrapper.global.BarStyleWrapper;
import pl.rosehc.platform.PlatformPlugin;
import pl.rosehc.platform.rank.Rank;
import pl.rosehc.platform.user.PlatformUser;
import pl.rosehc.sectors.SectorsPlugin;
import pl.rosehc.sectors.sector.Sector;
import pl.rosehc.sectors.sector.user.SectorUser;

public final class PlatformCommandBindings implements Binding {

  private final PlatformPlugin plugin;

  public PlatformCommandBindings(final PlatformPlugin plugin) {
    this.plugin = plugin;
  }

  private static List<String> provideUserSuggestions(final BladeContext context,
      final String input) {
    final List<String> completions = new ArrayList<>();
    if (input.trim().length() < 2) {
      return completions;
    }

    for (final SectorUser user : SectorsPlugin.getInstance().getSectorUserFactory().getUserMap()
        .values()) {
      if (user.getNickname().toLowerCase().startsWith(input)) {
        completions.add(user.getNickname());
      }
    }

    return completions;
  }

  private static <T extends Enum<T>> void bindEnumProvider(final BladeCommandService service,
      final Class<T> enumType, final Enum<T>[] enumValues) {
    service.bindProvider(enumType, new BladeProvider<T>() {

      @Override
      public @NotNull List<String> suggest(final @NotNull BladeContext context,
          final @NotNull BladeArgument argument) throws BladeExitMessage {
        final List<String> completions = new ArrayList<>();
        final String input = argument.getString();
        for (final Enum<T> enumValue : enumValues) {
          if (enumValue.name().toLowerCase().startsWith(input)) {
            completions.add(enumValue.name());
          }
        }

        return completions;
      }

      @Override
      public @Nullable T provide(final @NotNull BladeContext context,
          final @NotNull BladeArgument argument) throws BladeExitMessage {
        final String input = argument.getString();
        if (Objects.nonNull(input) && !input.equals("null")) {
          try {
            return Enum.valueOf(enumType, input.toUpperCase());
          } catch (final Exception ignored) {
            throw new BladeExitMessage("No enum constant by name " + input + " was found.");
          }
        }

        return null;
      }
    });
  }

  @Override
  public void bind(final BladeCommandService service) {
    final BiFunction<BladeContext, String, List<String>> userSuggestionProvider = PlatformCommandBindings::provideUserSuggestions;
    service.bindProvider(Sector.class, new BladeProvider<Sector>() {

      @Override
      public @NotNull List<String> suggest(final @NotNull BladeContext context,
          final @NotNull BladeArgument argument) throws BladeExitMessage {
        final List<String> completions = new ArrayList<>();
        final String input = argument.getString();
        for (final Sector sector : SectorsPlugin.getInstance().getSectorFactory().getSectorMap()
            .values()) {
          if (sector.getName().toLowerCase().startsWith(input)) {
            completions.add(sector.getName());
          }
        }

        return completions;
      }

      @Override
      public @Nullable Sector provide(final @NotNull BladeContext context,
          final @NotNull BladeArgument argument) throws BladeExitMessage {
        final String input = argument.getString();
        return Objects.nonNull(input) && !input.equals("null") ? SectorsPlugin.getInstance()
            .getSectorFactory().findSector(input).orElseThrow(() -> new BladeExitMessage(
                ChatHelper.colored(
                    plugin.getPlatformConfiguration().messagesWrapper.sectorNotFound.replace(
                        "{SECTOR_NAME}", input)))) : null;
      }
    });
    service.bindProvider(PlatformUser.class, new BladeProvider<PlatformUser>() {

      @Override
      public @Nullable PlatformUser provide(final @NotNull BladeContext context,
          final @NotNull BladeArgument argument) throws BladeExitMessage {
        final String input = argument.getString();
        return Objects.nonNull(input) && !input.equals("null") ? PlatformPlugin.getInstance()
            .getPlatformUserFactory().findUserByNickname(input).orElseThrow(
                () -> new BladeExitMessage(ChatHelper.colored(
                    plugin.getPlatformConfiguration().messagesWrapper.playerNotFound.replace(
                        "{PLAYER_NAME}", input)))) : null;
      }

      @Override
      public @NotNull List<String> suggest(final @NotNull BladeContext context,
          final @NotNull BladeArgument argument) throws BladeExitMessage {
        return userSuggestionProvider.apply(context, argument.getString());
      }
    });
    service.bindProvider(SectorUser.class, new BladeProvider<SectorUser>() {

      @Override
      public @Nullable SectorUser provide(final @NotNull BladeContext context,
          final @NotNull BladeArgument argument) throws BladeExitMessage {
        final String input = argument.getString();
        return Objects.nonNull(input) && !input.equals("null") ? SectorsPlugin.getInstance()
            .getSectorUserFactory().findUserByNickname(input).orElseThrow(
                () -> new BladeExitMessage(ChatHelper.colored(
                    plugin.getPlatformConfiguration().messagesWrapper.playerIsOffline.replace(
                        "{PLAYER_NAME}", input)))) : null;
      }

      @Override
      public @NotNull List<String> suggest(final @NotNull BladeContext context,
          final @NotNull BladeArgument argument) throws BladeExitMessage {
        return userSuggestionProvider.apply(context, argument.getString());
      }
    });
    service.bindProvider(Rank.class, (context, argument) -> {
      final String input = argument.getString();
      return Objects.nonNull(input) && !input.equals("null") ? this.plugin.getRankFactory()
          .findRank(input).orElseThrow(this::createRankNotFoundMessage) : null;
    });
    service.bindProvider(GameMode.class, (context, argument) -> {
      final String input = argument.getString();
      return Objects.nonNull(input) && !input.equals("null") ? this.getGameMode(input) : null;
    });
    bindEnumProvider(service, BarStyleWrapper.class, BarStyleWrapper.values());
    bindEnumProvider(service, BarColorWrapper.class, BarColorWrapper.values());
  }

  private BladeExitMessage createRankNotFoundMessage() {
    final StringBuilder builder = new StringBuilder(
        ChatHelper.colored(this.plugin.getPlatformConfiguration().messagesWrapper.rankNotFound)
            + "\n");
    builder.append(
        ChatHelper.colored(this.plugin.getPlatformConfiguration().messagesWrapper.rankListStart));
    builder.append('\n');
    for (final Rank rank : this.plugin.getRankFactory().getRankMap().values()) {
      builder.append(ChatHelper.colored(
          this.plugin.getPlatformConfiguration().messagesWrapper.rankListFormat.replace(
              "{RANK_NAME}", rank.getName())));
      builder.append('\n');
    }

    return new BladeExitMessage(builder.toString());
  }

  private GameMode getGameMode(final String string) {
    final GameMode gameMode = StringHelper.equals(string, "survival", "0", "s") ? GameMode.SURVIVAL
        : StringHelper.equals(string, "creative", "1", "c") ? GameMode.CREATIVE
            : StringHelper.equals(string, "adventure", "2", "a") ? GameMode.ADVENTURE
                : StringHelper.equals(string, "spectator", "3", "sp") ? GameMode.SPECTATOR : null;
    if (gameMode == null) {
      throw new BladeExitMessage(ChatHelper.colored(
          this.plugin.getPlatformConfiguration().messagesWrapper.gameModeNotFound));
    }

    return gameMode;
  }
}
