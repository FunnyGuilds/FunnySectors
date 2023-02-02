package pl.rosehc.adapter.helper;

import java.util.Collection;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * @author stevimeister on 19/11/2021
 **/
public final class ChatHelper {

  private ChatHelper() {
  }

  public static String colored(final String text) {
    return ChatColor.translateAlternateColorCodes('&', text
        .replace(">>", "\u00BB")
        .replace("<<", "\u00AB"));
  }

  public static void sendMessage(final CommandSender sender, final String text) {
    sender.sendMessage(colored(text));
  }

  public static void sendMessage(final CommandSender commandSender,
      final Collection<? extends String> collection) {
    collection.forEach(text -> sendMessage(commandSender, text));
  }

  public static void sendMessage(final Collection<? extends CommandSender> senders,
      final String text) {
    for (final CommandSender sender : senders) {
      sendMessage(sender, text);
    }
  }

  public static void sendMessage(final Collection<? extends CommandSender> senders,
      final String permission, final String text) {
    for (final CommandSender sender : senders) {
      if (!sender.hasPermission(permission)) {
        continue;
      }

      sendMessage(sender, text);
    }
  }

  public static void sendTitle(final ProxiedPlayer player, final String title,
      final String subTitle) {
    player.sendTitle(ProxyServer.getInstance().createTitle()
        .title(new TextComponent(ChatHelper.colored(title)))
        .subTitle(new TextComponent(ChatHelper.colored(subTitle)))
        .fadeIn(15)
        .stay(30)
        .fadeOut(15));
  }
}


