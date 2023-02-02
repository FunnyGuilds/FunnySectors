package pl.rosehc.adapter.helper;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * @author stevimeister on 03/10/2021
 **/
public final class ChatHelper {

  private ChatHelper() {
  }

  public static String colored(final String text) {
    return ChatColor.translateAlternateColorCodes('&', text)
        .replace(">>", "»")
        .replace("<<", "«");
  }

  public static List<String> colored(final List<String> text) {
    return text.stream().map(ChatHelper::colored).collect(Collectors.toList());
  }

  public static void sendMessage(final CommandSender sender, final String... messages) {
    sender.sendMessage(Arrays.stream(messages).filter(Objects::nonNull).map(ChatHelper::colored)
        .toArray(String[]::new));
  }

  public static void sendMessage(final Collection<? extends Player> players, final List<?> text) {
    for (final Object string : text) {
      sendMessage(players, colored(String.valueOf(string)));
    }
  }

  public static void sendMessage(final Collection<? extends Player> players, final String text) {
    players.forEach(player -> sendMessage(player, text));
  }

  public static void sendMessage(final CommandSender sender, final List<String> strings) {
    strings.forEach(text -> sendMessage(sender, text));
  }

  public static void sendTitle(final Player player, final String title, final String subTitle,
      final int fadeIn, final int stay, final int fadeOut) {
    final PacketPlayOutTitle packetPlayOutTitle = new PacketPlayOutTitle(
        PacketPlayOutTitle.EnumTitleAction.TITLE, new ChatComponentText(colored(title)));
    final PacketPlayOutTitle packetPlayOutSubTitle = new PacketPlayOutTitle(
        PacketPlayOutTitle.EnumTitleAction.SUBTITLE, new ChatComponentText(colored(subTitle)));
    final PacketPlayOutTitle packetPlayOutTitleLength = new PacketPlayOutTitle(fadeIn, stay,
        fadeOut);
    final PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;
    playerConnection.sendPacket(packetPlayOutTitle);
    playerConnection.sendPacket(packetPlayOutSubTitle);
    playerConnection.sendPacket(packetPlayOutTitleLength);
  }

  public static void sendTitle(final Player player, final String title, final String subTitle) {
    sendTitle(player, title, subTitle, 0, 50, 25);
  }
}
