package pl.rosehc.controller.guild.guild;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import pl.rosehc.controller.MasterController;
import pl.rosehc.controller.guild.guild.group.GuildGroup;
import pl.rosehc.controller.guild.guild.group.GuildGroupColor;
import pl.rosehc.controller.guild.guild.group.GuildGroupFactory;

public final class GuildHelper {

  private GuildHelper() {
  }

  public static Map<UUID, GuildGroup> deserializeGuildGroups(final String[] serializedGroupArray) {
    if (serializedGroupArray == null || serializedGroupArray.length == 0) {
      throw new UnsupportedOperationException("Wystąpił niespodziewany problem. (BRAK GRUP)");
    }

    final Map<UUID, GuildGroup> guildGroupMap = new ConcurrentHashMap<>();
    for (final String serializedGroup : serializedGroupArray) {
      final String[] splittedSerializedGroup = serializedGroup.split(";");
      final UUID uniqueId = UUID.fromString(splittedSerializedGroup[0]);
      guildGroupMap.put(uniqueId,
          new GuildGroup(uniqueId, deserializePermissions(splittedSerializedGroup[2]),
              GuildGroupColor.valueOf(splittedSerializedGroup[3]),
              MasterController.getInstance().getGuildGroupFactory().getLeaderGuildGroup()
                  .getUniqueId().equals(uniqueId),
              MasterController.getInstance().getGuildGroupFactory().getDeputyGuildGroup()
                  .getUniqueId().equals(uniqueId), splittedSerializedGroup[1]));
    }

    return guildGroupMap;
  }

  public static GuildMember[] deserializeGuildMembers(final String[] serializedMemberArray,
      final Map<UUID, GuildGroup> guildGroupMap, final int size) {
    if (serializedMemberArray == null || serializedMemberArray.length == 0) {
      throw new UnsupportedOperationException("Wystąpił niespodziewany problem. (BRAK CZŁONKÓW)");
    }

    final GuildMember[] guildMembers = new GuildMember[size];
    for (int index = 0; index < serializedMemberArray.length; index++) {
      if (serializedMemberArray[index] != null) {
        final String[] serializedMember = serializedMemberArray[index].split(";");
        final UUID userUniqueId = UUID.fromString(
            serializedMember[0]), groupUniqueId = UUID.fromString(serializedMember[1]);
        guildMembers[index] = new GuildMember(userUniqueId,
            MasterController.getInstance().getGuildUserFactory().findUserByUniqueId(userUniqueId)
                .orElseThrow(() -> new UnsupportedOperationException("Brak użytkownika!")),
            deserializePermissions(serializedMember.length > 2 ? serializedMember[2] : ""),
            Optional.ofNullable(guildGroupMap.get(groupUniqueId)).orElseThrow(
                () -> new UnsupportedOperationException(
                    "Grupa o identyfikatorze " + groupUniqueId + " nie istnieje!")));
      }
    }

    return guildMembers;
  }

  public static LinkedList<GuildRegenerationBlockState> deserializeRegenerationBlocks(
      final String[] serializedRegenerationBlockArray) {
    if (serializedRegenerationBlockArray == null || serializedRegenerationBlockArray.length == 0) {
      return null;
    }

    final LinkedList<GuildRegenerationBlockState> regenerationBlockList = new LinkedList<>();
    for (final String serializedRegenerationBlock : serializedRegenerationBlockArray) {
      final String[] splittedSerializedRegenerationBlock = serializedRegenerationBlock.split(";");
      regenerationBlockList.add(
          new GuildRegenerationBlockState(splittedSerializedRegenerationBlock[0],
              Byte.parseByte(splittedSerializedRegenerationBlock[1]),
              Integer.parseInt(splittedSerializedRegenerationBlock[2]),
              Integer.parseInt(splittedSerializedRegenerationBlock[3]),
              Integer.parseInt(splittedSerializedRegenerationBlock[4])));
    }

    return regenerationBlockList;
  }

  public static String[] serializeRegenerationBlocks(
      final LinkedList<GuildRegenerationBlockState> regenerationBlockList) {
    if (regenerationBlockList == null || regenerationBlockList.isEmpty()) {
      return null;
    }

    final String[] serializedRegenerationBlockArray = new String[regenerationBlockList.size()];
    int index = 0;
    for (final GuildRegenerationBlockState state : regenerationBlockList) {
      serializedRegenerationBlockArray[index++] =
          state.getMaterial() + ";" + state.getData() + ";" + state.getX() + ";" + state.getY()
              + ";" + state.getZ();
    }

    return serializedRegenerationBlockArray;
  }

  public static String[] serializeGuildGroups(final Map<UUID, GuildGroup> guildGroupMap) {
    final String[] serializedGroupArray = new String[guildGroupMap.size()];
    int index = 0;
    for (final Entry<UUID, GuildGroup> entry : guildGroupMap.entrySet()) {
      final GuildGroup group = entry.getValue();
      serializedGroupArray[index++] =
          entry.getKey() + ";" + group.getName() + ";" + serializePermissions(
              group.getPermissions()) + ";" + group.getColor().name();
    }

    return serializedGroupArray;
  }

  public static String[] serializeGuildMembers(final GuildMember[] members) {
    final String[] serializedMemberArray = new String[(int) Arrays.stream(members)
        .filter(Objects::nonNull).count()];
    int index = 0;
    for (final GuildMember member : members) {
      if (member != null) {
        serializedMemberArray[index++] =
            member.getUniqueId() + ";" + member.getGroup().getUniqueId() + ";"
                + serializePermissions(member.getPermissions());
      }
    }

    return serializedMemberArray;
  }

  public static Map<UUID, GuildGroup> createDefaultGuildGroups() {
    final Map<UUID, GuildGroup> defaultGuildGroupMap = new ConcurrentHashMap<>();
    final GuildGroupFactory guildGroupFactory = MasterController.getInstance()
        .getGuildGroupFactory();
    defaultGuildGroupMap.put(guildGroupFactory.getDefaultGuildGroup().getUniqueId(),
        new GuildGroup(guildGroupFactory.getDefaultGuildGroup()));
    defaultGuildGroupMap.put(guildGroupFactory.getLeaderGuildGroup().getUniqueId(),
        new GuildGroup(guildGroupFactory.getLeaderGuildGroup()));
    defaultGuildGroupMap.put(guildGroupFactory.getDeputyGuildGroup().getUniqueId(),
        new GuildGroup(guildGroupFactory.getDeputyGuildGroup()));
    for (final GuildGroup group : guildGroupFactory.getDefaultGuildGroupMap().values()) {
      if (!defaultGuildGroupMap.containsKey(group.getUniqueId())) {
        defaultGuildGroupMap.put(group.getUniqueId(), new GuildGroup(group));
      }
    }

    return defaultGuildGroupMap;
  }

  public static String serializePermissions(final Set<GuildPermissionType> permissions) {
    if (permissions == null || permissions.isEmpty()) {
      return "";
    }

    return permissions.stream().map(permission -> permission.name() + ",")
        .collect(Collectors.joining());
  }

  public static Set<GuildPermissionType> deserializePermissions(final String input) {
    if (input == null || input.trim().isEmpty()) {
      return new HashSet<>();
    }

    return Arrays.stream(input.split(",")).map(GuildPermissionType::valueOf)
        .collect(Collectors.toSet());
  }
}
