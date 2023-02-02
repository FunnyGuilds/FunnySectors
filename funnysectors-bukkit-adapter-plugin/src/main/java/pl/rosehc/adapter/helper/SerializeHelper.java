package pl.rosehc.adapter.helper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

/**
 * @author stevimeister on 30/10/2021
 **/
public final class SerializeHelper {

  private SerializeHelper() {
  }

  public static String serializeLocation(final Location location) {
    try {
      return location.getWorld().getName() + ":" + location.getX() + ":" + location.getY() + ":"
          + location.getZ() + ":" + location.getYaw() + ":" + location.getPitch();
    } catch (final Exception ex) {
      return "";
    }
  }

  public static Location deserializeLocation(final String serializedData) {
    try {
      final String[] split = serializedData.split(":");
      return new Location(Bukkit.getWorld(split[0]), Double.parseDouble(split[1]),
          Double.parseDouble(split[2]), Double.parseDouble(split[3]), Float.parseFloat(split[4]),
          Float.parseFloat(split[5]));
    } catch (final Exception ex) {
      return null;
    }
  }

  public static String serializeBukkitObject(final Object object) {
    try (final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(); final BukkitObjectOutputStream bukkitObjectOutputStream = new BukkitObjectOutputStream(
        byteArrayOutputStream)) {
      bukkitObjectOutputStream.writeObject(object);
      return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
    } catch (Exception ex) {
      throw new IllegalStateException("Unable to serialize object", ex);
    }
  }

  public static Object deserializeBukkitObject(final String base64) {
    try (final BukkitObjectInputStream bukkitObjectInputStream = new BukkitObjectInputStream(
        new ByteArrayInputStream(Base64.getDecoder().decode(base64)))) {
      return bukkitObjectInputStream.readObject();
    } catch (Exception ex) {
      throw new IllegalStateException("Unable to deserialize object", ex);
    }
  }

  public static byte[] serializeBukkitObjectToBytes(final Object object) {
    try (final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(); final BukkitObjectOutputStream bukkitObjectOutputStream = new BukkitObjectOutputStream(
        byteArrayOutputStream)) {
      bukkitObjectOutputStream.writeObject(object);
      return Base64.getEncoder().encode(byteArrayOutputStream.toByteArray());
    } catch (Exception ex) {
      throw new IllegalStateException("Unable to serialize object", ex);
    }
  }

  public static Object deserializeBukkitObjectFromBytes(final byte[] base64) {
    try (final BukkitObjectInputStream bukkitObjectInputStream = new BukkitObjectInputStream(
        new ByteArrayInputStream(Base64.getDecoder().decode(base64)))) {
      return bukkitObjectInputStream.readObject();
    } catch (Exception ex) {
      throw new IllegalStateException("Unable to deserialize object", ex);
    }
  }
}
