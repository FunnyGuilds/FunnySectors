package pl.rosehc.platform.user.subdata;

public final class PlatformUserChatSettings {

  private boolean global, itemShop;
  private boolean kills, deaths;
  private boolean cases, achievements;
  private boolean rewards, privateMessages;

  public PlatformUserChatSettings(final boolean global, final boolean itemShop, final boolean kills,
      final boolean deaths, final boolean cases, final boolean achievements, final boolean rewards,
      final boolean privateMessages) {
    this.global = global;
    this.itemShop = itemShop;
    this.kills = kills;
    this.deaths = deaths;
    this.cases = cases;
    this.achievements = achievements;
    this.rewards = rewards;
    this.privateMessages = privateMessages;
  }

  public boolean isGlobal() {
    return this.global;
  }

  public void setGlobal(final boolean global) {
    this.global = global;
  }

  public boolean isItemShop() {
    return this.itemShop;
  }

  public void setItemShop(final boolean itemShop) {
    this.itemShop = itemShop;
  }

  public boolean isKills() {
    return this.kills;
  }

  public void setKills(final boolean kills) {
    this.kills = kills;
  }

  public boolean isDeaths() {
    return this.deaths;
  }

  public void setDeaths(final boolean deaths) {
    this.deaths = deaths;
  }

  public boolean isCases() {
    return this.cases;
  }

  public void setCases(final boolean cases) {
    this.cases = cases;
  }

  public boolean isAchievements() {
    return this.achievements;
  }

  public void setAchievements(final boolean achievements) {
    this.achievements = achievements;
  }

  public boolean isRewards() {
    return this.rewards;
  }

  public void setRewards(final boolean rewards) {
    this.rewards = rewards;
  }

  public boolean isPrivateMessages() {
    return this.privateMessages;
  }

  public void setPrivateMessages(final boolean privateMessages) {
    this.privateMessages = privateMessages;
  }
}
