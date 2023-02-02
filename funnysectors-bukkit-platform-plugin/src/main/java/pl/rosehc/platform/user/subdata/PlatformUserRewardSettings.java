package pl.rosehc.platform.user.subdata;

public final class PlatformUserRewardSettings {

  private boolean discordRewardReceived;

  public PlatformUserRewardSettings(final boolean discordRewardReceived) {
    this.discordRewardReceived = discordRewardReceived;
  }

  public boolean isDiscordRewardReceived() {
    return this.discordRewardReceived;
  }

  public void setDiscordRewardReceived(final boolean discordRewardReceived) {
    this.discordRewardReceived = discordRewardReceived;
  }
}
