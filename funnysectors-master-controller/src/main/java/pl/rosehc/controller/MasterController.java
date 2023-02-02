package pl.rosehc.controller;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import pl.rosehc.adapter.database.DatabaseAdapter;
import pl.rosehc.adapter.helper.TimeHelper;
import pl.rosehc.adapter.redis.RedisAdapter;
import pl.rosehc.adapter.redis.callback.CallbackFactory;
import pl.rosehc.controller.auth.AuthUser;
import pl.rosehc.controller.auth.AuthUserFactory;
import pl.rosehc.controller.auth.AuthUserRepository;
import pl.rosehc.controller.auth.AuthUserUpdateTask;
import pl.rosehc.controller.configuration.ConfigurationFactory;
import pl.rosehc.controller.configuration.ConfigurationHelper;
import pl.rosehc.controller.configuration.impl.configuration.AchievementsConfiguration;
import pl.rosehc.controller.configuration.impl.configuration.AuthConfiguration;
import pl.rosehc.controller.configuration.impl.configuration.DatabaseConfiguration;
import pl.rosehc.controller.configuration.impl.configuration.GuildsConfiguration;
import pl.rosehc.controller.configuration.impl.configuration.LinkerRandomTPConfiguration;
import pl.rosehc.controller.configuration.impl.configuration.PlatformConfiguration;
import pl.rosehc.controller.configuration.impl.configuration.PlatformConfiguration.SpecialBossBarWrapper;
import pl.rosehc.controller.configuration.impl.configuration.ProtectionConfiguration;
import pl.rosehc.controller.configuration.impl.configuration.SectorsConfiguration;
import pl.rosehc.controller.configuration.impl.configuration.SystemRandomTPConfiguration;
import pl.rosehc.controller.configuration.impl.configuration.TradeConfiguration;
import pl.rosehc.controller.configuration.impl.handler.GuildsConfigurationReloadHandler;
import pl.rosehc.controller.configuration.impl.handler.OnlySynchronizationReloadHandler;
import pl.rosehc.controller.configuration.impl.handler.PlatformConfigurationReloadHandler;
import pl.rosehc.controller.configuration.impl.handler.SectorsConfigurationReloadHandler;
import pl.rosehc.controller.guild.guild.Guild;
import pl.rosehc.controller.guild.guild.GuildFactory;
import pl.rosehc.controller.guild.guild.GuildRepository;
import pl.rosehc.controller.guild.guild.GuildSchematicCacheHelper;
import pl.rosehc.controller.guild.guild.GuildUpdateTask;
import pl.rosehc.controller.guild.guild.group.GuildGroupFactory;
import pl.rosehc.controller.guild.user.GuildUser;
import pl.rosehc.controller.guild.user.GuildUserFactory;
import pl.rosehc.controller.guild.user.GuildUserRepository;
import pl.rosehc.controller.guild.user.GuildUserUpdateTask;
import pl.rosehc.controller.packet.AuthPacketHandler;
import pl.rosehc.controller.packet.DiscordPacketHandler;
import pl.rosehc.controller.packet.GuildPacketHandler;
import pl.rosehc.controller.packet.PlatformPacketHandler;
import pl.rosehc.controller.packet.ProtectionPacketHandler;
import pl.rosehc.controller.packet.ProxyPacketHandler;
import pl.rosehc.controller.packet.RandomTPPacketHandler;
import pl.rosehc.controller.packet.SectorPacketHandler;
import pl.rosehc.controller.packet.TradePacketHandler;
import pl.rosehc.controller.packet.auth.AuthInitializationRequestPacket;
import pl.rosehc.controller.packet.auth.user.AuthUserCreatePacket;
import pl.rosehc.controller.packet.auth.user.AuthUserDeletePacket;
import pl.rosehc.controller.packet.auth.user.AuthUserLastIPUpdatePacket;
import pl.rosehc.controller.packet.auth.user.AuthUserLastOnlineUpdatePacket;
import pl.rosehc.controller.packet.auth.user.AuthUserMarkRegisteredPacket;
import pl.rosehc.controller.packet.auth.user.AuthUserPasswordUpdatePacket;
import pl.rosehc.controller.packet.auth.user.AuthUserSetPremiumStatePacket;
import pl.rosehc.controller.packet.configuration.ConfigurationReloadPacket;
import pl.rosehc.controller.packet.controller.ControllerUpdateTimePacket;
import pl.rosehc.controller.packet.discord.DiscordRewardVerificationRequestPacket;
import pl.rosehc.controller.packet.guild.GuildCuboidSchematicSynchronizePacket;
import pl.rosehc.controller.packet.guild.GuildsInitializationRequestPacket;
import pl.rosehc.controller.packet.guild.guild.GuildAddRegenerationBlocksPacket;
import pl.rosehc.controller.packet.guild.guild.GuildAlertPacket;
import pl.rosehc.controller.packet.guild.guild.GuildAllyInviteEntryUpdatePacket;
import pl.rosehc.controller.packet.guild.guild.GuildCreatePacket;
import pl.rosehc.controller.packet.guild.guild.GuildDeletePacket;
import pl.rosehc.controller.packet.guild.guild.GuildGuildsRequestPacket;
import pl.rosehc.controller.packet.guild.guild.GuildHelpInfoAddPacket;
import pl.rosehc.controller.packet.guild.guild.GuildHelpInfoRemovePacket;
import pl.rosehc.controller.packet.guild.guild.GuildHelpInfoUpdatePacket;
import pl.rosehc.controller.packet.guild.guild.GuildHomeLocationUpdatePacket;
import pl.rosehc.controller.packet.guild.guild.GuildJoinAlertMessageUpdatePacket;
import pl.rosehc.controller.packet.guild.guild.GuildMemberAddPacket;
import pl.rosehc.controller.packet.guild.guild.GuildMemberInviteAddPacket;
import pl.rosehc.controller.packet.guild.guild.GuildMemberInviteRemovePacket;
import pl.rosehc.controller.packet.guild.guild.GuildMemberRemovePacket;
import pl.rosehc.controller.packet.guild.guild.GuildMemberUpdateRankPacket;
import pl.rosehc.controller.packet.guild.guild.GuildPistonsUpdatePacket;
import pl.rosehc.controller.packet.guild.guild.GuildPvPUpdatePacket;
import pl.rosehc.controller.packet.guild.guild.GuildRegionUpdateSizePacket;
import pl.rosehc.controller.packet.guild.guild.GuildUpdateAllyPacket;
import pl.rosehc.controller.packet.guild.guild.GuildValidityTimeUpdatePacket;
import pl.rosehc.controller.packet.guild.user.GuildUserCacheFighterPacket;
import pl.rosehc.controller.packet.guild.user.GuildUserCacheVictimPacket;
import pl.rosehc.controller.packet.guild.user.GuildUserClearFightersPacket;
import pl.rosehc.controller.packet.guild.user.GuildUserCreatePacket;
import pl.rosehc.controller.packet.guild.user.GuildUserSynchronizeRankingPacket;
import pl.rosehc.controller.packet.guild.user.GuildUsersRequestPacket;
import pl.rosehc.controller.packet.platform.PlatformChatStateChangePacket;
import pl.rosehc.controller.packet.platform.PlatformDropSettingsUpdateTurboDropPacket;
import pl.rosehc.controller.packet.platform.PlatformInitializationRequestPacket;
import pl.rosehc.controller.packet.platform.PlatformMotdSettingsSynchronizePacket;
import pl.rosehc.controller.packet.platform.PlatformSetFreezeStatePacket;
import pl.rosehc.controller.packet.platform.PlatformSetMotdCounterPlayerLimitPacket;
import pl.rosehc.controller.packet.platform.PlatformSetSlotsPacket;
import pl.rosehc.controller.packet.platform.PlatformSetSpawnPacket;
import pl.rosehc.controller.packet.platform.PlatformSpecialBossBarUpdatePacket;
import pl.rosehc.controller.packet.platform.ban.PlatformBanComputerUidUpdatePacket;
import pl.rosehc.controller.packet.platform.ban.PlatformBanCreatePacket;
import pl.rosehc.controller.packet.platform.ban.PlatformBanDeletePacket;
import pl.rosehc.controller.packet.platform.ban.PlatformBanIpUpdatePacket;
import pl.rosehc.controller.packet.platform.ban.PlatformBansRequestPacket;
import pl.rosehc.controller.packet.platform.end.PlatformEndPortalPointCreatePacket;
import pl.rosehc.controller.packet.platform.end.PlatformEndPortalPointDeletePacket;
import pl.rosehc.controller.packet.platform.safe.PlatformSafeContentsModifyPacket;
import pl.rosehc.controller.packet.platform.safe.PlatformSafeCreatePacket;
import pl.rosehc.controller.packet.platform.safe.PlatformSafeDescriptionUpdatePacket;
import pl.rosehc.controller.packet.platform.safe.PlatformSafeLastOpenedTimeUpdatePacket;
import pl.rosehc.controller.packet.platform.safe.PlatformSafeModifierUpdatePacket;
import pl.rosehc.controller.packet.platform.safe.PlatformSafeOwnerUpdatePacket;
import pl.rosehc.controller.packet.platform.safe.request.PlatformSafeDataRequestPacket;
import pl.rosehc.controller.packet.platform.safe.request.PlatformSafeModificationRequestPacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserAddDepositLimitsPacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserCombatTimeUpdatePacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserComputerUidUpdatePacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserCooldownSynchronizePacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserCreatePacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserDisableFirstJoinStatePacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserDropSettingsAddDisabledDropPacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserDropSettingsRemoveDisabledDropPacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserGodStateUpdatePacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserIgnoredPlayerUpdatePacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserLastPrivateMessageUpdatePacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserNicknameUpdatePacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserRankUpdatePacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserReceiveKitPacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserRemoveDepositLimitsPacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserSetHomePacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserSynchronizeChatSettingsPacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserSynchronizeSomeDropSettingsDataPacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserTeleportRequestUpdatePacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserVanishStateUpdatePacket;
import pl.rosehc.controller.packet.platform.user.PlatformUsersRequestPacket;
import pl.rosehc.controller.packet.platform.whitelist.PlatformWhitelistChangeStatePacket;
import pl.rosehc.controller.packet.platform.whitelist.PlatformWhitelistSetReasonPacket;
import pl.rosehc.controller.packet.platform.whitelist.PlatformWhitelistUpdatePlayerPacket;
import pl.rosehc.controller.packet.protection.ProtectionConfigurationRequestPacket;
import pl.rosehc.controller.packet.proxy.ProxyInitializationRequestPacket;
import pl.rosehc.controller.packet.proxy.ProxyUpdateStatisticsPacket;
import pl.rosehc.controller.packet.randomtp.RandomTPConfigurationRequestPacket;
import pl.rosehc.controller.packet.sector.SectorInitializationRequestPacket;
import pl.rosehc.controller.packet.sector.SectorUpdateStatisticsPacket;
import pl.rosehc.controller.packet.sector.user.SectorUserCreatePacket;
import pl.rosehc.controller.packet.sector.user.SectorUserDeletePacket;
import pl.rosehc.controller.packet.sector.user.SectorUserUpdateSectorPacket;
import pl.rosehc.controller.packet.trade.TradeConfigurationRequestPacket;
import pl.rosehc.controller.platform.ban.Ban;
import pl.rosehc.controller.platform.ban.BanFactory;
import pl.rosehc.controller.platform.ban.BanRepository;
import pl.rosehc.controller.platform.ban.BanUpdateTask;
import pl.rosehc.controller.platform.rank.RankFactory;
import pl.rosehc.controller.platform.safe.Safe;
import pl.rosehc.controller.platform.safe.SafeFactory;
import pl.rosehc.controller.platform.safe.SafeRepository;
import pl.rosehc.controller.platform.safe.task.SafeModifierUpdateTask;
import pl.rosehc.controller.platform.safe.task.SafeUpdateTask;
import pl.rosehc.controller.platform.task.PlatformCheckSpecialBarTask;
import pl.rosehc.controller.platform.task.PlatformPickTurboDropPlayersTask;
import pl.rosehc.controller.platform.user.PlatformUser;
import pl.rosehc.controller.platform.user.PlatformUserFactory;
import pl.rosehc.controller.platform.user.PlatformUserRepository;
import pl.rosehc.controller.platform.user.task.PlatformUserSendAutoMessageTask;
import pl.rosehc.controller.platform.user.task.PlatformUserUpdateTask;
import pl.rosehc.controller.proxy.ProxyFactory;
import pl.rosehc.controller.sector.SectorFactory;
import pl.rosehc.controller.sector.user.SectorUserFactory;

