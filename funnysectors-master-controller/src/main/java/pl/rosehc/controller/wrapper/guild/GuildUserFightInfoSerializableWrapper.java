package pl.rosehc.controller.wrapper.guild;

import java.util.UUID;

public final class GuildUserFightInfoSerializableWrapper {

  private UUID uniqueId;
  private long fightTime;

  private GuildUserFightInfoSerializableWrapper() {
  }

  public GuildUserFightInfoSerializableWrapper(final UUID uniqueId, final long fightTime) {
    this.uniqueId = uniqueId;
    this.fightTime = fightTime;
  }

  public UUID getUniqueId() {
    return this.uniqueId;
  }

  public long getFightTime() {
    return this.fightTime;
  }
}
