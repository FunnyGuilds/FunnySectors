package pl.rosehc.controller.wrapper.platform;

import java.util.UUID;

public final class PlatformSafeSerializableWrapper {

  private UUID uniqueId, ownerUniqueId, modifierUuid;
  private String ownerNickname;
  private String description;
  private byte[] contents;
  private long creationTime;
  private long lastOpenedTime;

  private PlatformSafeSerializableWrapper() {
  }

  public PlatformSafeSerializableWrapper(final UUID uniqueId, final UUID ownerUniqueId,
      final UUID modifierUuid, final String ownerNickname, final String description,
      final byte[] contents, final long creationTime, final long lastOpenedTime) {
    this.uniqueId = uniqueId;
    this.ownerUniqueId = ownerUniqueId;
    this.modifierUuid = modifierUuid;
    this.ownerNickname = ownerNickname;
    this.description = description;
    this.contents = contents;
    this.creationTime = creationTime;
    this.lastOpenedTime = lastOpenedTime;
  }

  public UUID getUniqueId() {
    return this.uniqueId;
  }

  public UUID getOwnerUniqueId() {
    return this.ownerUniqueId;
  }

  public UUID getModifierUuid() {
    return this.modifierUuid;
  }

  public String getOwnerNickname() {
    return this.ownerNickname;
  }

  public String getDescription() {
    return this.description;
  }

  public byte[] getContents() {
    return this.contents;
  }

  public long getCreationTime() {
    return this.creationTime;
  }

  public long getLastOpenedTime() {
    return this.lastOpenedTime;
  }
}
