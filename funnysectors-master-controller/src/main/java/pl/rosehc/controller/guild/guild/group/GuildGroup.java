package pl.rosehc.controller.guild.guild.group;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import pl.rosehc.controller.guild.guild.GuildPermissionType;
import pl.rosehc.controller.wrapper.guild.GuildGroupColorWrapper;
import pl.rosehc.controller.wrapper.guild.GuildGroupSerializableWrapper;
import pl.rosehc.controller.wrapper.guild.GuildPermissionTypeWrapper;

public final class GuildGroup {

  private final UUID uniqueId;
  private final Set<GuildPermissionType> permissions;
  private final GuildGroupColor color;
  private final boolean leader, deputy;
  private String name;

  public GuildGroup(final UUID uniqueId, final Set<GuildPermissionType> permissions,
      final GuildGroupColor color, final boolean leader, final boolean deputy, final String name) {
    this.uniqueId = uniqueId;
    this.permissions = permissions;
    this.color = color;
    this.leader = leader;
    this.deputy = deputy;
    this.name = name;
  }

  public GuildGroup(final GuildGroup group) {
    this.uniqueId = group.getUniqueId();
    this.color = group.getColor();
    this.permissions = new HashSet<>(group.getPermissions());
    this.leader = group.isLeader();
    this.deputy = group.isDeputy();
    this.name = group.getName();
  }

  public GuildGroupSerializableWrapper wrap() {
    return new GuildGroupSerializableWrapper(this.uniqueId,
        GuildGroupColorWrapper.fromOriginal(this.color), this.name,
        this.permissions.stream().map(GuildPermissionTypeWrapper::fromOriginal)
            .collect(Collectors.toSet()));
  }

  public UUID getUniqueId() {
    return this.uniqueId;
  }

  public GuildGroupColor getColor() {
    return this.color;
  }

  public Set<GuildPermissionType> getPermissions() {
    return this.permissions;
  }

  public boolean changePermission(final GuildPermissionType type) {
    if (!this.permissions.add(type)) {
      this.permissions.remove(type);
      return false;
    }

    return true;
  }

  public boolean hasPermission(final GuildPermissionType type) {
    return this.permissions.contains(type);
  }

  public boolean isLeader() {
    return this.leader;
  }

  public boolean isDeputy() {
    return this.deputy;
  }

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  @Override
  public boolean equals(final Object object) {
    if (this == object) {
      return true;
    }

    if (object == null || getClass() != object.getClass()) {
      return false;
    }

    final GuildGroup that = (GuildGroup) object;
    return this.uniqueId.equals(that.uniqueId) && this.name.equals(that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.uniqueId, this.name);
  }
}