public final class MasterController {

  public static final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(
      Runtime.getRuntime().availableProcessors() * 2);
  private static MasterController instance;
  private final CallbackFactory callbackFactory = new CallbackFactory();
  private final ConfigurationFactory configurationFactory = new ConfigurationFactory();

  private DatabaseAdapter databaseAdapter;
  private RedisAdapter redisAdapter;
  private SectorFactory sectorFactory;
  private ProxyFactory proxyFactory;
  private SectorUserFactory sectorUserFactory;
  private RankFactory rankFactory;
  private PlatformUserRepository platformUserRepository;
  private PlatformUserFactory platformUserFactory;
  private BanRepository banRepository;
  private BanFactory banFactory;
  private SafeRepository safeRepository;
  private SafeFactory safeFactory;
  private GuildUserRepository guildUserRepository;
  private GuildUserFactory guildUserFactory;
  private GuildGroupFactory guildGroupFactory;
  private GuildRepository guildRepository;
  private GuildFactory guildFactory;
  private AuthUserRepository authUserRepository;
  private AuthUserFactory authUserFactory;
  private ScheduledFuture<?> autoMessageTaskFuture;

  public static MasterController getInstance() {
    return instance;
  }

  public void stop() {
    try {
      SCHEDULER.shutdown();
      //noinspection ResultOfMethodCallIgnored
      SCHEDULER.awaitTermination(1L, TimeUnit.MINUTES);
    } catch (final InterruptedException ex) {
      ex.printStackTrace();
    }

    try {
      this.shutdownPlatform();
      this.shutdownGuilds();
      this.shutdownAuth();
    } catch (final SQLException ex) {
      System.err.println("Wystąpił niespodziewany problem podczas próby wyłączenia aplikacji.");
      ex.printStackTrace();
    }

    if (Objects.nonNull(this.databaseAdapter)) {
      this.databaseAdapter.close();
    }
    if (Objects.nonNull(this.redisAdapter)) {
      this.redisAdapter.disconnect();
    }
  }

