package pl.rosehc.platform.disco.impl;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.bukkit.Color;
import pl.rosehc.platform.disco.DiscoEffect;

public final class GrayDiscoEffect extends DiscoEffect {

  private final AtomicInteger lastGrayscale = new AtomicInteger();

  @Override
  protected List<Color> calculateColors() {
    if (this.lastGrayscale.getAndAdd(3) >= 250) {
      this.lastGrayscale.set(0);
    }

    final int lastGrayscale = this.lastGrayscale.get();
    return Collections.singletonList(Color.fromRGB(lastGrayscale, lastGrayscale, lastGrayscale));
  }
}
