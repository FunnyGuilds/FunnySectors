package pl.rosehc.platform.hologram;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.server.v1_8_R3.EntityArmorStand;
import net.minecraft.server.v1_8_R3.EntitySlime;
import net.minecraft.server.v1_8_R3.PacketPlayOutAttachEntity;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import net.minecraft.server.v1_8_R3.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import pl.rosehc.adapter.helper.ChatHelper;

public final class Hologram {

  private static final double VIEW_DISTANCE = Math.min(2500D,
      Math.pow(Bukkit.getViewDistance() << 4, 2D));
  private static final double ARMOR_STAND_HEIGHT_OFFSET = 0.25D, TEXT_HEIGHT_OFFSET = 0.29D, SLIME_HEIGHT_OFFSET = 0.5D;
  private final Location location;
  private final Map<Integer, HologramEntry> entryBySlimeIdMap;

  private volatile HologramEntry[] entries;
  private volatile boolean displayed;

  public Hologram(final Player owner, final Location location, final HologramText[] texts,
      final HologramAction[] actions) {
    this.location = location.clone().add(0D, 2D, 0D);
    this.entries = new HologramEntry[texts.length];
    if (this.entries.length < 1) {
      throw new IllegalStateException("entries size < 1");
    }

    this.entryBySlimeIdMap = new ConcurrentHashMap<>(this.entries.length);
    final Location locationClone = location.clone();
    final WorldServer worldHandle = ((CraftWorld) location.getWorld()).getHandle();
    for (int index = 0; index < texts.length; index++) {
      locationClone.setY(locationClone.getY() - ARMOR_STAND_HEIGHT_OFFSET);
      final HologramText text = texts[index];
      final EntityArmorStand textArmorStand = new EntityArmorStand(
          worldHandle,
          locationClone.getX(),
          locationClone.getY() - ARMOR_STAND_HEIGHT_OFFSET,
          locationClone.getZ()
      ), vehicleArmorStand = new EntityArmorStand(
          worldHandle,
          locationClone.getX(),
          (locationClone.getY() - TEXT_HEIGHT_OFFSET) - SLIME_HEIGHT_OFFSET,
          locationClone.getZ()
      );
      final EntitySlime slime = new EntitySlime(worldHandle);
      final String convertedText = text.get(owner);
      textArmorStand.setInvisible(true);
      textArmorStand.setGravity(true);
      textArmorStand.setInvisible(true);
      textArmorStand.setGravity(true);
      textArmorStand.setSmall(true);
      textArmorStand.setArms(false);
      textArmorStand.setBasePlate(true);
      textArmorStand.setCustomNameVisible(!convertedText.trim().isEmpty());
      textArmorStand.setCustomName(ChatHelper.colored(convertedText));
      textArmorStand.n(true);
      textArmorStand.a(new HologramEmptyBoundingBox());
      vehicleArmorStand.setInvisible(true);
      vehicleArmorStand.setGravity(true);
      vehicleArmorStand.setSmall(true);
      vehicleArmorStand.setArms(false);
      vehicleArmorStand.setBasePlate(true);
      vehicleArmorStand.n(true);
      vehicleArmorStand.a(new HologramEmptyBoundingBox());
      slime.setInvisible(true);
      slime.setSize(1);
      slime.a(0, 0F);
      slime.a(new HologramEmptyBoundingBox());
      slime.locX = locationClone.getX();
      slime.locY = (locationClone.getY() - TEXT_HEIGHT_OFFSET) - SLIME_HEIGHT_OFFSET;
      slime.locZ = locationClone.getZ();
      slime.canPickUpLoot = false;
      final HologramEntry entry = new HologramEntry(
          textArmorStand,
          vehicleArmorStand,
          slime,
          locationClone.clone(),
          text
      );
      if (index < actions.length) {
        final HologramAction action = actions[index];
        if (action != HologramAction.EMPTY) {
          entry.setAction(action);
        }
      }

      this.entries[index] = entry;
      this.entryBySlimeIdMap.put(slime.getId(), entry);
    }
  }

  public Hologram(final Player player, final Location location, final HologramText[] texts) {
    this(player, location, texts, new HologramAction[0]);
  }

  public synchronized Optional<HologramEntry> findEntryByIndex(final int index) {
    return index < this.entries.length ? Optional.ofNullable(this.entries[index])
        : Optional.empty();
  }

  public Optional<HologramEntry> findEntryBySlimeId(final int id) {
    return Optional.ofNullable(this.entryBySlimeIdMap.get(id));
  }