  public void start() throws Exception {
    instance = this;
    this.loadConfigurations();
    final SectorsConfiguration sectorsConfiguration = this.configurationFactory.findConfiguration(
        SectorsConfiguration.class);
    final PlatformConfiguration platformConfiguration = this.configurationFactory.findConfiguration(
        PlatformConfiguration.class);
    this.sectorFactory = new SectorFactory(sectorsConfiguration);
    this.proxyFactory = new ProxyFactory(sectorsConfiguration);
    this.sectorUserFactory = new SectorUserFactory();
    this.rankFactory = new RankFactory(platformConfiguration);
    this.loadDatabase();
    this.initPlatform();
    this.initGuilds();
    this.initAuth();
    this.registerPacketHandlers();
    SCHEDULER.scheduleAtFixedRate(
        () -> this.redisAdapter.sendPacket(new ControllerUpdateTimePacket(), "rhc_sectors"), 1L, 1L,
        TimeUnit.SECONDS);
  }

  public DatabaseAdapter getDatabaseAdapter() {
    return this.databaseAdapter;
  }

  public RedisAdapter getRedisAdapter() {
    return this.redisAdapter;
  }

  public CallbackFactory getCallbackFactory() {
    return this.callbackFactory;
  }

  public ConfigurationFactory getConfigurationFactory() {
    return this.configurationFactory;
  }

