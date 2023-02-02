package pl.rosehc.controller.guild.guild;

import pl.rosehc.controller.wrapper.guild.GuildPlayerHelpInfoSerializableWrapper;

public final class GuildPlayerHelpInfo {

  private final String nickname;
  private final long time;
  private int x, y, z;

  public GuildPlayerHelpInfo(final String nickname, final long time, final int x, final int y,
      final int z) {
    this.nickname = nickname;
    this.time = time;
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public GuildPlayerHelpInfoSerializableWrapper wrap() {
    return new GuildPlayerHelpInfoSerializableWrapper(this.nickname, this.time, this.x, this.y,
        this.z);
  }

  public String getNickname() {
    return this.nickname;
  }

  public long getTime() {
    return this.time;
  }

  public int getX() {
    return this.x;
  }

  public void setX(final int x) {
    this.x = x;
  }

  public int getY() {
    return this.y;
  }

  public void setY(final int y) {
    this.y = y;
  }

  public int getZ() {
    return this.z;
  }

  public void setZ(final int z) {
    this.z = z;
  }
}
