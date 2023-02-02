package pl.rosehc.platform.rank;

import java.util.List;

public final class Rank {

  private final String name, chatPrefix, chatSuffix, nameTagPrefix, nameTagSuffix;
  private final List<String> permissions;
  private final int priority;
  private final boolean defaultRank;

  public Rank(final String name, final String chatPrefix, final String chatSuffix,
      final String nameTagPrefix, final String nameTagSuffix, final List<String> permissions,
      final int priority, final boolean defaultRank) {
    this.name = name;
    this.chatPrefix = chatPrefix;
    this.chatSuffix = chatSuffix;
    this.nameTagPrefix = nameTagPrefix;
    this.nameTagSuffix = nameTagSuffix;
    this.permissions = permissions;
    this.priority = priority;
    this.defaultRank = defaultRank;
  }

  public String getName() {
    return this.name;
  }

  public String getChatPrefix() {
    return this.chatPrefix;
  }

  public String getChatSuffix() {
    return this.chatSuffix;
  }

  public String getNameTagPrefix() {
    return this.nameTagPrefix;
  }

  public String getNameTagSuffix() {
    return this.nameTagSuffix;
  }

  public List<String> getPermissions() {
    return this.permissions;
  }

  public int getPriority() {
    return this.priority;
  }

  public boolean isDefaultRank() {
    return this.defaultRank;
  }
}
