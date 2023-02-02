package pl.rosehc.controller.wrapper.guild;

import pl.rosehc.controller.guild.guild.GuildPermissionType;

@SuppressWarnings("SpellCheckingInspection")
public enum GuildPermissionTypeWrapper {

  PLACING_BLOCKS, BREAKING_BLOCKS,
  LOG_BLOCK_ACCESS, PLACING_SAND,
  PLACING_TNT, ACCEPTING_TELEPORTS_ON_TERRAIN,
  EDITING_PERMISSIONS, PLACING_LAPIS_BLOCKS,
  PLACING_COAL, PLACING_OBSIDIAN,
  CHANGING_PVP_STATE_IN_GUILD, CHANGING_PVP_STATE_IN_ALLY,
  CHEST_ACCESS, FURNACES_ACCESS, WATER_PLACING_ACCESS,
  LAVA_PLACING_ACCESS, ANVIL_PLACING_ACCESS, REDSTONE_PLACING_ACCESS;

  public static GuildPermissionTypeWrapper fromOriginal(final GuildPermissionType type) {
    return values()[type.ordinal()];
  }

  public GuildPermissionType toOriginal() {
    return GuildPermissionType.values()[this.ordinal()];
  }
}
