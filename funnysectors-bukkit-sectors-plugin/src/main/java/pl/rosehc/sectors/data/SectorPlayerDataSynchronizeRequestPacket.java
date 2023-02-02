package pl.rosehc.sectors.data;

import java.util.UUID;
import pl.rosehc.adapter.helper.SerializeHelper;
import pl.rosehc.adapter.redis.callback.CallbackPacket;
import pl.rosehc.adapter.redis.packet.PacketHandler;

/**
 * @author stevimeister on 06/01/2022
 **/
public final class SectorPlayerDataSynchronizeRequestPacket extends CallbackPacket {

  private UUID uniqueId;
  private String nickname, fromSectorName, gameMode, items, armor, enderChest, potionEffects, location;
  private double health;
  private float walkSpeed, flySpeed, exp;
  private int totalExp, foodLevel, fireTicks, heldSlot, level;
  private boolean allowFlight, flying, op, sprinting;

  private SectorPlayerDataSynchronizeRequestPacket() {
  }

  public SectorPlayerDataSynchronizeRequestPacket(final SectorPlayerData sectorPlayerData,
      final String fromSectorName) {
    this.uniqueId = sectorPlayerData.getUniqueId();
    this.nickname = sectorPlayerData.getNickname();
    this.fromSectorName = fromSectorName;
    this.gameMode = sectorPlayerData.getGameMode().name();
    this.items = SerializeHelper.serializeBukkitObject(sectorPlayerData.getItems());
    this.armor = SerializeHelper.serializeBukkitObject(sectorPlayerData.getArmor());
    this.enderChest = SerializeHelper.serializeBukkitObject(sectorPlayerData.getEnderChest());
    this.potionEffects = SerializeHelper.serializeBukkitObject(sectorPlayerData.getPotionEffects());
    this.health = sectorPlayerData.getHealth();
    this.walkSpeed = sectorPlayerData.getWalkSpeed();
    this.flySpeed = sectorPlayerData.getFlySpeed();
    this.exp = sectorPlayerData.getExp();
    this.totalExp = sectorPlayerData.getTotalExp();
    this.foodLevel = sectorPlayerData.getFoodLevel();
    this.fireTicks = sectorPlayerData.getFireTicks();
    this.heldSlot = sectorPlayerData.getHeldSlot();
    this.level = sectorPlayerData.getLevel();
    this.allowFlight = sectorPlayerData.isAllowFlight();
    this.flying = sectorPlayerData.isFlying();
    this.op = sectorPlayerData.isOp();
    this.sprinting = sectorPlayerData.isSprinting();
    this.location = SerializeHelper.serializeLocation(sectorPlayerData.getLocation());
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((SectorPlayerDataSynchronizePacketHandler) handler).handle(this);
  }

  public UUID getUniqueId() {
    return this.uniqueId;
  }

  public String getNickname() {
    return this.nickname;
  }

  public String getFromSectorName() {
    return this.fromSectorName;
  }

  public String getGameMode() {
    return this.gameMode;
  }

  public String getItems() {
    return this.items;
  }

  public String getArmor() {
    return this.armor;
  }

  public String getEnderChest() {
    return this.enderChest;
  }

  public String getPotionEffects() {
    return this.potionEffects;
  }

  public String getLocation() {
    return this.location;
  }

  public double getHealth() {
    return this.health;
  }

  public float getWalkSpeed() {
    return this.walkSpeed;
  }

  public float getFlySpeed() {
    return this.flySpeed;
  }

  public float getExp() {
    return this.exp;
  }

  public int getTotalExp() {
    return this.totalExp;
  }

  public int getFoodLevel() {
    return this.foodLevel;
  }

  public int getFireTicks() {
    return this.fireTicks;
  }

  public int getHeldSlot() {
    return this.heldSlot;
  }

  public int getLevel() {
    return this.level;
  }

  public boolean isAllowFlight() {
    return this.allowFlight;
  }

  public boolean isFlying() {
    return this.flying;
  }

  public boolean isOp() {
    return this.op;
  }

  public boolean isSprinting() {
    return this.sprinting;
  }
}