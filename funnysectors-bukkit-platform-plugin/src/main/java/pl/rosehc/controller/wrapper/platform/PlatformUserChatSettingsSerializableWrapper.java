package pl.rosehc.controller.wrapper.platform;

public final class PlatformUserChatSettingsSerializableWrapper {

  private boolean global, itemShop;
  private boolean kills, deaths;
  private boolean cases, achievements;
  private boolean rewards, privateMessages;

  private PlatformUserChatSettingsSerializableWrapper() {
  }

  public PlatformUserChatSettingsSerializableWrapper(final boolean global, final boolean itemShop,
      final boolean kills, final boolean deaths, final boolean cases, final boolean achievements,
      final boolean rewards, final boolean privateMessages) {
    this.global = global;
    this.itemShop = itemShop;
    this.kills = kills;
    this.deaths = deaths;
    this.cases = cases;
    this.achievements = achievements;
    this.rewards = rewards;
  }

  public boolean isGlobal() {
    return this.global;
  }

  public boolean isItemShop() {
    return this.itemShop;
  }

  public boolean isKills() {
    return this.kills;
  }

  public boolean isDeaths() {
    return this.deaths;
  }

  public boolean isCases() {
    return this.cases;
  }

  public boolean isAchievements() {
    return this.achievements;
  }

  public boolean isRewards() {
    return this.rewards;
  }

  public boolean isPrivateMessages() {
    return this.privateMessages;
  }
}
