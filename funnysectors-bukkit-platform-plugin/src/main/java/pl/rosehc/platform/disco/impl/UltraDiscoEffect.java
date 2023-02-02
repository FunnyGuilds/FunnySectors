package pl.rosehc.platform.disco.impl;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.Color;
import pl.rosehc.platform.disco.DiscoEffect;

public final class UltraDiscoEffect extends DiscoEffect {

  @Override
  protected List<Color> calculateColors() {
    final ThreadLocalRandom random = ThreadLocalRandom.current();
    return Arrays.asList(
        Color.fromRGB(random.nextInt(255), random.nextInt(255), random.nextInt(255)),
        Color.fromRGB(random.nextInt(255), random.nextInt(255), random.nextInt(255)),
        Color.fromRGB(random.nextInt(255), random.nextInt(255), random.nextInt(255)),
        Color.fromRGB(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
  }
}
