package pl.rosehc.sectors.data;

import com.mojang.authlib.GameProfile;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import net.minecraft.server.v1_8_R3.DedicatedPlayerList;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.MobEffect;
import net.minecraft.server.v1_8_R3.MobEffectList;
import net.minecraft.server.v1_8_R3.NBTCompressedStreamTools;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagDouble;
import net.minecraft.server.v1_8_R3.NBTTagFloat;
import net.minecraft.server.v1_8_R3.NBTTagList;
import net.minecraft.server.v1_8_R3.PlayerAbilities;
import net.minecraft.server.v1_8_R3.PlayerInteractManager;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.sectors.SectorsPlugin;

public final class SectorPlayerDataSynchronizePacketHandler implements PacketHandler {

  private final SectorsPlugin plugin;

  public SectorPlayerDataSynchronizePacketHandler(final SectorsPlugin plugin) {
    this.plugin = plugin;
  }

  public void handle(final SectorPlayerDataSynchronizeRequestPacket packet) {
    final SectorPlayerData data = SectorPlayerData.of(packet);
    final World world = MinecraftServer.getServer().getWorld();
    final EntityPlayer entityPlayer = new EntityPlayer(MinecraftServer.getServer(),
        MinecraftServer.getServer().getWorldServer(0),
        new GameProfile(data.getUniqueId(), data.getNickname()), new PlayerInteractManager(world));
    NBTTagCompound compound = MinecraftServer.getServer().getPlayerList().a(entityPlayer);
    if (Objects.isNull(compound)) {
      compound = new NBTTagCompound();
      entityPlayer.b(compound);
    }

    final NBTTagList positionList = new NBTTagList();
    final NBTTagList rotationList = new NBTTagList();
    final Location location = data.getLocation();
    final ItemStack[] items = data.getItems();
    final PotionEffect[] effects = data.getPotionEffects();
    final PlayerAbilities abilities = entityPlayer.abilities;
    final NBTTagList activeEffects = new NBTTagList();
    final int heldSlot = data.getHeldSlot();
    positionList.add(new NBTTagDouble(location.getX()));
    positionList.add(new NBTTagDouble(location.getY()));
    positionList.add(new NBTTagDouble(location.getZ()));
    rotationList.add(new NBTTagFloat(location.getYaw()));
    rotationList.add(new NBTTagFloat(location.getPitch()));
    compound.set("Pos", positionList);
    compound.set("Rotation", rotationList);
    compound.setLong("WorldUUIDLeast",
        ((CraftWorld) location.getWorld()).getHandle().getDataManager().getUUID()
            .getLeastSignificantBits());
    compound.setLong("WorldUUIDMost",
        ((CraftWorld) location.getWorld()).getHandle().getDataManager().getUUID()
            .getMostSignificantBits());
    compound.setFloat("XpP", data.getExp());
    compound.setInt("XpLevel", data.getLevel());
    compound.setInt("XpTotal", data.getTotalExp());
    compound.setInt("SelectedItemSlot", heldSlot);
    compound.setShort("Fire", (short) data.getFireTicks());
    abilities.walkSpeed = data.getWalkSpeed() / 2F;
    abilities.flySpeed = data.getFlySpeed() / 2F;
    abilities.canFly = data.isAllowFlight();
    abilities.isFlying = data.isFlying();
    abilities.a(compound);
    entityPlayer.setSprinting(data.isSprinting());
    if (effects.length > 0) {
      for (final PotionEffect effect : effects) {
        final MobEffect mobEffect = new MobEffect(effect.getType().getId(), effect.getDuration(),
            effect.getAmplifier(), effect.isAmbient(), effect.hasParticles());
        final MobEffectList mobEffectList = MobEffectList.byId[mobEffect.getEffectId()];
        activeEffects.add(mobEffect.a(new NBTTagCompound()));
        mobEffectList.a(entityPlayer, entityPlayer.getAttributeMap(), mobEffect.getAmplifier());
        mobEffectList.b(entityPlayer, entityPlayer.getAttributeMap(), mobEffect.getAmplifier());
      }
    } else {
      for (final MobEffect effect : entityPlayer.getEffects()) {
        MobEffectList.byId[effect.getEffectId()].a(entityPlayer, entityPlayer.getAttributeMap(),
            effect.getAmplifier());
      }
    }

    compound.set("ActiveEffects", activeEffects);
    compound.set("Attributes", GenericAttributes.a(entityPlayer.getAttributeMap()));
    if (heldSlot >= 0 && heldSlot < 9) {
      final ItemStack heldItem = items[heldSlot];
      if (Objects.nonNull(heldItem)) {
        compound.set("SelectedItem", CraftItemStack.asNMSCopy(heldItem).save(new NBTTagCompound()));
      }
    }

    final DedicatedPlayerList server = ((CraftServer) Bukkit.getServer()).getHandle();
    if (packet.isOp() != server.isOp(entityPlayer.getProfile())) {
      if (packet.isOp()) {
        server.addOp(entityPlayer.getProfile());
      } else {
        server.removeOp(entityPlayer.getProfile());
      }
    }

    final NBTTagList inventory = new NBTTagList();
    final NBTTagList enderContents = new NBTTagList();
    final ItemStack[] armor = data.getArmor();
    final ItemStack[] enderChest = data.getEnderChest();
    saveContents(inventory, items);
    saveContents(enderContents, enderChest);

    for (int slot = 0; slot < armor.length; slot++) {
      final ItemStack item = armor[slot];
      if (Objects.nonNull(item) && !item.getType().equals(Material.AIR)) {
        final NBTTagCompound itemCompound = new NBTTagCompound();
        itemCompound.setByte("Slot", (byte) (slot + 100));
        CraftItemStack.asNMSCopy(item).save(itemCompound);
        inventory.add(itemCompound);
      }
    }

    compound.set("Inventory", inventory);
    compound.set("EnderItems", enderContents);
    compound.setFloat("HealF", (float) data.getHealth());
    compound.setShort("Health", (short) (int) Math.ceil(data.getHealth()));
    compound.setInt("playerGameType", data.getGameMode().getValue());
    compound.setFloat("FallDistance", 0F);
    compound.setShort("Air", (short) 0);

    final SectorPlayerDataSynchronizeResponsePacket responsePacket = new SectorPlayerDataSynchronizeResponsePacket(
        data.getUniqueId());
    responsePacket.setCallbackId(packet.getCallbackId());
    responsePacket.setResponse(true);
    try (final FileOutputStream fileOutputStream = new FileOutputStream(
        new File(new File(world.getDataManager().getDirectory(), "playerdata"),
            data.getUniqueId() + ".dat"))) {
      NBTCompressedStreamTools.a(compound, fileOutputStream);
      responsePacket.setSuccess(true);
    } catch (final IOException ex) {
      ex.printStackTrace();
      responsePacket.setResponseText(ChatHelper.colored(
          this.plugin.getSectorsConfiguration().messagesWrapper.cannotSynchronizeYourData));
    }

    this.plugin.getRedisAdapter()
        .sendPacket(responsePacket, "rhc_playerdata_" + packet.getFromSectorName());
  }

  private void saveContents(final NBTTagList list, final ItemStack[] contents) {
    for (int slot = 0; slot < contents.length; slot++) {
      final ItemStack item = contents[slot];
      if (Objects.nonNull(item) && !item.getType().equals(Material.AIR)) {
        final NBTTagCompound itemCompound = new NBTTagCompound();
        itemCompound.setByte("Slot", (byte) slot);
        CraftItemStack.asNMSCopy(item).save(itemCompound);
        list.add(itemCompound);
      }
    }
  }
}
