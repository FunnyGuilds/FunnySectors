package pl.rosehc.platform.safe;

import java.util.Objects;
import java.util.UUID;
import org.bukkit.inventory.ItemStack;
import pl.rosehc.adapter.helper.SerializeHelper;
import pl.rosehc.controller.wrapper.platform.PlatformSafeSerializableWrapper;

public final class Safe {

  private final UUID uniqueId;
  private final long creationTime;

  private UUID ownerUniqueId;
  private String ownerNickname;
  private String description;
  private UUID modifierUuid;
  private ItemStack[] contents;
  private long lastOpenedTime;

  private Safe(final PlatformSafeSerializableWrapper wrapper) {
    this.uniqueId = wrapper.getUniqueId();
    this.creationTime = wrapper.getCreationTime();
    this.ownerUniqueId = wrapper.getOwnerUniqueId();
    this.ownerNickname = wrapper.getOwnerNickname();
    this.modifierUuid = wrapper.getModifierUuid();
    this.description = wrapper.getDescription();
    this.contents = Objects.nonNull(wrapper.getContents()) && wrapper.getContents().length != 0
        ? (ItemStack[]) SerializeHelper.deserializeBukkitObjectFromBytes(wrapper.getContents())
        : null;
    this.lastOpenedTime = wrapper.getLastOpenedTime();
  }

  public Safe(final UUID uniqueId, final UUID ownerUniqueId, final String ownerNickname,
      final long creationTime) {
    this.uniqueId = uniqueId;
    this.ownerUniqueId = ownerUniqueId;
    this.ownerNickname = ownerNickname;
    this.creationTime = creationTime;
  }

  public static Safe create(final PlatformSafeSerializableWrapper wrapper) {
    return new Safe(wrapper);
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

  public ItemStack[] getContents() {
    return this.contents;
  }

  public void setContents(final ItemStack[] contents) {
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
