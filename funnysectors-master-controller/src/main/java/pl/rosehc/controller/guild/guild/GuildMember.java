package pl.rosehc.controller.guild.guild;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import pl.rosehc.controller.guild.guild.group.GuildGroup;
import pl.rosehc.controller.guild.user.GuildUser;
import pl.rosehc.controller.wrapper.guild.GuildMemberSerializableWrapper;
import pl.rosehc.controller.wrapper.guild.GuildPermissionTypeWrapper;

public final class GuildMember {

  private final UUID uniqueId;
  private final GuildUser user;
  private final Set<GuildPermissionType> permissions;
  private GuildGroup group;

  public GuildMember(final UUID uniqueId, final GuildUser user,
      final Set<GuildPermissionType> permissions, final GuildGroup group) {
    this.uniqueId = uniqueId;
    this.user = user;
    this.permissions = permissions;
    this.group = group;
  }

  public GuildMemberSerializableWrapper wrap() {
    return new GuildMemberSerializableWrapper(this.uniqueId,
        this.permissions.stream().map(GuildPermissionTypeWrapper::fromOriginal)
            .collect(Collectors.toSet()), this.group.getUniqueId());
  }

  public Set<GuildPermissionType> getPermissions() {
    return this.permissions;
  }

  public UUID getUniqueId() {
    return this.uniqueId;
  }

  public GuildUser getUser() {
    return this.user;
  }

  public boolean changePermission(final GuildPermissionType type) {
    if (!this.permissions.add(type)) {
      this.permissions.remove(type);
      return false;
    }

    return true;
  }

  public boolean hasPermission(final GuildPermissionType type) {
    if (this.canManage()) {
      return true;
    }

    return this.permissions.contains(type) || this.group.getPermissions().contains(type);
  }

  public GuildGroup getGroup() {
    return this.group;
  }

  public void setGroup(final GuildGroup group) {
    this.group = group;
  }

  public boolean isLeader() {
    return this.group.isLeader();
  }

  public boolean isDeputy() {
    return this.group.isDeputy();
  }

  public boolean canManage() {
    return this.isLeader() || this.isDeputy();
  }
}
