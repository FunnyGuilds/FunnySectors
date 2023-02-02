package pl.rosehc.controller.platform.rank;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import pl.rosehc.controller.configuration.impl.configuration.PlatformConfiguration;

public final class RankFactory {

  private final Map<String, Rank> rankMap;
  private volatile Rank defaultRank;

  public RankFactory(final PlatformConfiguration configuration) {
    this.rankMap = configuration.rankList.stream().map(
            rankWrapper -> new Rank(rankWrapper.name, rankWrapper.chatPrefix, rankWrapper.chatSuffix,
                rankWrapper.nameTagPrefix, rankWrapper.nameTagSuffix, rankWrapper.permissions,
                rankWrapper.priority, rankWrapper.defaultRank))
        .collect(Collectors.toConcurrentMap(rank -> rank.getName().toLowerCase(), rank -> rank));
    this.updateDefaultRank();
  }

  public synchronized void updateDefaultRank() {
    this.defaultRank = this.rankMap.values().stream().filter(Rank::isDefaultRank).findFirst()
        .orElseThrow(() -> new UnsupportedOperationException("Domyślna ranga nie istnieje?"));
  }

  public Optional<Rank> findRank(final String name) {
    return Optional.ofNullable(this.rankMap.get(name.toLowerCase()));
  }

  public Rank getDefaultRank() {
    return this.defaultRank;
  }

  public Map<String, Rank> getRankMap() {
    return this.rankMap;
  }
}
