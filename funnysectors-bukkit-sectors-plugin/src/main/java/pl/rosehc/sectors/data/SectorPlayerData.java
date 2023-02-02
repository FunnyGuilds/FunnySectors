package pl.rosehc.sectors.data;

import java.util.UUID;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import pl.rosehc.adapter.helper.SerializeHelper;

/**
 * @author stevimeister on 06/01/2022
 **/
public final class SectorPlayerData {

  private final UUID uniqueId;
  private final String nickname;

  private GameMode gameMode;
  private ItemStack[] items, armor, enderChest;
  private PotionEffect[] potionEffects;
  private Location location;
  private double health;
  private float walkSpeed, flySpeed, exp;
  private int totalExp, foodLevel, fireTicks, heldSlot, level;
  private boolean allowFlight, flying, op, sprinting;

  private SectorPlayerData(final Player player) {
    this.uniqueId = player.getUniqueId();
    this.gameMode = player.getGameMode();
    this.nickname = player.getName();
    this.items = player.getInventory().getContents();
    this.armor = player.getInventory().getArmorContents();
    this.enderChest = player.getEnderChest().getContents();
    this.potionEffects = player.getActivePotionEffects().toArray(new PotionEffect[0]);
    this.health = player.getHealth();
    this.level = player.getLevel();
    this.totalExp = player.getTotalExperience();
    this.walkSpeed = player.getWalkSpeed();
    this.flySpeed = player.getFlySpeed();
    this.exp = player.getExp();
    this.foodLevel = player.getFoodLevel();
    this.fireTicks = player.getFireTicks();
    this.heldSlot = player.getInventory().getHeldItemSlot();
    this.allowFlight = player.getAllowFlight();
    this.flying = player.isFlying();
    this.op = player.isOp();
    this.sprinting = player.isSprinting();
    this.location = player.getLocation().clone().add(0.0, 1.5, 0.0);
  }

  private SectorPlayerData(final UUID uniqueId, final String nickname, final String gameMode,
      final String items,
      final String armor, final String enderChest,
      final String potionEffects, final double health, final float walkSpeed, final float flySpeed,
      final float exp, final int totalExp,
      final int foodLevel, final int fireTicks, final int heldSlot, final int level,
      final boolean allowFlight, final boolean flying,
      final boolean op, final boolean sprinting, final String location) {
    this.uniqueId = uniqueId;
    this.nickname = nickname;
    this.gameMode = GameMode.valueOf(gameMode);
    this.items = (ItemStack[]) SerializeHelper.deserializeBukkitObject(items);
    this.armor = (ItemStack[]) SerializeHelper.deserializeBukkitObject(armor);
    this.enderChest = (ItemStack[]) SerializeHelper.deserializeBukkitObject(enderChest);
    this.potionEffects = (PotionEffect[]) SerializeHelper.deserializeBukkitObject(potionEffects);
    this.health = health;
    this.walkSpeed = walkSpeed;
    this.flySpeed = flySpeed;
    this.exp = exp;
    this.totalExp = totalExp;
    this.foodLevel = foodLevel;
    this.fireTicks = fireTicks;
    this.heldSlot = heldSlot;
    this.level = level;
    this.allowFlight = allowFlight;
    this.flying = flying;
    this.op = op;
    this.sprinting = sprinting;
    this.location = SerializeHelper.deserializeLocation(location);
  }

  public static SectorPlayerData of(final Player player) {
    return new SectorPlayerData(player);
  }

  public static SectorPlayerData of(final SectorPlayerDataSynchronizeRequestPacket packet) {
    return new SectorPlayerData(packet.getUniqueId(),
        packet.getNickname(),
        packet.getGameMode(),
        packet.getItems(),
        packet.getArmor(),
        packet.getEnderChest(),
        packet.getPotionEffects(),
        packet.getHealth(),
        packet.getWalkSpeed(),
        packet.getFlySpeed(),
        packet.getExp(),
        packet.getTotalExp(),
        packet.getFoodLevel(),
        packet.getFireTicks(),
        packet.getHeldSlot(),
        packet.getLevel(),
        packet.isAllowFlight(),
        packet.isFlying(),
        packet.isOp(),
        packet.isSprinting(),
        packet.getLocation()
    );
  }

  public UUID getUniqueId() {
    return this.uniqueId;
  }

  public String getNickname() {
    return this.nickname;
  }

  public GameMode getGameMode() {
    return this.gameMode;
  }

  public void setGameMode(final GameMode gameMode) {
    this.gameMode = gameMode;
  }

  public ItemStack[] getItems() {
    return this.items;
  }

  public void setItems(final ItemStack[] items) {
    this.items = items;
  }

  public ItemStack[] getArmor() {
    return this.armor;
  }

  public void setArmor(final ItemStack[] armor) {
    this.armor = armor;
  }

  public ItemStack[] getEnderChest() {
    return this.enderChest;
  }

  public void setEnderChest(final ItemStack[] enderChest) {
    this.enderChest = enderChest;
  }

  public PotionEffect[] getPotionEffects() {
    return this.potionEffects;
  }

  public void setPotionEffects(final PotionEffect[] potionEffects) {
    this.potionEffects = potionEffects;
  }

  public double getHealth() {
    return this.health;
  }

  public void setHealth(final double health) {
    this.health = health;
  }

  public float getWalkSpeed() {
    return this.walkSpeed;
  }

  public void setWalkSpeed(final float walkSpeed) {
    this.walkSpeed = walkSpeed;
  }

  public float getFlySpeed() {
    return this.flySpeed;
  }

  public void setFlySpeed(final float flySpeed) {
    this.flySpeed = flySpeed;
  }

  public float getExp() {
    return this.exp;
  }

  public void setExp(final float exp) {
    this.exp = exp;
  }

  public int getTotalExp() {
    return this.totalExp;
  }

  public void setTotalExp(final int totalExp) {
    this.totalExp = totalExp;
  }

  public int getFoodLevel() {
    return this.foodLevel;
  }

  public void setFoodLevel(final int foodLevel) {
    this.foodLevel = foodLevel;
  }

  public int getFireTicks() {
    return this.fireTicks;
  }

  public void setFireTicks(final int fireTicks) {
    this.fireTicks = fireTicks;
  }

  public int getHeldSlot() {
    return this.heldSlot;
  }

  public void setHeldSlot(final int heldSlot) {
    this.heldSlot = heldSlot;
  }

  public int getLevel() {
    return this.level;
  }

  public void setLevel(final int level) {
    this.level = level;
  }

  public boolean isAllowFlight() {
    return this.allowFlight;
  }

  public void setAllowFlight(final boolean allowFlight) {
    this.allowFlight = allowFlight;
  }

  public boolean isFlying() {
    return this.flying;
  }

  public void setFlying(final boolean flying) {
    this.flying = flying;
  }

  public boolean isOp() {
    return this.op;
  }

  public void setOp(final boolean op) {
    this.op = op;
  }

  public boolean isSprinting() {
    return this.sprinting;
  }

  public void setSprinting(final boolean sprinting) {
    this.sprinting = sprinting;
  }

  public Location getLocation() {
    return this.location;
  }

  public void setLocation(final Location location) {
    this.location = location;
  }
}