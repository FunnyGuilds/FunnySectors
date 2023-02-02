package pl.rosehc.controller.platform.user.subdata;

public final class PlatformUserRewardSettings {

  private long discordUserId;
  private boolean discordRewardReceived;

  public PlatformUserRewardSettings(final long discordUserId, final boolean discordRewardReceived) {
    this.discordUserId = discordUserId;
    this.discordRewardReceived = discordRewardReceived;
  }

  public long getDiscordUserId() {
    return this.discordUserId;
  }

  public void setDiscordUserId(final long discordUserId) {
    this.discordUserId = discordUserId;
  }

  public boolean isDiscordRewardReceived() {
    return this.discordRewardReceived;
  }

  public void setDiscordRewardReceived(final boolean discordRewardReceived) {
    this.discordRewardReceived = discordRewardReceived;
  }
}
