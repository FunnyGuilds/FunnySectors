package pl.rosehc.platform.disco.impl;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.bukkit.Color;
import pl.rosehc.platform.disco.DiscoEffect;

public final class SmoothDiscoEffect extends DiscoEffect {

  private final AtomicReference<Color> lastColor = new AtomicReference<>(Color.fromRGB(255, 0, 0));

  @Override
  protected List<Color> calculateColors() {
    Color color = this.lastColor.get();
    int red = color.getRed(), green = color.getGreen(), blue = color.getBlue();
    if (red == 255 && green < 255 && blue == 0) {
      green += 15;
    }
    if (green == 255 && red > 0 && blue == 0) {
      red -= 15;
    }
    if (green == 255 && blue < 255 && red == 0) {
      blue += 15;
    }
    if (blue == 255 && green > 0 && red == 0) {
      green -= 15;
    }
    if (blue == 255 && red < 255 && green == 0) {
      red += 15;
    }
    if (red == 255 && blue > 0 && green == 0) {
      blue -= 15;
    }

    this.lastColor.set(color = Color.fromRGB(red, green, blue));
    return Collections.singletonList(color);
  }
}
