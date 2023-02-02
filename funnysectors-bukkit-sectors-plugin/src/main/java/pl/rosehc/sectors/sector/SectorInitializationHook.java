package pl.rosehc.sectors.sector;

import pl.rosehc.adapter.helper.EventCompletionStage;

@FunctionalInterface
public interface SectorInitializationHook {

  void onInitialize(final EventCompletionStage completionStage, final Sector sector,
      final boolean success);
}
