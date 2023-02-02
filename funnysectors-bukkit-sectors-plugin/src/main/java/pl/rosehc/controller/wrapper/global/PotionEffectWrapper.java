package pl.rosehc.controller.wrapper.global;

import com.google.gson.annotations.SerializedName;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class PotionEffectWrapper {

  @SerializedName("effect_type")
  public String potionEffectType;
  public int amplifier;
  public int duration;

  private PotionEffectWrapper() {
  }

  public PotionEffectWrapper(final String potionEffectType, final int amplifier,
      final int duration) {
    this.potionEffectType = potionEffectType;
    this.amplifier = amplifier;
    this.duration = duration;
  }

  public String getPotionEffectType() {
    return this.potionEffectType;
  }

  public int getAmplifier() {
    return this.amplifier;
  }

  public int getDuration() {
    return this.duration;
  }

  public PotionEffect asPotionEffect() {
    return new PotionEffect(PotionEffectType.getByName(this.potionEffectType), this.duration * 20,
        this.amplifier);
  }
}
