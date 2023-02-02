package pl.rosehc.controller.wrapper.platform;

public enum PlatformUserCooldownType {

  CHAT(3000L),
  SECTOR_CHANGE(3000L),
  PROXY_JOIN(30_000L),
  HELPOP(30_000L),
  END_BAN(1800_000L),
  GUILD_ALERT(10_000L),
  GUILD_NEED_HELP(30_000L),
  GUILD_NEED_HELP_ALLY(30_000L),
  USER_RANKING_RESET(120_000L),
  USER_INCOGNITO(20_000L),
  REPAIR_PICKAXE(30_000L);

  private final long defaultValue;

  PlatformUserCooldownType(final long defaultValue) {
    this.defaultValue = defaultValue;
  }

  public long getDefaultValue() {
    return this.defaultValue;
  }
}
