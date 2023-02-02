package pl.rosehc.controller.platform.safe;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import pl.rosehc.controller.wrapper.platform.PlatformSafeSerializableWrapper;

public final class Safe {

  private final UUID uniqueId;
  private final long creationTime;

  private UUID ownerUniqueId;
  private String ownerNickname;
  private String description;
  private UUID modifierUuid;
  private byte[] contents;
  private long lastOpenedTime;

  public Safe(final ResultSet result) throws SQLException {
    this.uniqueId = UUID.fromString(result.getString("uniqueId"));
    this.creationTime = result.getLong("creationTime");
    this.ownerUniqueId = UUID.fromString(result.getString("ownerUniqueId"));
    this.ownerNickname = result.getString("ownerNickname");
    this.description = result.getString("description");
    this.contents = result.getBytes("contents");
    this.lastOpenedTime = result.getLong("lastOpenedTime");
  }

  public Safe(final UUID uniqueId, final UUID ownerUniqueId, final String ownerNickname,
      final long creationTime) {
    this.uniqueId = uniqueId;
    this.ownerUniqueId = ownerUniqueId;
    this.ownerNickname = ownerNickname;
    this.creationTime = creationTime;
  }

  public PlatformSafeSerializableWrapper wrap() {
    return new PlatformSafeSerializableWrapper(this.uniqueId, this.ownerUniqueId, this.modifierUuid,
        this.ownerNickname, this.description, this.contents, this.creationTime,
        this.lastOpenedTime);
  }

  public UUID getUniqueId() {
    return this.uniqueId;
  }

  public UUID getOwnerUniqueId() {
    return this.ownerUniqueId;
  }

  public void setOwnerUniqueId(final UUID ownerUniqueId) {
    this.ownerUniqueId = ownerUniqueId;
  }

  public UUID getModifierUuid() {
    return this.modifierUuid;
  }

  public void setModifierUuid(final UUID modifierUuid) {
    this.modifierUuid = modifierUuid;
  }

  public String getOwnerNickname() {
    return this.ownerNickname;
  }

  public void setOwnerNickname(final String ownerNickname) {
    this.ownerNickname = ownerNickname;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(final String description) {
    this.description = description;
  }

  public byte[] getContents() {
    return this.contents;
  }

  public void setContents(final byte[] contents) {
    this.contents = contents;
  }

  public long getCreationTime() {
    return this.creationTime;
  }

  public long getLastOpenedTime() {
    return this.lastOpenedTime;
  }

  public void setLastOpenedTime(final long lastOpenedTime) {
    this.lastOpenedTime = lastOpenedTime;
  }
}
