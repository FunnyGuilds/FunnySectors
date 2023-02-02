package pl.rosehc.controller.wrapper.guild;

public final class GuildPlayerHelpInfoSerializableWrapper {

  private String nickname;
  private long time;
  private int x, y, z;

  private GuildPlayerHelpInfoSerializableWrapper() {
  }

  public GuildPlayerHelpInfoSerializableWrapper(final String nickname, final long time, final int x,
      final int y, final int z) {
    this.nickname = nickname;
    this.time = time;
    this.x = x;
    this.y = y;
    this.z = z;
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

  public int getY() {
    return this.y;
  }

  public int getZ() {
    return this.z;
  }
}
