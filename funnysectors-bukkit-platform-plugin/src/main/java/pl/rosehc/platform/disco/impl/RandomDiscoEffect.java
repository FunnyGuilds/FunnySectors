package pl.rosehc.platform.disco.impl;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.Color;
import pl.rosehc.platform.disco.DiscoEffect;

public final class RandomDiscoEffect extends DiscoEffect {

  @Override
  protected List<Color> calculateColors() {
    final ThreadLocalRandom random = ThreadLocalRandom.current();
    return Collections.singletonList(
        Color.fromRGB(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
  }
}