  public SectorFactory getSectorFactory() {
    return this.sectorFactory;
  }

  public ProxyFactory getProxyFactory() {
    return this.proxyFactory;
  }

  public SectorUserFactory getSectorUserFactory() {
    return this.sectorUserFactory;
  }

  public RankFactory getRankFactory() {
    return this.rankFactory;
  }

  public PlatformUserRepository getPlatformUserRepository() {
    return this.platformUserRepository;
  }

  public PlatformUserFactory getPlatformUserFactory() {
    return this.platformUserFactory;
  }

  public BanRepository getBanRepository() {
    return this.banRepository;
  }

  public BanFactory getBanFactory() {
    return this.banFactory;
  }

  public SafeRepository getSafeRepository() {
    return this.safeRepository;
  }

  public SafeFactory getSafeFactory() {
    return this.safeFactory;
  }

  public GuildUserRepository getGuildUserRepository() {
    return this.guildUserRepository;
  }

  public GuildUserFactory getGuildUserFactory() {
    return this.guildUserFactory;
  }

  public GuildGroupFactory getGuildGroupFactory() {
    return this.guildGroupFactory;
  }

  public GuildRepository getGuildRepository() {
    return this.guildRepository;
  }

  public GuildFactory getGuildFactory() {
    return this.guildFactory;
  }

