package pl.rosehc.controller.guild.guild;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import pl.rosehc.controller.MasterController;
import pl.rosehc.controller.sector.Sector;

public final class GuildFactory {

  private final Map<String, Guild> guildMap;
  private final AtomicBoolean shutdownExecuted = new AtomicBoolean();

  public GuildFactory(final MasterController masterController) throws SQLException {
    this.guildMap = masterController.getGuildRepository().loadAll();
    for (final Guild guild : this.guildMap.values()) {
      final String alliedGuildTag = masterController.getGuildRepository()
          .fetchAlliedGuildTag(guild.getName());
      if (Objects.nonNull(alliedGuildTag) && this.guildMap.containsKey(alliedGuildTag)) {
        guild.setAlliedGuild(this.guildMap.get(alliedGuildTag));
      }
    }

    System.out.println("[GILDIE] Załadowano " + this.getGuildMap().size() + " gildii.");
  }

  public void registerGuild(final Guild guild) {
    if (!this.shutdownExecuted.get()) {
      this.guildMap.put(guild.getTag().toLowerCase(), guild);
      this.guildMap.put(guild.getName().toLowerCase(), guild);
    }
  }

  public void unregisterGuild(final Guild guild) {
    if (!this.shutdownExecuted.get()) {
      this.guildMap.remove(guild.getTag().toLowerCase());
      this.guildMap.remove(guild.getName().toLowerCase());
    }
  }

  public void markToShutdown() {
    this.shutdownExecuted.set(true);
  }

  public Optional<Guild> findGuildByCredential(final String credential, final boolean tag) {
    Guild guild = this.guildMap.get(credential.toLowerCase());
    if (Objects.nonNull(guild)) {
      if (guild.getTag().equalsIgnoreCase(credential) && !tag) {
        return Optional.empty();
      } else if (guild.getName().equalsIgnoreCase(credential) && tag) {
        return Optional.empty();
      }
    }

    return Optional.ofNullable(guild);
  }

  public List<Guild> getGuildsBySector(final Sector sector) {
    final List<Guild> guildList = new ArrayList<>(this.guildMap.values());
    guildList.removeIf(guild -> !guild.getCreationSector().equals(sector));
    return guildList;
  }

  public Optional<Guild> findGuildByCredential(final String credential) {
    return this.findGuildByCredential(credential, true);
  }

  public Map<String, Guild> getGuildMap() {
    final Map<String, Guild> guildMap = new ConcurrentHashMap<>();
    for (final Entry<String, Guild> entry : this.guildMap.entrySet()) {
      if (entry.getValue().getTag().equals(entry.getKey())) {
        guildMap.put(entry.getKey(), entry.getValue());
      }
    }

    return guildMap;
  }
}
