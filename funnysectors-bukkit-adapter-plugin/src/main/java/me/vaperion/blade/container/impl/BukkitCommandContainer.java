//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package me.vaperion.blade.container.impl;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import me.vaperion.blade.annotation.Flag;
import me.vaperion.blade.command.BladeCommand;
import me.vaperion.blade.command.BladeParameter.CommandParameter;
import me.vaperion.blade.command.BladeParameter.FlagParameter;
import me.vaperion.blade.container.CommandContainer;
import me.vaperion.blade.container.ContainerCreator;
import me.vaperion.blade.context.BladeContext;
import me.vaperion.blade.context.impl.BukkitSender;
import me.vaperion.blade.exception.BladeExitMessage;
import me.vaperion.blade.exception.BladeUsageMessage;
import me.vaperion.blade.service.BladeCommandService;
import me.vaperion.blade.utils.MessageBuilder;
import me.vaperion.blade.utils.Tuple;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.help.GenericCommandHelpTopic;
import org.bukkit.plugin.SimplePluginManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BukkitCommandContainer extends Command implements CommandContainer {

  private static final Field COMMAND_MAP;
  private static final Field KNOWN_COMMANDS;
  public static final ContainerCreator<BukkitCommandContainer> CREATOR = BukkitCommandContainer::new;

  static {
    Field mapField = null;
    Field commandsField = null;

    try {
      mapField = SimplePluginManager.class.getDeclaredField("commandMap");
      mapField.setAccessible(true);
      commandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
      commandsField.setAccessible(true);
      Field modifiers = Field.class.getDeclaredField("modifiers");
      modifiers.setAccessible(true);
      modifiers.setInt(mapField, modifiers.getInt(mapField) & -17);
      modifiers.setInt(commandsField, modifiers.getInt(commandsField) & -17);
    } catch (Exception var3) {
      System.err.println("Failed to grab commandMap from the plugin manager.");
      var3.printStackTrace();
    }

    COMMAND_MAP = mapField;
    KNOWN_COMMANDS = commandsField;
  }

  private final BladeCommandService commandService;
  private final BladeCommand parentCommand;

  @SuppressWarnings("unchecked")
  private BukkitCommandContainer(@NotNull BladeCommandService service,
      @NotNull BladeCommand command, @NotNull String alias, @NotNull String fallbackPrefix)
      throws Exception {
    super(alias, command.getDescription(), "/" + alias, new ArrayList<>());
    this.commandService = service;
    this.parentCommand = command;
    SimplePluginManager simplePluginManager = (SimplePluginManager) Bukkit.getServer()
        .getPluginManager();
    SimpleCommandMap simpleCommandMap = (SimpleCommandMap) COMMAND_MAP.get(simplePluginManager);
    synchronized (Bukkit.getServer()) {
      Map<String, Command> knownCommands = (Map<String, Command>) KNOWN_COMMANDS.get(
          simpleCommandMap);
      if (service.isOverrideCommands()) {
        for (Command registeredCommand : new ArrayList<>(knownCommands.values())) {
          if (this.doesBukkitCommandConflict(registeredCommand, alias, command)) {
            registeredCommand.unregister(simpleCommandMap);
          }
        }
      }

      simpleCommandMap.register(fallbackPrefix, this);
      knownCommands.put(alias.toLowerCase().trim(), this);
      knownCommands.put(fallbackPrefix.toLowerCase().trim() + ":" + alias.toLowerCase().trim(),
          this);
      KNOWN_COMMANDS.set(simpleCommandMap, knownCommands);
      Bukkit.getHelpMap().addTopic(new GenericCommandHelpTopic(this));
    }
  }

  private boolean doesBukkitCommandConflict(@NotNull Command bukkitCommand, @NotNull String alias,
      @NotNull BladeCommand bladeCommand) {
    if (bukkitCommand instanceof BukkitCommandContainer) {
      return false;
    } else if (!bukkitCommand.getName().equalsIgnoreCase(alias) && bukkitCommand.getAliases()
        .stream().noneMatch((a) -> a.equalsIgnoreCase(alias))) {
      String[] var4 = bladeCommand.getRealAliases();
      int var5 = var4.length;

      for (String realAlias : var4) {
        if (bukkitCommand.getName().equalsIgnoreCase(realAlias) || bukkitCommand.getAliases()
            .stream().anyMatch((a) -> a.equalsIgnoreCase(realAlias))) {
          return true;
        }
      }

      return false;
    } else {
      return true;
    }
  }

  @Nullable
  private Tuple<BladeCommand, String> resolveCommand(@NotNull String[] arguments)
      throws BladeExitMessage {
    return this.commandService.getCommandResolver().resolveCommand(arguments);
  }

  @NotNull
  private String getSenderType(@NotNull Class<?> clazz) {
    String var2 = clazz.getSimpleName();
    byte var3 = -1;
    switch (var2.hashCode()) {
      case -1954273943:
        if (var2.equals("ConsoleCommandSender")) {
          var3 = 1;
        }
        break;
      case -1901885695:
        if (var2.equals("Player")) {
          var3 = 0;
        }
    }

    switch (var3) {
      case 0:
        return "players";
      case 1:
        return "the console";
      default:
        return "everyone";
    }
  }

  private void sendUsageMessage(@NotNull CommandSender sender, @NotNull String alias,
      @Nullable BladeCommand command) {
    if (command != null) {
      boolean hasDesc =
          command.getDescription() != null && !command.getDescription().trim().isEmpty();
      MessageBuilder builder = (new MessageBuilder(
          ChatColor.RED + "Poprawne użycie komendy /")).append(ChatColor.RED + alias);
      if (hasDesc) {
        builder.hover(Collections.singletonList(ChatColor.GRAY + command.getDescription()));
      }

      Optional.of(command.getFlagParameters()).ifPresent((flagParameters) -> {
        if (!flagParameters.isEmpty()) {
          builder.append(" ").append(ChatColor.RED + "(").reset();
          if (hasDesc) {
            builder.hover(
                Collections.singletonList(ChatColor.GRAY + command.getDescription().trim()));
          }

          int i = 0;
          Iterator var5 = flagParameters.iterator();

          while (var5.hasNext()) {
            FlagParameter flagParameter = (FlagParameter) var5.next();
            builder.append(i++ == 0 ? "" : ChatColor.GRAY + " | ").reset();
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
      Optional.of(command.getCommandParameters()).ifPresent((commandParameters) -> {
        if (!commandParameters.isEmpty()) {
          builder.append(" ");
          if (hasDesc) {
            builder.hover(
                Collections.singletonList(ChatColor.GRAY + command.getDescription().trim()));
          }

          int i = 0;
          Iterator var5 = commandParameters.iterator();

          while (var5.hasNext()) {
            CommandParameter commandParameter = (CommandParameter) var5.next();
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
          builder.hover(
              Collections.singletonList(ChatColor.GRAY + command.getDescription().trim()));
        }
      }

      builder.sendTo(sender);
    }
  }

  private boolean hasPermission(@NotNull CommandSender sender, String[] args)
      throws BladeExitMessage {
    Tuple<BladeCommand, String> command = this.resolveCommand(
        this.joinAliasToArgs(this.parentCommand.getAliases()[0], args));
    BladeContext context = new BladeContext(this.commandService, new BukkitSender(sender),
        command == null ? "" : command.getRight(), args);
    return this.checkPermission(context,
        command == null ? null : command.getLeft()).getLeft();
  }

  private Tuple<Boolean, String> checkPermission(@NotNull BladeContext context,
      @Nullable BladeCommand command) throws BladeExitMessage {
    return command == null ? new Tuple(false,
        "This command failed to execute as we couldn't find its registration.")
        : new Tuple(this.commandService.getPermissionTester().testPermission(context, command),
            command.getPermissionMessage());
  }

  private String[] joinAliasToArgs(String alias, String[] args) {
    String[] aliasParts = alias.split(" ");
    String[] argsWithAlias = new String[args.length + aliasParts.length];
    System.arraycopy(aliasParts, 0, argsWithAlias, 0, aliasParts.length);
    System.arraycopy(args, 0, argsWithAlias, aliasParts.length, args.length);
    return argsWithAlias;
  }

  public boolean testPermissionSilent(@NotNull CommandSender sender) {
    return this.hasPermission(sender, new String[0]);
  }

  public boolean execute(@NotNull CommandSender sender, @NotNull String alias,
      @NotNull String[] args) {
    BladeCommand command = null;

    try {
      String[] joined = this.joinAliasToArgs(alias, args);
      BladeContext context = new BladeContext(this.commandService, new BukkitSender(sender), alias,
          args);
      Tuple<BladeCommand, String> resolved = this.resolveCommand(joined);
      if (resolved != null) {
        Tuple<Boolean, String> permissionResult = this.checkPermission(context,
            resolved.getLeft());
        if (!(Boolean) permissionResult.getLeft()) {
          throw new BladeExitMessage(permissionResult.getRight());
        }

        command = resolved.getLeft();
        String resolvedAlias = resolved.getRight();
        int offset = Math.min(args.length, resolvedAlias.split(" ").length - 1);
        if (command.isSenderParameter() && !command.getSenderType().isInstance(sender)) {
          throw new BladeExitMessage(
              "This command can only be executed by " + this.getSenderType(command.getSenderType())
                  + ".");
        }

        BladeCommand finalCommand = command;
        Runnable runnable = () -> {
          try {
            List parsed;
            if (finalCommand.isContextBased()) {
              parsed = Collections.singletonList(context);
            } else {
              parsed = this.commandService.getCommandParser().parseArguments(finalCommand, context,
                  Arrays.copyOfRange(args, offset, args.length));
              if (finalCommand.isSenderParameter()) {
                parsed.add(0, sender);
              }
            }

            finalCommand.getMethod().setAccessible(true);
            finalCommand.getMethod()
                .invoke(finalCommand.getInstance(), parsed.toArray(new Object[0]));
          } catch (BladeUsageMessage var8) {
            this.sendUsageMessage(sender, resolvedAlias, finalCommand);
          } catch (BladeExitMessage var9) {
            sender.sendMessage(ChatColor.RED + var9.getMessage());
          } catch (InvocationTargetException var10) {
            if (var10.getTargetException() != null) {
              if (var10.getTargetException() instanceof BladeUsageMessage) {
                this.sendUsageMessage(sender, resolvedAlias, finalCommand);
                return;
              }

              if (var10.getTargetException() instanceof BladeExitMessage) {
                sender.sendMessage(ChatColor.RED + var10.getTargetException().getMessage());
                return;
              }
            }

            var10.printStackTrace();
            sender.sendMessage(
                ChatColor.RED + "An exception was thrown while executing this command.");
          } catch (Throwable var11) {
            var11.printStackTrace();
            sender.sendMessage(
                ChatColor.RED + "An exception was thrown while executing this command.");
          }

        };
        if (command.isAsync()) {
          this.commandService.getAsyncExecutor().accept(runnable);
        } else {
          long time = System.nanoTime();
          runnable.run();
          long elapsed = (System.nanoTime() - time) / 1000000L;
          if (elapsed >= this.commandService.getExecutionTimeWarningThreshold()) {
            Bukkit.getLogger().warning(
                String.format("[Blade] Command '%s' (%s#%s) took %d milliseconds to execute!",
                    resolvedAlias, command.getMethod().getDeclaringClass().getName(),
                    command.getMethod().getName(), elapsed));
          }
        }

        return true;
      }

      List<BladeCommand> availableCommands = this.commandService.getAllBladeCommands().stream()
          .filter((c) -> Arrays.stream(c.getAliases()).anyMatch((a) ->
              a.toLowerCase().startsWith(alias.toLowerCase(Locale.ROOT) + " ")
                  || a.equalsIgnoreCase(alias)))
          .filter((c) -> this.checkPermission(context, c).getLeft()).collect(Collectors.toList());

      for (String line : this.commandService.getHelpGenerator()
          .generate(context, availableCommands)) {
        sender.sendMessage(line);
      }

      return true;
    } catch (BladeUsageMessage var18) {
      this.sendUsageMessage(sender, alias, command);
    } catch (BladeExitMessage var19) {
      sender.sendMessage(ChatColor.RED + var19.getMessage());
    } catch (Throwable var20) {
      var20.printStackTrace();
      sender.sendMessage(ChatColor.RED + "An exception was thrown while executing this command.");
    }

    return false;
  }

  @NotNull
  public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias,
      @NotNull String[] args) throws IllegalArgumentException {
    if (!this.commandService.getTabCompleter().isDefault()) {
      return Collections.emptyList();
    } else if (!this.hasPermission(sender, args)) {
      return Collections.emptyList();
    } else {
      try {
        Tuple<BladeCommand, String> resolved = this.resolveCommand(
            this.joinAliasToArgs(alias, args));
        if (resolved == null) {
          return Collections.emptyList();
        }

        BladeCommand command = resolved.getLeft();
        String foundAlias = resolved.getRight();
        List<String> argList = new ArrayList(Arrays.asList(args));
        if (foundAlias.split(" ").length > 1) {
          argList.subList(0, foundAlias.split(" ").length - 1).clear();
        }

        if (argList.isEmpty()) {
          argList.add("");
        }

        String[] actualArguments = argList.toArray(new String[0]);
        BladeContext context = new BladeContext(this.commandService, new BukkitSender(sender),
            foundAlias, actualArguments);
        return this.commandService.getCommandCompleter().suggest(context, command, actualArguments);
      } catch (BladeExitMessage var10) {
        sender.sendMessage(ChatColor.RED + var10.getMessage());
      } catch (Exception var11) {
        var11.printStackTrace();
        sender.sendMessage(
            ChatColor.RED + "An exception was thrown while completing this command.");
      }

      return Collections.emptyList();
    }
  }

  public BladeCommandService getCommandService() {
    return this.commandService;
  }

  public BladeCommand getParentCommand() {
    return this.parentCommand;
  }
}