  public AuthUserRepository getAuthUserRepository() {
    return this.authUserRepository;
  }

  public AuthUserFactory getAuthUserFactory() {
    return this.authUserFactory;
  }

  public ScheduledFuture<?> getAutoMessageTaskFuture() {
    return this.autoMessageTaskFuture;
  }

  public void setAutoMessageTaskFuture(final ScheduledFuture<?> autoMessageTaskFuture) {
    this.autoMessageTaskFuture = autoMessageTaskFuture;
  }

  private void registerPacketHandlers() {
    this.redisAdapter.subscribe(new PlatformPacketHandler(this), "rhc_master_controller",
        Arrays.asList(
            PlatformInitializationRequestPacket.class,
            PlatformUsersRequestPacket.class,
            PlatformUserCreatePacket.class,
            PlatformUserCooldownSynchronizePacket.class,
            PlatformUserSetHomePacket.class,
            PlatformUserAddDepositLimitsPacket.class,
            PlatformUserRemoveDepositLimitsPacket.class,
            PlatformUserReceiveKitPacket.class,
            PlatformUserVanishStateUpdatePacket.class,
            PlatformUserNicknameUpdatePacket.class,
            PlatformUserRankUpdatePacket.class,
            PlatformUserTeleportRequestUpdatePacket.class,
            PlatformUserLastPrivateMessageUpdatePacket.class,
            PlatformUserCombatTimeUpdatePacket.class,
            PlatformUserVanishStateUpdatePacket.class,
            PlatformUserGodStateUpdatePacket.class,
            PlatformUserDisableFirstJoinStatePacket.class,
            PlatformUserSynchronizeSomeDropSettingsDataPacket.class,
            PlatformUserDropSettingsAddDisabledDropPacket.class,
            PlatformUserDropSettingsRemoveDisabledDropPacket.class,
            PlatformUserIgnoredPlayerUpdatePacket.class,
            PlatformUserSynchronizeChatSettingsPacket.class,
            PlatformUserIgnoredPlayerUpdatePacket.class,
            PlatformUserComputerUidUpdatePacket.class,
            PlatformSafeDataRequestPacket.class,
            PlatformSafeModificationRequestPacket.class,
            PlatformSafeContentsModifyPacket.class,
            PlatformSafeCreatePacket.class,
            PlatformSafeDescriptionUpdatePacket.class,
            PlatformSafeModifierUpdatePacket.class,
            PlatformSafeLastOpenedTimeUpdatePacket.class,
            PlatformSafeOwnerUpdatePacket.class,
            PlatformBansRequestPacket.class,
            PlatformBanCreatePacket.class,
            PlatformBanDeletePacket.class,
            PlatformBanComputerUidUpdatePacket.class,
            PlatformBanIpUpdatePacket.class,
            PlatformSetSpawnPacket.class,
            PlatformSetSlotsPacket.class,
            PlatformSetFreezeStatePacket.class,
            PlatformSetMotdCounterPlayerLimitPacket.class,
            PlatformSpecialBossBarUpdatePacket.class,
            PlatformEndPortalPointCreatePacket.class,
            PlatformEndPortalPointDeletePacket.class,
            PlatformChatStateChangePacket.class,
            PlatformDropSettingsUpdateTurboDropPacket.class,
            PlatformMotdSettingsSynchronizePacket.class,
            PlatformWhitelistChangeStatePacket.class,
            PlatformWhitelistUpdatePlayerPacket.class,
            PlatformWhitelistSetReasonPacket.class
        ));
    this.redisAdapter.subscribe(new GuildPacketHandler(this), "rhc_master_controller",
        Arrays.asList(
            GuildsInitializationRequestPacket.class,
            GuildCuboidSchematicSynchronizePacket.class,
            GuildUserCacheFighterPacket.class,
            GuildUserCacheVictimPacket.class,
            GuildUserClearFightersPacket.class,
            GuildUserCreatePacket.class,
            GuildUsersRequestPacket.class,
            GuildUserSynchronizeRankingPacket.class,
            GuildAddRegenerationBlocksPacket.class,
            GuildAlertPacket.class,
            GuildAllyInviteEntryUpdatePacket.class,
            GuildCreatePacket.class,
            GuildDeletePacket.class,
            GuildHelpInfoAddPacket.class,
            GuildHelpInfoRemovePacket.class,
            GuildHelpInfoUpdatePacket.class,
            GuildHomeLocationUpdatePacket.class,
            GuildJoinAlertMessageUpdatePacket.class,
            GuildMemberAddPacket.class,
            GuildMemberInviteAddPacket.class,
            GuildMemberInviteRemovePacket.class,
            GuildMemberRemovePacket.class,
            GuildPistonsUpdatePacket.class,
            GuildPvPUpdatePacket.class,
            GuildRegionUpdateSizePacket.class,
            GuildUpdateAllyPacket.class,
            GuildValidityTimeUpdatePacket.class,
            GuildMemberUpdateRankPacket.class,
            GuildGuildsRequestPacket.class
        ));
    this.redisAdapter.subscribe(new AuthPacketHandler(this), "rhc_master_controller", Arrays.asList(
        AuthInitializationRequestPacket.class,
        AuthUserCreatePacket.class,
        AuthUserDeletePacket.class,
        AuthUserLastIPUpdatePacket.class,
        AuthUserLastOnlineUpdatePacket.class,
        AuthUserMarkRegisteredPacket.class,
        AuthUserPasswordUpdatePacket.class,
        AuthUserSetPremiumStatePacket.class
    ));
    this.redisAdapter.subscribe(new SectorPacketHandler(this), Arrays.asList(
        "rhc_master_controller",
        "rhc_global"
    ), Arrays.asList(
        ConfigurationReloadPacket.class,
        SectorInitializationRequestPacket.class,
        SectorUpdateStatisticsPacket.class,
        SectorUserCreatePacket.class,
        SectorUserDeletePacket.class,
        SectorUserUpdateSectorPacket.class
    ));
    this.redisAdapter.subscribe(new ProxyPacketHandler(this), Arrays.asList(
        "rhc_master_controller",
        "rhc_global"
    ), Arrays.asList(
        ProxyInitializationRequestPacket.class,
        ProxyUpdateStatisticsPacket.class
    ));
    this.redisAdapter.subscribe(new RandomTPPacketHandler(this), "rhc_master_controller",
        Collections.singletonList(RandomTPConfigurationRequestPacket.class));
    this.redisAdapter.subscribe(new DiscordPacketHandler(this), "rhc_master_controller",
        Collections.singletonList(DiscordRewardVerificationRequestPacket.class));
    this.redisAdapter.subscribe(new ProtectionPacketHandler(this), "rhc_master_controller",
        Collections.singletonList(ProtectionConfigurationRequestPacket.class));
    this.redisAdapter.subscribe(new TradePacketHandler(this), "rhc_master_controller",
        Collections.singletonList(TradeConfigurationRequestPacket.class));
  }

