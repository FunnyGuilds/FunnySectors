package pl.rosehc.adapter.command;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import me.vaperion.blade.annotation.Flag;
import me.vaperion.blade.command.BladeCommand;
import me.vaperion.blade.command.BladeParameter;
import me.vaperion.blade.container.CommandContainer;
import me.vaperion.blade.container.ContainerCreator;
import me.vaperion.blade.context.BladeContext;
import me.vaperion.blade.exception.BladeExitMessage;
import me.vaperion.blade.exception.BladeUsageMessage;
import me.vaperion.blade.service.BladeCommandService;
import me.vaperion.blade.utils.MessageBuilder;
import me.vaperion.blade.utils.Tuple;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.rosehc.adapter.AdapterPlugin;

public final class BungeeCommandContainer extends Command implements CommandContainer, TabExecutor {

  public static final ContainerCreator<BungeeCommandContainer> CREATOR = BungeeCommandContainer::new;
  private final BladeCommandService service;
  private final BladeCommand command;
  private final String alias;

  public BungeeCommandContainer(final @NotNull BladeCommandService service,
      final @NotNull BladeCommand command, final @NotNull String alias,
      final @NotNull String ignored) {
    super(alias);
    this.service = service;
    this.command = command;
    this.alias = alias;
    ProxyServer.getInstance().getPluginManager().registerCommand(AdapterPlugin.getInstance(), this);
  }

  @Override
  public @NotNull BladeCommandService getCommandService() {
    return this.service;
  }

  @Override
  public @NotNull BladeCommand getParentCommand() {
    return this.command;
  }

  @Override
  public void execute(final CommandSender sender, final String[] args) {
    BladeCommand command = null;
    String resolvedAlias = alias;

    try {
      String[] joined = joinAliasToArgs(args);

      BladeContext context = new BladeContext(service, new BungeeSender(sender), alias, args);

      Tuple<BladeCommand, String> resolved = resolveCommand(joined);
      if (resolved == null) {
        List<BladeCommand> availableCommands = service.getAllBladeCommands()
            .stream().filter(c -> Arrays.stream(c.getAliases()).anyMatch(
                a -> a.toLowerCase().startsWith(alias.toLowerCase(Locale.ROOT) + " ")
                    || a.equalsIgnoreCase(alias)))
            .filter(c -> this.checkPermission(context, c).getLeft())
            .collect(Collectors.toList());

        for (String line : service.getHelpGenerator().generate(context, availableCommands)) {
          sender.sendMessage(line);
        }

        return;
      }

      Tuple<Boolean, String> permissionResult = checkPermission(context, resolved.getLeft());
      if (!permissionResult.getLeft()) {
        throw new BladeExitMessage(permissionResult.getRight());
      }

      command = resolved.getLeft();
      resolvedAlias = resolved.getRight();
      int offset = Math.min(args.length, resolvedAlias.split(" ").length - 1);

      if (command.isSenderParameter() && !command.getSenderType().isInstance(sender)) {
        throw new BladeExitMessage(
            "This command can only be executed by " + getSenderType(command.getSenderType()) + ".");
      }

      final BladeCommand finalCommand = command;
      final String finalResolvedAlias = resolvedAlias;

      Runnable runnable = () -> {
        try {
          List<Object> parsed;
          if (finalCommand.isContextBased()) {
            parsed = Collections.singletonList(context);
          } else {
            parsed = service.getCommandParser().parseArguments(finalCommand, context,
                Arrays.copyOfRange(args, offset, args.length));
            if (finalCommand.isSenderParameter()) {
              parsed.add(0, sender);
            }
          }

          finalCommand.getMethod().setAccessible(true);
          finalCommand.getMethod()
              .invoke(finalCommand.getInstance(), parsed.toArray(new Object[0]));
        } catch (BladeUsageMessage ex) {
          sendUsageMessage(sender, finalResolvedAlias, finalCommand);
        } catch (BladeExitMessage ex) {
          sender.sendMessage(ChatColor.RED + ex.getMessage());
        } catch (InvocationTargetException ex) {
          if (ex.getTargetException() != null) {
            if (ex.getTargetException() instanceof BladeUsageMessage) {
              sendUsageMessage(sender, finalResolvedAlias, finalCommand);
              return;
            } else if (ex.getTargetException() instanceof BladeExitMessage) {
              sender.sendMessage(ChatColor.RED + ex.getTargetException().getMessage());
              return;
            }
          }

          ex.printStackTrace();
          sender.sendMessage(
              ChatColor.RED + "An exception was thrown while executing this command.");
        } catch (Throwable t) {
          t.printStackTrace();
          sender.sendMessage(
              ChatColor.RED + "An exception was thrown while executing this command.");
        }
      };

      if (command.isAsync()) {
        service.getAsyncExecutor().accept(runnable);
      } else {
        long time = System.nanoTime();
        runnable.run();
        long elapsed = (System.nanoTime() - time) / 1000000;

        if (elapsed >= service.getExecutionTimeWarningThreshold()) {
          ProxyServer.getInstance().getLogger().warning(String.format(
              "[Blade] Command '%s' (%s#%s) took %d milliseconds to execute!",
              finalResolvedAlias,
              finalCommand.getMethod().getDeclaringClass().getName(),
              finalCommand.getMethod().getName(),
              elapsed
          ));
        }
      }
    } catch (BladeUsageMessage ex) {
      sendUsageMessage(sender, resolvedAlias, command);
    } catch (BladeExitMessage ex) {
      sender.sendMessage(ChatColor.RED + ex.getMessage());
    } catch (Throwable t) {
      t.printStackTrace();
      sender.sendMessage(ChatColor.RED + "An exception was thrown while executing this command.");
    }
  }

