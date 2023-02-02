package pl.rosehc.controller.configuration.impl.handler;

import java.util.stream.Collectors;
import pl.rosehc.controller.MasterController;
import pl.rosehc.controller.configuration.ConfigurationReloadHandler;
import pl.rosehc.controller.configuration.impl.configuration.GuildsConfiguration;
import pl.rosehc.controller.guild.guild.GuildSchematicCacheHelper;
import pl.rosehc.controller.guild.guild.group.GuildGroup;
import pl.rosehc.controller.guild.guild.group.GuildGroupFactory;
import pl.rosehc.controller.packet.guild.GuildCuboidSchematicSynchronizePacket;
import pl.rosehc.controller.wrapper.guild.GuildPermissionTypeWrapper;

public final class GuildsConfigurationReloadHandler implements
    ConfigurationReloadHandler<GuildsConfiguration> {

  private final MasterController masterController;

  public GuildsConfigurationReloadHandler(final MasterController masterController) {
    this.masterController = masterController;
  }

  @Override
  public void handle(final GuildsConfiguration guildsConfiguration) {
    final GuildGroupFactory guildGroupFactory = this.masterController.getGuildGroupFactory();
    GuildSchematicCacheHelper.cacheSchematicData(guildsConfiguration);
    guildGroupFactory.getDefaultGuildGroupMap().clear();
    guildGroupFactory.getDefaultGuildGroupMap().putAll(
        guildsConfiguration.pluginWrapper.groupMap.entrySet().stream().map(
                entry -> new GuildGroup(entry.getKey(),
                    entry.getValue().permissions.stream().map(GuildPermissionTypeWrapper::toOriginal)
                        .collect(Collectors.toSet()), entry.getValue().color.toOriginal(),
                    entry.getValue().leader, entry.getValue().deputy, entry.getValue().name))
            .collect(Collectors.toConcurrentMap(GuildGroup::getUniqueId, group -> group)));
    guildGroupFactory.updateDefaultGroups(guildsConfiguration);
    this.masterController.getRedisAdapter().sendPacket(
        new GuildCuboidSchematicSynchronizePacket(GuildSchematicCacheHelper.getSchematicData()),
        "rhc_guilds");
  }
}
