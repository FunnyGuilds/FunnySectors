package pl.rosehc.controller.platform.user;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import pl.rosehc.controller.wrapper.platform.PlatformUserCooldownType;
import pl.rosehc.controller.wrapper.platform.PlatformUserDepositItemTypeWrapper;

public final class PlatformUserSerializationHelper {

  private PlatformUserSerializationHelper() {
  }

  public static Map<PlatformUserCooldownType, Long> deserializeCooldownMap(final String input) {
    final Map<PlatformUserCooldownType, Long> cooldownMap = new ConcurrentHashMap<>();
    if (input == null || input.trim().isEmpty()) {
      return cooldownMap;
    }

    final String[] split = input.split("@");
    for (final String cooldown : split) {
      final String[] cooldownSplit = cooldown.split(";");
      if (cooldownSplit.length >= 2) {
        cooldownMap.put(PlatformUserCooldownType.valueOf(cooldownSplit[0]),
            Long.parseLong(cooldownSplit[1]));
      }
    }

    return cooldownMap;
  }

  public static String serializeCooldownMap(final Map<PlatformUserCooldownType, Long> cooldownMap) {
    final StringBuilder builder = new StringBuilder();
    for (final Entry<PlatformUserCooldownType, Long> entry : cooldownMap.entrySet()) {
      builder.append(entry.getKey().name()).append(";").append(entry.getValue()).append("@");
    }

    return builder.toString();
  }

  public static Map<String, Long> deserializeReceivedKitMap(final String input) {
    if (input == null || input.trim().isEmpty()) {
      return null;
    }

    final Map<String, Long> receivedKitMap = new ConcurrentHashMap<>();
    final String[] split = input.split("@");
    for (final String kit : split) {
      final String[] kitSplit = kit.split(";");
      if (kitSplit.length >= 2) {
        receivedKitMap.put(kitSplit[0], Long.parseLong(kitSplit[1]));
      }
    }

    return receivedKitMap;
  }

  public static String serializeReceivedKitMap(final Map<String, Long> receivedKitMap) {
    if (Objects.isNull(receivedKitMap)) {
      return null;
    }

    final StringBuilder builder = new StringBuilder();
    for (final Entry<String, Long> entry : receivedKitMap.entrySet()) {
      builder.append(entry.getKey()).append(";").append(entry.getValue()).append("@");
    }

    return builder.toString();
  }

  public static Map<PlatformUserDepositItemTypeWrapper, Integer> deserializeDepositItemMap(
      final String input) {
    final Map<PlatformUserDepositItemTypeWrapper, Integer> depositItemMap = new ConcurrentHashMap<>();
    if (input == null || input.trim().isEmpty()) {
      return depositItemMap;
    }

    final String[] split = input.split(";");
    for (final String depositItem : split) {
      final String[] depositItemSplit = depositItem.split("@");
      if (depositItemSplit.length >= 2) {
        depositItemMap.put(PlatformUserDepositItemTypeWrapper.valueOf(depositItemSplit[0]),
            Integer.parseInt(depositItemSplit[1]));
      }
    }

    return depositItemMap;
  }

  public static String serializeDepositItemMap(
      final Map<PlatformUserDepositItemTypeWrapper, Integer> depositItemMap) {
    if (Objects.isNull(depositItemMap)) {
      return null;
    }

    final StringBuilder builder = new StringBuilder();
    for (final Entry<PlatformUserDepositItemTypeWrapper, Integer> entry : depositItemMap.entrySet()) {
      builder.append(entry.getKey().name()).append("@").append(entry.getValue()).append(";");
    }

    return builder.toString();
  }
}