  public synchronized void addText(final HologramText text, final HologramAction action,
      final Player player) {
    final HologramEntry[] lastEntries = this.entries;
    final HologramEntry[] newEntries = new HologramEntry[lastEntries.length + 1];
    final HologramEntry lastEntry = lastEntries[lastEntries.length - 1];
    final Location newEntryLocation = lastEntry.getLocation().clone()
        .subtract(0D, ARMOR_STAND_HEIGHT_OFFSET, 0D);
    final WorldServer worldHandle = ((CraftWorld) newEntryLocation.getWorld()).getHandle();
    final EntityArmorStand textArmorStand = new EntityArmorStand(
        worldHandle,
        newEntryLocation.getX(),
        newEntryLocation.getY() - ARMOR_STAND_HEIGHT_OFFSET,
        newEntryLocation.getZ()
    ), vehicleArmorStand = new EntityArmorStand(
        worldHandle,
        newEntryLocation.getX(),
        (newEntryLocation.getY() - TEXT_HEIGHT_OFFSET) - SLIME_HEIGHT_OFFSET,
        newEntryLocation.getZ()
    );
    final EntitySlime slime = new EntitySlime(worldHandle);
    final String convertedText = text.get(player);
    textArmorStand.setInvisible(true);
    textArmorStand.setGravity(true);
    textArmorStand.setInvisible(true);
    textArmorStand.setGravity(true);
    textArmorStand.setSmall(true);
    textArmorStand.setArms(false);
    textArmorStand.setBasePlate(true);
    textArmorStand.setCustomNameVisible(!convertedText.trim().isEmpty());
    textArmorStand.setCustomName(ChatHelper.colored(convertedText));
    textArmorStand.n(true);
    textArmorStand.a(new HologramEmptyBoundingBox());
    vehicleArmorStand.setInvisible(true);
    vehicleArmorStand.setGravity(true);
    vehicleArmorStand.setSmall(true);
    vehicleArmorStand.setArms(false);
    vehicleArmorStand.setBasePlate(true);
    vehicleArmorStand.n(true);
    vehicleArmorStand.a(new HologramEmptyBoundingBox());
    slime.setInvisible(true);
    slime.setSize(1);
    slime.a(0, 0F);
    slime.a(new HologramEmptyBoundingBox());
    slime.locX = newEntryLocation.getX();
    slime.locY = (newEntryLocation.getY() - TEXT_HEIGHT_OFFSET) - SLIME_HEIGHT_OFFSET;
    slime.locZ = newEntryLocation.getZ();
    slime.canPickUpLoot = false;
    final HologramEntry entry = new HologramEntry(
        textArmorStand,
        vehicleArmorStand,
        slime,
        newEntryLocation,
        text
    );
    if (Objects.nonNull(action) && action != HologramAction.EMPTY) {
      entry.setAction(action);
    }

    System.arraycopy(lastEntries, 0, newEntries, 0, lastEntries.length);
    newEntries[newEntries.length - 1] = entry;
    this.entries = newEntries;
    this.entryBySlimeIdMap.put(slime.getId(), entry);
  }

  public synchronized void update(final Player owner) {
    final Location hologramLocation = this.location;
    final Location ownerLocation = owner.getLocation();
    if (!ownerLocation.getWorld().equals(hologramLocation.getWorld())
        || !hologramLocation.getWorld()
        .isChunkLoaded(hologramLocation.getBlockX() >> 4, hologramLocation.getBlockZ() >> 4)
        || hologramLocation.distanceSquared(ownerLocation) > VIEW_DISTANCE) {
      this.hide(owner);
      return;
    }

    this.show(owner);
    final PlayerConnection connection = ((CraftPlayer) owner).getHandle().playerConnection;
    for (final HologramEntry entry : this.entries) {
      final HologramText text = Objects.requireNonNull(entry.getText(),
          "Hologram entry text cannot be null!");
      final EntityArmorStand entity = entry.getTextArmorStand();
      entity.setCustomName(ChatHelper.colored(text.get(owner)));
      connection.sendPacket(
          new PacketPlayOutEntityMetadata(entity.getId(), entity.getDataWatcher(), false));
    }
  }

  public synchronized void show(final Player player) {
    if (!this.displayed) {
      final PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
      for (final HologramEntry entry : this.entries) {
        connection.sendPacket(new PacketPlayOutSpawnEntityLiving(entry.getTextArmorStand()));
        connection.sendPacket(new PacketPlayOutSpawnEntityLiving(entry.getVehicleArmorStand()));
        connection.sendPacket(new PacketPlayOutSpawnEntityLiving(entry.getSlime()));
        connection.sendPacket(
            new PacketPlayOutAttachEntity(0, entry.getVehicleArmorStand(), entry.getSlime()));
      }

      this.displayed = true;
    }
  }

  public synchronized void hide(final Player player) {
    if (this.displayed) {
      final PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
      for (final HologramEntry entry : this.entries) {
        connection.sendPacket(new PacketPlayOutEntityDestroy(entry.getTextArmorStand().getId()));
        connection.sendPacket(new PacketPlayOutEntityDestroy(entry.getSlime().getId()));
        connection.sendPacket(new PacketPlayOutEntityDestroy(entry.getVehicleArmorStand().getId()));
      }

      this.displayed = false;
    }
  }

  public synchronized void setText(final HologramText text, final int index) {
    if (index < this.entries.length) {
      final HologramEntry entry = this.entries[index];
      entry.setText(text);
    }
  }

  public synchronized void setAction(final HologramAction action, final int index) {
    if (index < this.entries.length) {
      final HologramEntry entry = this.entries[index];
      entry.setAction(action);
    }
  }

  public synchronized void addText(final HologramText text, final Player owner) {
    this.addText(text, null, owner);
  }
}
