package pl.rosehc.platform.hologram;

import net.minecraft.server.v1_8_R3.EntityArmorStand;
import net.minecraft.server.v1_8_R3.EntitySlime;
import org.bukkit.Location;

public final class HologramEntry {

  private final EntityArmorStand textArmorStand, vehicleArmorStand;
  private final EntitySlime slime;
  private final Location location;
  private HologramText text;
  private HologramAction action;

  public HologramEntry(final EntityArmorStand textArmorStand,
      final EntityArmorStand vehicleArmorStand, final EntitySlime slime, final Location location,
      final HologramText text) {
    this.textArmorStand = textArmorStand;
    this.vehicleArmorStand = vehicleArmorStand;
    this.slime = slime;
    this.location = location;
    this.text = text;
  }

  public EntityArmorStand getTextArmorStand() {
    return this.textArmorStand;
  }

  public EntityArmorStand getVehicleArmorStand() {
    return this.vehicleArmorStand;
  }

  public EntitySlime getSlime() {
    return this.slime;
  }

  public Location getLocation() {
    return this.location;
  }

  public HologramText getText() {
    return this.text;
  }

  public void setText(final HologramText text) {
    this.text = text;
  }

  public HologramAction getAction() {
    return this.action;
  }

  public void setAction(final HologramAction action) {
    this.action = action;
  }
}