  private void loadConfigurations() {
    final DatabaseConfiguration databaseConfiguration = ConfigurationHelper.load(
        new File("./database.json"), DatabaseConfiguration.class);
    final PlatformConfiguration platformConfiguration = ConfigurationHelper.load(
        new File("./tools.json"), PlatformConfiguration.class,
        configuration -> configuration.specialBossBarWrapper = new SpecialBossBarWrapper());
    databaseConfiguration.canSynchronize = false;
    databaseConfiguration.canReload = false;
    platformConfiguration.autoMessagesSettingsWrapper.parsedBroadcastTime = TimeHelper.timeFromString(
        platformConfiguration.autoMessagesSettingsWrapper.broadcastTime);
    this.configurationFactory.addConfiguration(databaseConfiguration);
    this.configurationFactory.addConfiguration(platformConfiguration);
    this.configurationFactory.addConfiguration(
        ConfigurationHelper.load(new File("./sectors.json"), SectorsConfiguration.class));
    this.configurationFactory.addConfiguration(
        ConfigurationHelper.load(new File("./protection.json"), ProtectionConfiguration.class));
    this.configurationFactory.addConfiguration(
        ConfigurationHelper.load(new File("./auth.json"), AuthConfiguration.class));
    this.configurationFactory.addConfiguration(
        ConfigurationHelper.load(new File("./trade.json"), TradeConfiguration.class));
    this.configurationFactory.addConfiguration(
        ConfigurationHelper.load(new File("./linker-rtp.json"), LinkerRandomTPConfiguration.class));
    this.configurationFactory.addConfiguration(
        ConfigurationHelper.load(new File("./system-rtp.json"), SystemRandomTPConfiguration.class));
    this.configurationFactory.addConfiguration(
        ConfigurationHelper.load(new File("./achievements.json"), AchievementsConfiguration.class));
    this.configurationFactory.addConfiguration(
        ConfigurationHelper.load(new File("./guilds.json"), GuildsConfiguration.class));
    this.configurationFactory.addReloadHandler(SectorsConfiguration.class,
        new SectorsConfigurationReloadHandler(this));
    this.configurationFactory.addReloadHandler(PlatformConfiguration.class,
        new PlatformConfigurationReloadHandler(this));
    this.configurationFactory.addReloadHandler(GuildsConfiguration.class,
        new GuildsConfigurationReloadHandler(this));
    this.configurationFactory.addReloadHandler(ProtectionConfiguration.class,
        OnlySynchronizationReloadHandler.create(ProtectionConfiguration.class));
    this.configurationFactory.addReloadHandler(AuthConfiguration.class,
        OnlySynchronizationReloadHandler.create(AuthConfiguration.class));
    this.configurationFactory.addReloadHandler(TradeConfiguration.class,
        OnlySynchronizationReloadHandler.create(TradeConfiguration.class));
    this.configurationFactory.addReloadHandler(LinkerRandomTPConfiguration.class,
        OnlySynchronizationReloadHandler.create(LinkerRandomTPConfiguration.class));
    this.configurationFactory.addReloadHandler(SystemRandomTPConfiguration.class,
        OnlySynchronizationReloadHandler.create(SystemRandomTPConfiguration.class));
    this.configurationFactory.addReloadHandler(AchievementsConfiguration.class,
        OnlySynchronizationReloadHandler.create(AchievementsConfiguration.class));
  }

