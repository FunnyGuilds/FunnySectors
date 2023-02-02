package pl.rosehc.platform.disco;

import pl.rosehc.platform.disco.impl.GrayDiscoEffect;
import pl.rosehc.platform.disco.impl.RandomDiscoEffect;
import pl.rosehc.platform.disco.impl.SmoothDiscoEffect;
import pl.rosehc.platform.disco.impl.UltraDiscoEffect;

public enum DiscoEffectType {

  GRAY(new GrayDiscoEffect()), RANDOM(new RandomDiscoEffect()),
  SMOOTH(new SmoothDiscoEffect()), ULTRA(new UltraDiscoEffect());

  private final DiscoEffect effect;

  DiscoEffectType(final DiscoEffect effect) {
    this.effect = effect;
  }

  public DiscoEffect getEffect() {
    return this.effect;
  }
}
