package pl.rosehc.controller.guild.guild.group;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import pl.rosehc.controller.configuration.impl.configuration.GuildsConfiguration;
import pl.rosehc.controller.wrapper.guild.GuildPermissionTypeWrapper;

public final class GuildGroupFactory {

  private final Map<UUID, GuildGroup> defaultGuildGroupMap;
  private volatile GuildGroup defaultGuildGroup, leaderGuildGroup, deputyGuildGroup;

  public GuildGroupFactory(final GuildsConfiguration guildsConfiguration) {
    this.defaultGuildGroupMap = guildsConfiguration.pluginWrapper.groupMap.entrySet().stream().map(
            entry -> new GuildGroup(entry.getKey(),
                entry.getValue().permissions.stream().map(GuildPermissionTypeWrapper::toOriginal)
                    .collect(Collectors.toSet()), entry.getValue().color.toOriginal(),
                entry.getValue().leader, entry.getValue().deputy, entry.getValue().name))
        .collect(Collectors.toConcurrentMap(GuildGroup::getUniqueId, group -> group));
    this.updateDefaultGroups(guildsConfiguration);
  }

  public synchronized void updateDefaultGroups(final GuildsConfiguration guildsConfiguration) {
    this.defaultGuildGroup = this.findGroup(guildsConfiguration.pluginWrapper.defaultGroupUUID)
        .orElseThrow(() -> new UnsupportedOperationException(
            "Grupa o identyfikatorze " + guildsConfiguration.pluginWrapper.defaultGroupUUID));
    this.leaderGuildGroup = this.defaultGuildGroupMap.values().stream().filter(GuildGroup::isLeader)
        .findFirst()
        .orElseThrow(() -> new UnsupportedOperationException("Nie znaleziono grupy lidera!"));
    this.deputyGuildGroup = this.defaultGuildGroupMap.values().stream().filter(GuildGroup::isDeputy)
        .findFirst()
        .orElseThrow(() -> new UnsupportedOperationException("Nie znaleziono grupy zastępcy!"));
  }

  public synchronized GuildGroup getDefaultGuildGroup() {
    return this.defaultGuildGroup;
  }

  public synchronized GuildGroup getLeaderGuildGroup() {
    return this.leaderGuildGroup;
  }

  public synchronized GuildGroup getDeputyGuildGroup() {
    return this.deputyGuildGroup;
  }

  public Optional<GuildGroup> findGroup(final UUID uniqueId) {
    return Optional.ofNullable(this.defaultGuildGroupMap.get(uniqueId));
  }

  public Map<UUID, GuildGroup> getDefaultGuildGroupMap() {
    return this.defaultGuildGroupMap;
  }
}
