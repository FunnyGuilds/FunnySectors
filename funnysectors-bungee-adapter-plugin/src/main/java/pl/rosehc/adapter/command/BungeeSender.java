package pl.rosehc.adapter.command;

import me.vaperion.blade.context.WrappedSender;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class BungeeSender implements WrappedSender<CommandSender> {

  private final CommandSender commandSender;

  public BungeeSender(final CommandSender commandSender) {
    this.commandSender = commandSender;
  }

  @Override
  public @NotNull CommandSender getBackingSender() {
    return this.commandSender;
  }

  @Override
  public @NotNull String getName() {
    return this.commandSender.getName();
  }

  @Override
  public boolean hasPermission(final @NotNull String permissionNode) {
    return permissionNode.equals("console") ? this.commandSender instanceof ConsoleCommandSender
        : this.commandSender.hasPermission(permissionNode);
  }

  @Override
  public void sendMessage(final @NotNull String... messages) {
    for (final String message : messages) {
      this.sendMessage(message);
    }
  }

  @Override
  public void sendMessage(final @NotNull String message) {
    commandSender.sendMessage(TextComponent.fromLegacyText(message));
  }

  @Override
  public <S> @Nullable S parseAs(final @NotNull Class<S> clazz) {
    //noinspection unchecked
    return clazz.equals(ProxiedPlayer.class) && commandSender instanceof ProxiedPlayer
        ? (S) commandSender : clazz.equals(CommandSender.class) ? (S) commandSender : null;
  }
}
