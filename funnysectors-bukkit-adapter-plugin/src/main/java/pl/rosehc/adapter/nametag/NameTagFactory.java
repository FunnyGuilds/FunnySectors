package pl.rosehc.adapter.nametag;

import java.util.Objects;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardTeam;
import net.minecraft.server.v1_8_R3.Scoreboard;
import net.minecraft.server.v1_8_R3.ScoreboardTeam;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.adapter.helper.PacketHelper;

public final class NameTagFactory {

  private final Scoreboard scoreboard = new Scoreboard();
  private final Object mutex = new Object();

  public void createNameTag(final Player player) {
    synchronized (this.mutex) {
      final String name = player.getName();
      final ScoreboardTeam playerTeam = this.scoreboard.createTeam(name);
      playerTeam.setPrefix("");
      playerTeam.setDisplayName("");
      playerTeam.setSuffix("");

      this.scoreboard.addPlayerToTeam(name, name);
      final PacketPlayOutScoreboardTeam playerTeamPacket = new PacketPlayOutScoreboardTeam(
          playerTeam, 0);
      PacketHelper.sendPacket(player, playerTeamPacket);
      for (final Player requester : Bukkit.getOnlinePlayers()) {
        if (Objects.equals(requester, player)) {
          continue;
        }

        PacketHelper.sendPacket(requester, playerTeamPacket);
        final ScoreboardTeam requesterTeam = this.scoreboard.getPlayerTeam(requester.getName());
        if (!Objects.isNull(requesterTeam)) {
          PacketHelper.sendPacket(player, new PacketPlayOutScoreboardTeam(requesterTeam, 0));
        }
      }
    }
  }

  public void updateNameTag(final Player player) {
    synchronized (this.mutex) {
      final ScoreboardTeam playerTeam = this.scoreboard.getPlayerTeam(player.getName());
      if (Objects.isNull(playerTeam)) {
        return;
      }

      for (final Player requester : Bukkit.getOnlinePlayers()) {
        final NameTagPlayerEvent event = new NameTagPlayerEvent(player, requester, "", "");
        Bukkit.getPluginManager().callEvent(event);
        if (event.getPrefix().length() >= 64) {
          event.setPrefix(event.getPrefix().substring(0, 63));
        }

        if (event.getSuffix().length() >= 64) {
          event.setSuffix(event.getSuffix().substring(0, 63));
        }

        playerTeam.setPrefix(ChatHelper.colored(event.getPrefix()));
        playerTeam.setSuffix(ChatHelper.colored(event.getSuffix()));
        PacketHelper.sendPacket(requester, new PacketPlayOutScoreboardTeam(playerTeam, 2));
      }
    }
  }

  public void removeNameTag(final Player player) {
    synchronized (this.mutex) {
      final ScoreboardTeam playerTeam = this.scoreboard.getPlayerTeam(player.getName());
      if (Objects.isNull(playerTeam)) {
        return;
      }

      this.scoreboard.removePlayerFromTeam(player.getName(), playerTeam);
      final PacketPlayOutScoreboardTeam playerTeamPacket = new PacketPlayOutScoreboardTeam(
          playerTeam, 1);
      PacketHelper.sendPacket(player, playerTeamPacket);
      for (final Player requester : Bukkit.getOnlinePlayers()) {
        if (Objects.equals(player, requester)) {
          continue;
        }

        PacketHelper.sendPacket(requester, playerTeamPacket);
        final ScoreboardTeam requesterTeam = this.scoreboard.getPlayerTeam(requester.getName());
        if (Objects.nonNull(requesterTeam)) {
          PacketHelper.sendPacket(player, new PacketPlayOutScoreboardTeam(requesterTeam, 1));
        }
      }

      this.scoreboard.removeTeam(playerTeam);
    }
  }
}