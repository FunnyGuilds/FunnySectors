package pl.rosehc.controller.wrapper.guild;

import java.util.Set;
import java.util.UUID;

public final class GuildGroupSerializableWrapper {

  private UUID uniqueId;
  private GuildGroupColorWrapper color;
  private String name;
  private Set<GuildPermissionTypeWrapper> permissions;

  private GuildGroupSerializableWrapper() {
  }

  public GuildGroupSerializableWrapper(final UUID uniqueId, final GuildGroupColorWrapper color,
      final String name, final Set<GuildPermissionTypeWrapper> permissions) {
    this.uniqueId = uniqueId;
    this.color = color;
    this.name = name;
    this.permissions = permissions;
  }

  public UUID getUniqueId() {
    return this.uniqueId;
  }

  public GuildGroupColorWrapper getColor() {
    return this.color;
  }

  public String getName() {
    return this.name;
  }

  public Set<GuildPermissionTypeWrapper> getPermissions() {
    return this.permissions;
  }
}