  private Tuple<Boolean, String> checkPermission(@NotNull BladeContext context,
      final @Nullable BladeCommand command) throws BladeExitMessage {
    if (command == null) {
      return new Tuple<>(false,
          "This command failed to execute as we couldn't find its registration.");
    }

    return new Tuple<>(
        service.getPermissionTester().testPermission(context, command),
        command.getPermissionMessage());
  }

  private String[] joinAliasToArgs(String[] args) {
    String[] aliasParts = alias.split(" ");
    String[] argsWithAlias = new String[args.length + aliasParts.length];
    System.arraycopy(aliasParts, 0, argsWithAlias, 0, aliasParts.length);
    System.arraycopy(args, 0, argsWithAlias, aliasParts.length, args.length);
    return argsWithAlias;
  }

  @Nullable
  private Tuple<BladeCommand, String> resolveCommand(@NotNull String[] arguments)
      throws BladeExitMessage {
    return service.getCommandResolver().resolveCommand(arguments);
  }

  @NotNull
  private String getSenderType(@NotNull Class<?> clazz) {
    switch (clazz.getSimpleName()) {
      case "ProxiedPlayer":
        return "players";

      case "ConsoleCommandSender":
        return "the console";

      default:
        return "everyone";
    }
  }


  private void sendUsageMessage(@NotNull CommandSender sender, @NotNull String alias,
      @Nullable BladeCommand command) {
    if (command == null) {
      return;
    }
    boolean hasDesc =
        command.getDescription() != null && !command.getDescription().trim().isEmpty();

    MessageBuilder builder = new MessageBuilder(ChatColor.RED + "Poprawne użycie komendy /").append(
        ChatColor.RED + alias);
    if (hasDesc) {
      builder.hover(Collections.singletonList(ChatColor.GRAY + command.getDescription()));
    }

    Optional.of(command.getFlagParameters())
        .ifPresent(flagParameters -> {
          if (!flagParameters.isEmpty()) {
            builder.append(" ").append(ChatColor.RED + "(").reset();
            if (hasDesc) {
              builder.hover(
                  Collections.singletonList(ChatColor.GRAY + command.getDescription().trim()));
            }

            int i = 0;
            for (BladeParameter.FlagParameter flagParameter : flagParameters) {
              builder.append(i++ == 0 ? "" : (ChatColor.GRAY + " | ")).reset();
              if (hasDesc) {
                builder.hover(
                    Collections.singletonList(ChatColor.GRAY + command.getDescription().trim()));
              }

              Flag flag = flagParameter.getFlag();

              builder.append(ChatColor.AQUA + "-" + flag.value());
              if (!flagParameter.isBooleanFlag()) {
                builder.append(ChatColor.AQUA + " <" + flagParameter.getName() + ">");
              }
              if (!flag.description().trim().isEmpty()) {
                builder.hover(
                    Collections.singletonList(ChatColor.YELLOW + flag.description().trim()));
              }
            }

            builder.append(ChatColor.RED + ")").reset();
            if (hasDesc) {
              builder.hover(
                  Collections.singletonList(ChatColor.GRAY + command.getDescription().trim()));
            }
          }
        });

    Optional.of(command.getCommandParameters())
        .ifPresent(commandParameters -> {
          if (!commandParameters.isEmpty()) {
            builder.append(" ");
            if (hasDesc) {
              builder.hover(
                  Collections.singletonList(ChatColor.GRAY + command.getDescription().trim()));
            }

            int i = 0;
            for (BladeParameter.CommandParameter commandParameter : commandParameters) {
              builder.append(i++ == 0 ? "" : " ");

              builder.append(ChatColor.RED + (commandParameter.isOptional() ? "(" : "<"));
              builder.append(ChatColor.RED + commandParameter.getName());
              builder.append(ChatColor.RED + (commandParameter.isOptional() ? ")" : ">"));
            }
          }
        });

    if (command.getExtraUsageData() != null && !command.getExtraUsageData().trim().isEmpty()) {
      builder.append(" ");
      builder.append(ChatColor.RED + command.getExtraUsageData());
      if (hasDesc) {
        builder.hover(Collections.singletonList(ChatColor.GRAY + command.getDescription().trim()));
      }
    }

    sender.sendMessage(builder.build());
  }

  @Override
  public Iterable<String> onTabComplete(final CommandSender sender, final String[] args) {
    if (!this.service.getTabCompleter().isDefault()) {
      return Collections.emptyList();
    } else if (!this.hasPermission(sender)) {
      return Collections.emptyList();
    } else {
      try {
        Tuple<BladeCommand, String> resolved = this.resolveCommand(this.joinAliasToArgs(args));
        if (resolved == null) {
          return Collections.emptyList();
        }

        BladeCommand command = resolved.getLeft();
        String foundAlias = resolved.getRight();
        List<String> argList = new ArrayList<>(Arrays.asList(args));
        if (foundAlias.split(" ").length > 1) {
          argList.subList(0, foundAlias.split(" ").length - 1).clear();
        }

        if (argList.isEmpty()) {
          argList.add("");
        }

        String[] actualArguments = argList.toArray(new String[0]);
        BladeContext context = new BladeContext(this.service, new BungeeSender(sender), foundAlias,
            actualArguments);
        return this.service.getCommandCompleter().suggest(context, command, actualArguments);
      } catch (BladeExitMessage ex) {
        sender.sendMessage(ChatColor.RED + ex.getMessage());
      } catch (Exception ex) {
        ex.printStackTrace();
        sender.sendMessage(
            ChatColor.RED + "An exception was thrown while completing this command.");
      }

      return Collections.emptyList();
    }
  }
}
