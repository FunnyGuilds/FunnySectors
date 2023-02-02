package pl.rosehc.controller.wrapper.guild;

import java.util.Set;
import java.util.UUID;

public final class GuildMemberSerializableWrapper {

  private UUID uniqueId;
  private Set<GuildPermissionTypeWrapper> permissions;
  private UUID groupUniqueId;

  private GuildMemberSerializableWrapper() {
  }

  public GuildMemberSerializableWrapper(final UUID uniqueId,
      final Set<GuildPermissionTypeWrapper> permissions, final UUID groupUniqueId) {
    this.uniqueId = uniqueId;
    this.permissions = permissions;
    this.groupUniqueId = groupUniqueId;
  }

  public UUID getUniqueId() {
    return this.uniqueId;
  }

  public Set<GuildPermissionTypeWrapper> getPermissions() {
    return this.permissions;
  }

  public UUID getGroupUniqueId() {
    return this.groupUniqueId;
  }
}
