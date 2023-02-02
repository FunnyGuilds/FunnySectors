package pl.rosehc.platform.magiccase;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.bukkit.scheduler.BukkitTask;
import pl.rosehc.adapter.helper.NumberHelper;
import pl.rosehc.platform.PlatformConfiguration;

public final class MagicCaseFactory {

  private final Map<UUID, MagicCase> magicCaseMap;
  private final Set<MagicCaseItem> magicCaseItemSet;

  public MagicCaseFactory(final PlatformConfiguration platformConfiguration) {
    this.magicCaseMap = new ConcurrentHashMap<>();
    this.magicCaseItemSet = ConcurrentHashMap.newKeySet();
    this.magicCaseItemSet.addAll(
        platformConfiguration.magicCaseItemWrapperList.stream().map(MagicCaseItem::create)
            .collect(Collectors.toSet()));
  }

  public void removeMagicCase(final UUID uuid) {
    final MagicCase magicCase = this.magicCaseMap.remove(uuid);
    if (Objects.isNull(magicCase)) {
      return;
    }

    final BukkitTask updateTask = magicCase.getUpdateTask();
    if (Objects.nonNull(updateTask)) {
      updateTask.cancel();
    }
  }

  public void addMagicCase(final UUID uuid, final MagicCase magicCase) {
    this.magicCaseMap.put(uuid, magicCase);
  }

  public MagicCaseItem findRandomMagicCaseItem() {
    for (final MagicCaseItem magicCaseItem : this.magicCaseItemSet) {
      if (magicCaseItem.getChance() >= 100D || magicCaseItem.getChance() >= NumberHelper.range(0D,
          100D)) {
        return magicCaseItem;
      }
    }

    return null;
  }

  public Set<MagicCaseItem> getMagicCaseItemSet() {
    return this.magicCaseItemSet;
  }

  public boolean isCurrentlyOpeningMagicCase(final UUID uniqueId) {
    return this.magicCaseMap.containsKey(uniqueId);
  }
}