  private void loadDatabase() {
    final DatabaseConfiguration databaseConfiguration = this.configurationFactory.findConfiguration(
        DatabaseConfiguration.class);
    this.databaseAdapter = new DatabaseAdapter(databaseConfiguration.databaseWrapper.host,
        databaseConfiguration.databaseWrapper.port, databaseConfiguration.databaseWrapper.password,
        databaseConfiguration.databaseWrapper.user, databaseConfiguration.databaseWrapper.database);
    this.redisAdapter = new RedisAdapter(this, databaseConfiguration.redisWrapper.host,
        databaseConfiguration.redisWrapper.port, databaseConfiguration.redisWrapper.password);
  }

  private void shutdownPlatform() throws SQLException {
    final List<PlatformUser> platformUserList = new ArrayList<>(
        this.platformUserFactory.getUserMap().values());
    final List<Safe> safeList = new ArrayList<>(this.safeFactory.getSafeMap().values());
    final List<Ban> banList = new ArrayList<>(this.banFactory.getBanMap().values());
    this.platformUserFactory.markToShutdown();
    this.platformUserFactory.getUserMap().clear();
    this.banFactory.markToShutdown();
    this.banFactory.getBanMap().clear();
    this.safeFactory.markToShutdown();
    this.safeFactory.getSafeMap().clear();
    if (!platformUserList.isEmpty()) {
      this.platformUserRepository.updateAll(platformUserList);
    }

    if (!safeList.isEmpty()) {
      this.safeRepository.updateAll(safeList);
    }

    if (!banList.isEmpty()) {
      this.banRepository.updateAll(banList);
    }
  }

  private void shutdownGuilds() throws SQLException {
    final List<GuildUser> guildUserList = new ArrayList<>(
        this.guildUserFactory.getUserMap().values());
    final List<Guild> guildList = new ArrayList<>(this.guildFactory.getGuildMap().values());
    this.guildUserFactory.getUserMap().clear();
    this.guildUserFactory.markToShutdown();
    this.guildFactory.getGuildMap().clear();
    this.guildFactory.markToShutdown();
    if (!guildUserList.isEmpty()) {
      this.guildUserRepository.updateAll(guildUserList);
    }

    if (!guildList.isEmpty()) {
      this.guildRepository.updateAll(guildList);
    }
  }

  private void initPlatform() throws SQLException {
    this.platformUserRepository = new PlatformUserRepository(this.databaseAdapter);
    this.platformUserFactory = new PlatformUserFactory(this);
    this.banRepository = new BanRepository(this.databaseAdapter);
    this.banFactory = new BanFactory(this);
    this.safeRepository = new SafeRepository(this.databaseAdapter);
    this.safeFactory = new SafeFactory(this);
    SCHEDULER.scheduleAtFixedRate(new PlatformUserUpdateTask(this), 3L, 3L, TimeUnit.MINUTES);
    SCHEDULER.scheduleAtFixedRate(new BanUpdateTask(this), 3L, 3L, TimeUnit.MINUTES);
    SCHEDULER.scheduleAtFixedRate(new SafeUpdateTask(this), 3L, 3L, TimeUnit.MINUTES);
    SCHEDULER.scheduleAtFixedRate(new SafeModifierUpdateTask(this), 2L, 2L, TimeUnit.SECONDS);
    SCHEDULER.scheduleAtFixedRate(new PlatformCheckSpecialBarTask(this), 1L, 1L, TimeUnit.SECONDS);
    SCHEDULER.scheduleAtFixedRate(new PlatformPickTurboDropPlayersTask(this), 1L, 1L,
        TimeUnit.HOURS);
    this.autoMessageTaskFuture = SCHEDULER.scheduleAtFixedRate(
        new PlatformUserSendAutoMessageTask(this), this.configurationFactory.findConfiguration(
            PlatformConfiguration.class).autoMessagesSettingsWrapper.parsedBroadcastTime,
        this.configurationFactory.findConfiguration(
            PlatformConfiguration.class).autoMessagesSettingsWrapper.parsedBroadcastTime,
        TimeUnit.MILLISECONDS);
  }

  private void initGuilds() throws SQLException {
    final GuildsConfiguration guildsConfiguration = this.configurationFactory.findConfiguration(
        GuildsConfiguration.class);
    this.guildUserRepository = new GuildUserRepository(this.databaseAdapter);
    this.guildUserFactory = new GuildUserFactory(this);
    this.guildGroupFactory = new GuildGroupFactory(guildsConfiguration);
    this.guildRepository = new GuildRepository(this.databaseAdapter);
    this.guildFactory = new GuildFactory(this);
    SCHEDULER.scheduleAtFixedRate(new GuildUserUpdateTask(this), 1L, 1L, TimeUnit.MINUTES);
    SCHEDULER.scheduleAtFixedRate(new GuildUpdateTask(this), 1L, 1L, TimeUnit.MINUTES);
    GuildSchematicCacheHelper.cacheSchematicData(guildsConfiguration);
  }

  private void shutdownAuth() throws SQLException {
    final List<AuthUser> authUserList = new ArrayList<>(this.authUserFactory.getUserMap().values());
    this.authUserFactory.markToShutdown();
    this.authUserFactory.getUserMap().clear();
    if (!authUserList.isEmpty()) {
      this.authUserRepository.updateAll(authUserList);
    }
  }

  private void initAuth() throws SQLException {
    this.authUserRepository = new AuthUserRepository(this.databaseAdapter);
    this.authUserFactory = new AuthUserFactory(this);
    SCHEDULER.scheduleAtFixedRate(new AuthUserUpdateTask(this), 3L, 3L, TimeUnit.MINUTES);
  }
}
