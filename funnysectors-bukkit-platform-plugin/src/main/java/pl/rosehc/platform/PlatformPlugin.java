package pl.rosehc.platform;

import java.util.Arrays;
import java.util.Collections;
import java.util.logging.Level;
import me.vaperion.blade.Blade;
import me.vaperion.blade.bindings.impl.BukkitBindings;
import me.vaperion.blade.container.impl.BukkitCommandContainer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import pl.rosehc.adapter.AdapterPlugin;
import pl.rosehc.adapter.helper.ConfigurationHelper;
import pl.rosehc.adapter.helper.EventCompletionStage;
import pl.rosehc.adapter.helper.TimeHelper;
import pl.rosehc.adapter.plugin.BukkitPlugin;
import pl.rosehc.adapter.redis.callback.Callback;
import pl.rosehc.adapter.redis.callback.CallbackPacket;
import pl.rosehc.controller.packet.DiscordPacketHandler;
import pl.rosehc.controller.packet.PlatformPacketHandler;
import pl.rosehc.controller.packet.configuration.ConfigurationSynchronizePacket;
import pl.rosehc.controller.packet.discord.DiscordRewardGivePacket;
import pl.rosehc.controller.packet.platform.PlatformAlertMessagePacket;
import pl.rosehc.controller.packet.platform.PlatformChatStateChangePacket;
import pl.rosehc.controller.packet.platform.PlatformDropSettingsUpdateTurboDropPacket;
import pl.rosehc.controller.packet.platform.PlatformInitializationRequestPacket;
import pl.rosehc.controller.packet.platform.PlatformInitializationResponsePacket;
import pl.rosehc.controller.packet.platform.PlatformSetFreezeStatePacket;
import pl.rosehc.controller.packet.platform.PlatformSetSlotsPacket;
import pl.rosehc.controller.packet.platform.PlatformSetSpawnPacket;
import pl.rosehc.controller.packet.platform.PlatformSpecialBossBarUpdatePacket;
import pl.rosehc.controller.packet.platform.end.PlatformEndPortalPointCreatePacket;
import pl.rosehc.controller.packet.platform.end.PlatformEndPortalPointDeletePacket;
import pl.rosehc.controller.packet.platform.safe.PlatformSafeContentsModifyPacket;
import pl.rosehc.controller.packet.platform.safe.PlatformSafeCreatePacket;
import pl.rosehc.controller.packet.platform.safe.PlatformSafeDescriptionUpdatePacket;
import pl.rosehc.controller.packet.platform.safe.PlatformSafeLastOpenedTimeUpdatePacket;
import pl.rosehc.controller.packet.platform.safe.PlatformSafeModifierUpdatePacket;
import pl.rosehc.controller.packet.platform.safe.PlatformSafeOwnerUpdatePacket;
import pl.rosehc.controller.packet.platform.safe.request.PlatformSafeDataRequestPacket;
import pl.rosehc.controller.packet.platform.safe.request.PlatformSafeDataResponsePacket;
import pl.rosehc.controller.packet.platform.safe.request.PlatformSafeModificationResponsePacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserAddDepositLimitsPacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserCombatTimeUpdatePacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserCooldownSynchronizePacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserCreatePacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserDisableFirstJoinStatePacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserDiscordRewardStateUpdatePacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserDropSettingsAddDisabledDropPacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserDropSettingsRemoveDisabledDropPacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserGodStateUpdatePacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserIgnoredPlayerUpdatePacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserLastPrivateMessageUpdatePacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserMessagePacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserNicknameUpdatePacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserRankUpdatePacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserReceiveKitPacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserRemoveDepositLimitsPacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserSelectedDiscoEffectTypeUpdatePacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserSetHomePacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserSynchronizeChatSettingsPacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserSynchronizeSomeDropSettingsDataPacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserTeleportRequestUpdatePacket;
import pl.rosehc.controller.packet.platform.user.PlatformUserVanishStateUpdatePacket;
import pl.rosehc.controller.packet.platform.user.PlatformUsersRequestPacket;
import pl.rosehc.controller.packet.platform.user.PlatformUsersResponsePacket;
import pl.rosehc.platform.PlatformConfiguration.SimpleCustomCommandWrapper;
import pl.rosehc.platform.cobblex.CobbleXItemFactory;
import pl.rosehc.platform.command.PlatformCommandBindings;
import pl.rosehc.platform.command.player.BlocksCommand;
import pl.rosehc.platform.command.player.ChannelCommand;
import pl.rosehc.platform.command.player.ChatSettingsCommand;
import pl.rosehc.platform.command.player.CobbleXCommand;
import pl.rosehc.platform.command.player.DepositCommand;
import pl.rosehc.platform.command.player.DiscoCommand;
import pl.rosehc.platform.command.player.DropCommand;
import pl.rosehc.platform.command.player.EnderChestCommand;
import pl.rosehc.platform.command.player.HomeCommand;
import pl.rosehc.platform.command.player.IgnoreCommand;
import pl.rosehc.platform.command.player.KitCommand;
import pl.rosehc.platform.command.player.LevelCommand;
import pl.rosehc.platform.command.player.PrivateMessageCommand;
import pl.rosehc.platform.command.player.RepairCommand;
import pl.rosehc.platform.command.player.RepairPickaxeCommand;
import pl.rosehc.platform.command.player.SafeDescriptionCommand;
import pl.rosehc.platform.command.player.SimpleCustomCommand;
import pl.rosehc.platform.command.player.SpawnCommand;
import pl.rosehc.platform.command.player.TeleportRequestCommand;
import pl.rosehc.platform.command.player.TrashCommand;
import pl.rosehc.platform.command.player.WorkbenchCommand;
import pl.rosehc.platform.command.staff.AdminItemsCommand;
import pl.rosehc.platform.command.staff.AlertCommand;
import pl.rosehc.platform.command.staff.BossBarCommand;
import pl.rosehc.platform.command.staff.ChatCommand;
import pl.rosehc.platform.command.staff.ClearCommand;
import pl.rosehc.platform.command.staff.EndPortalPointCommand;
import pl.rosehc.platform.command.staff.FlightCommand;
import pl.rosehc.platform.command.staff.FreezeCommand;
import pl.rosehc.platform.command.staff.GameModeCommand;
import pl.rosehc.platform.command.staff.GodCommand;
import pl.rosehc.platform.command.staff.HealCommand;
import pl.rosehc.platform.command.staff.MagicCaseCommand;
import pl.rosehc.platform.command.staff.OpenInventoryCommand;
import pl.rosehc.platform.command.staff.SectorCommand;
import pl.rosehc.platform.command.staff.SetRankCommand;
import pl.rosehc.platform.command.staff.SetSpawnCommand;
import pl.rosehc.platform.command.staff.SpeedCommand;
import pl.rosehc.platform.command.staff.TeleportCommand;
import pl.rosehc.platform.command.staff.TeleportHereCommand;
import pl.rosehc.platform.command.staff.TurboDropCommand;
import pl.rosehc.platform.command.staff.VanishCommand;
import pl.rosehc.platform.crafting.CraftingRecipeFactory;
import pl.rosehc.platform.deposit.DepositItemCheckTask;
import pl.rosehc.platform.disco.task.DiscoEffectUpdateTask;
import pl.rosehc.platform.disco.task.DiscoUserUpdateTask;
import pl.rosehc.platform.drop.DropFactory;
import pl.rosehc.platform.end.EndPointFactory;
import pl.rosehc.platform.end.EndPointUpdateTask;
import pl.rosehc.platform.end.EndShockwaveTask;
import pl.rosehc.platform.end.session.EndPortalPointEditingSessionFactory;
import pl.rosehc.platform.hologram.HologramFactory;
import pl.rosehc.platform.kit.KitFactory;
import pl.rosehc.platform.listener.block.BlockBreakListener;
import pl.rosehc.platform.listener.block.BlockPlaceListener;
import pl.rosehc.platform.listener.entity.EntityDamageByEntityListener;
import pl.rosehc.platform.listener.entity.EntityDamageListener;
import pl.rosehc.platform.listener.inventory.InventoryClickListener;
import pl.rosehc.platform.listener.inventory.InventoryCraftItemListener;
import pl.rosehc.platform.listener.inventory.InventoryOpenListener;
import pl.rosehc.platform.listener.player.AsyncPlayerChatListener;
import pl.rosehc.platform.listener.player.PlayerBucketEmptyListener;
import pl.rosehc.platform.listener.player.PlayerBucketFillListener;
import pl.rosehc.platform.listener.player.PlayerCommandPreprocessListener;
import pl.rosehc.platform.listener.player.PlayerDeathListener;
import pl.rosehc.platform.listener.player.PlayerEnchantItemListener;
import pl.rosehc.platform.listener.player.PlayerEndWaterFromToListener;
import pl.rosehc.platform.listener.player.PlayerInteractListener;
import pl.rosehc.platform.listener.player.PlayerItemConsumeListener;
import pl.rosehc.platform.listener.player.PlayerJoinListener;
import pl.rosehc.platform.listener.player.PlayerNameTagListener;
import pl.rosehc.platform.listener.player.PlayerPortalListener;
import pl.rosehc.platform.listener.player.PlayerProjectileLaunchListener;
import pl.rosehc.platform.listener.player.PlayerQuitListener;
import pl.rosehc.platform.listener.player.PlayerRespawnListener;
import pl.rosehc.platform.listener.player.PlayerTeleportListener;
import pl.rosehc.platform.listener.sector.SectorConnectingListener;
import pl.rosehc.platform.listener.sector.SectorFreezeProtectionListener;
import pl.rosehc.platform.magiccase.MagicCaseFactory;
import pl.rosehc.platform.packet.PlayerPacketHandler;
import pl.rosehc.platform.packet.player.PlayerClearInventoryPacket;
import pl.rosehc.platform.packet.player.PlayerGiveMagicCasePacket;
import pl.rosehc.platform.packet.player.PlayerHealPacket;
import pl.rosehc.platform.packet.player.PlayerLocationRequestPacket;
import pl.rosehc.platform.packet.player.PlayerLocationResponsePacket;
import pl.rosehc.platform.packet.player.PlayerSelfTeleportPacket;
import pl.rosehc.platform.rank.RankFactory;
import pl.rosehc.platform.safe.SafeFactory;
import pl.rosehc.platform.scoreboard.EndScoreboardProfile;
import pl.rosehc.platform.scoreboard.SpawnScoreboardProfile;
import pl.rosehc.platform.timer.TimerTaskFactory;
import pl.rosehc.platform.user.PlatformUserFactory;
import pl.rosehc.platform.user.PlatformUserSectorSpoofHelper;
import pl.rosehc.platform.user.subdata.PlatformUserDropSettings;
import pl.rosehc.platform.user.task.PlatformUserCheckGoldenHeadTask;
import pl.rosehc.platform.user.task.PlatformUserCombatUpdateTask;
import pl.rosehc.platform.user.task.PlatformUserFreezeInformationTask;
import pl.rosehc.platform.user.task.PlatformUserRemoveDiamondArmorTask;
import pl.rosehc.platform.user.task.PlatformUserSpecialBarUpdateTask;
import pl.rosehc.platform.user.task.PlatformUserSynchronizeSomeDropSettingsDataTask;
import pl.rosehc.platform.user.task.PlatformUserTurboDropUpdateTask;
import pl.rosehc.platform.user.task.PlatformUserVanishUpdateTask;
import pl.rosehc.platform.vanishingblock.VanishingBlockFactory;
import pl.rosehc.platform.vanishingblock.VanishingBlockResetTask;
import pl.rosehc.sectors.SectorsPlugin;
import pl.rosehc.sectors.sector.Sector;
import pl.rosehc.sectors.sector.SectorInitializationHook;
import pl.rosehc.sectors.sector.SectorType;

public final class PlatformPlugin extends BukkitPlugin implements SectorInitializationHook {

  private static PlatformPlugin instance;
  private PlatformConfiguration platformConfiguration;
  private RankFactory rankFactory;
  private MagicCaseFactory magicCaseFactory;
  private CobbleXItemFactory cobbleXItemFactory;
  private DropFactory dropFactory;
  private KitFactory kitFactory;
  private CraftingRecipeFactory craftingRecipeFactory;
  private PlatformUserFactory platformUserFactory;
  private SafeFactory safeFactory;
  private TimerTaskFactory timerTaskFactory;
  private EndPointFactory endPointFactory;
  private EndPortalPointEditingSessionFactory endPortalPointEditingSessionFactory;
  private VanishingBlockFactory vanishingBlockFactory;
  private HologramFactory hologramFactory;

  public static PlatformPlugin getInstance() {
    return instance;
  }

  @Override
  public void onLoad() {
    instance = this;
    SectorsPlugin.getInstance().registerHook(this);
  }

  @Override
  public void onInitialize(final EventCompletionStage completionStage, final Sector sector,
      final boolean success) {
    if (success) {
      final Object waiter = new Object();
      completionStage.addWaiter(waiter);
      this.getRedisAdapter().subscribe(new PlatformPacketHandler(this), Arrays.asList(
          "rhc_platform_" + sector.getName(),
          "rhc_platform",
          "rhc_global"
      ), Arrays.asList(
          PlatformInitializationResponsePacket.class,
          PlatformUsersResponsePacket.class,
          PlatformUserCreatePacket.class,
          PlatformUserCooldownSynchronizePacket.class,
          PlatformUserNicknameUpdatePacket.class,
          PlatformUserRankUpdatePacket.class,
          PlatformUserAddDepositLimitsPacket.class,
          PlatformUserRemoveDepositLimitsPacket.class,
          PlatformUserReceiveKitPacket.class,
          PlatformUserTeleportRequestUpdatePacket.class,
          PlatformUserLastPrivateMessageUpdatePacket.class,
          PlatformUserCombatTimeUpdatePacket.class,
          PlatformUserVanishStateUpdatePacket.class,
          PlatformUserGodStateUpdatePacket.class,
          PlatformUserDisableFirstJoinStatePacket.class,
          PlatformUserSynchronizeSomeDropSettingsDataPacket.class,
          PlatformUserDropSettingsAddDisabledDropPacket.class,
          PlatformUserDropSettingsRemoveDisabledDropPacket.class,
          PlatformUserSynchronizeChatSettingsPacket.class,
          PlatformUserIgnoredPlayerUpdatePacket.class,
          PlatformUserSelectedDiscoEffectTypeUpdatePacket.class,
          PlatformUserMessagePacket.class,
          PlatformUserSetHomePacket.class,
          PlatformUserDiscordRewardStateUpdatePacket.class,
          PlatformSafeDataResponsePacket.class,
          PlatformSafeModificationResponsePacket.class,
          PlatformSafeContentsModifyPacket.class,
          PlatformSafeCreatePacket.class,
          PlatformSafeDescriptionUpdatePacket.class,
          PlatformSafeLastOpenedTimeUpdatePacket.class,
          PlatformSafeOwnerUpdatePacket.class,
          PlatformSafeModifierUpdatePacket.class,
          PlatformSetSlotsPacket.class,
          PlatformSetSpawnPacket.class,
          PlatformSetFreezeStatePacket.class,
          PlatformSpecialBossBarUpdatePacket.class,
          PlatformEndPortalPointCreatePacket.class,
          PlatformEndPortalPointDeletePacket.class,
          PlatformChatStateChangePacket.class,
          PlatformDropSettingsUpdateTurboDropPacket.class,
          PlatformAlertMessagePacket.class,
          ConfigurationSynchronizePacket.class
      ));
      this.getRedisAdapter()
          .sendPacket(new PlatformInitializationRequestPacket(sector.getName()), new Callback() {

            @Override
            public void done(final CallbackPacket packet) {
              final PlatformInitializationResponsePacket initializationResponsePacket = (PlatformInitializationResponsePacket) packet;
              executeOrDisable(() -> {
                platformConfiguration = ConfigurationHelper.deserializeConfiguration(
                    initializationResponsePacket.getPlatformConfigurationData(),
                    PlatformConfiguration.class);
                platformConfiguration.parsedCombatTime = TimeHelper.timeFromString(
                    platformConfiguration.combatTime);
                platformConfiguration.antiGriefSettingsWrapper.parsedRemovalTime = TimeHelper.timeFromString(
                    platformConfiguration.antiGriefSettingsWrapper.removalTime);
                rankFactory = new RankFactory(platformConfiguration);
                magicCaseFactory = new MagicCaseFactory(platformConfiguration);
                cobbleXItemFactory = new CobbleXItemFactory(platformConfiguration);
                dropFactory = new DropFactory(platformConfiguration);
                kitFactory = new KitFactory(platformConfiguration);
                craftingRecipeFactory = new CraftingRecipeFactory(platformConfiguration);
                completionStage.addWaiter(waiter);
                completionStage.removeWaiter(waiter);
                getRedisAdapter().sendPacket(new PlatformUsersRequestPacket(sector.getName()),
                    new Callback() {

                      @Override
                      public void done(final CallbackPacket packet) {
                        final PlatformUsersResponsePacket usersResponsePacket = (PlatformUsersResponsePacket) packet;
                        executeOrDisable(() -> {
                          platformUserFactory = new PlatformUserFactory(
                              usersResponsePacket.getUsers());
                          timerTaskFactory = new TimerTaskFactory();
                          vanishingBlockFactory = new VanishingBlockFactory();
                          hologramFactory = new HologramFactory();
                          completionStage.addWaiter(waiter);
                          completionStage.removeWaiter(waiter);
                          getRedisAdapter().sendPacket(
                              new PlatformSafeDataRequestPacket(sector.getName()), new Callback() {

                                @Override
                                public void done(final CallbackPacket packet) {
                                  final PlatformSafeDataResponsePacket safeDataResponsePacket = (PlatformSafeDataResponsePacket) packet;
                                  executeOrDisable(() -> {
                                    safeFactory = new SafeFactory(
                                        safeDataResponsePacket.getSafes());
                                    PlatformPlugin.this.getRedisAdapter()
                                        .subscribe(new PlayerPacketHandler(PlatformPlugin.this),
                                            Arrays.asList(
                                                "rhc_platform_" + sector.getName(),
                                                "rhc_platform"
                                            ), Arrays.asList(
                                                PlayerLocationRequestPacket.class,
                                                PlayerLocationResponsePacket.class,
                                                PlayerSelfTeleportPacket.class,
                                                PlayerGiveMagicCasePacket.class,
                                                PlayerClearInventoryPacket.class,
                                                PlayerHealPacket.class
                                            ));
                                    PlatformPlugin.this.getRedisAdapter()
                                        .subscribe(new DiscordPacketHandler(PlatformPlugin.this),
                                            "rhc_platform_" + sector.getName(),
                                            Collections.singletonList(
                                                DiscordRewardGivePacket.class));
                                    PlatformPlugin.this.registerListeners(
                                        new PlayerJoinListener(PlatformPlugin.this),
                                        new PlayerQuitListener(PlatformPlugin.this),
                                        new PlayerInteractListener(PlatformPlugin.this),
                                        new PlayerDeathListener(PlatformPlugin.this),
                                        new PlayerRespawnListener(PlatformPlugin.this),
                                        new PlayerTeleportListener(PlatformPlugin.this),
                                        new PlayerItemConsumeListener(PlatformPlugin.this),
                                        new PlayerEnchantItemListener(PlatformPlugin.this),
                                        new PlayerProjectileLaunchListener(PlatformPlugin.this),
                                        new PlayerCommandPreprocessListener(PlatformPlugin.this),
                                        new PlayerBucketEmptyListener(PlatformPlugin.this),
                                        new PlayerBucketFillListener(PlatformPlugin.this),
                                        new PlayerEndWaterFromToListener(PlatformPlugin.this),
                                        new PlayerPortalListener(PlatformPlugin.this),
                                        new SectorConnectingListener(PlatformPlugin.this),
                                        new SectorFreezeProtectionListener(PlatformPlugin.this),
                                        new PlayerNameTagListener(PlatformPlugin.this),
                                        new EntityDamageByEntityListener(PlatformPlugin.this),
                                        new EntityDamageListener(PlatformPlugin.this),
                                        new InventoryOpenListener(PlatformPlugin.this),
                                        new InventoryClickListener(PlatformPlugin.this),
                                        new InventoryCraftItemListener(PlatformPlugin.this),
                                        new BlockBreakListener(PlatformPlugin.this),
                                        new BlockPlaceListener(PlatformPlugin.this),
                                        new AsyncPlayerChatListener(PlatformPlugin.this));
                                    final Blade blade = Blade.of().fallbackPrefix("platform")
                                        .defaultPermissionMessage(ChatColor.RED + "Brak uprawnień.")
                                        .overrideCommands(true).executionTimeWarningThreshold(500L)
                                        .containerCreator(BukkitCommandContainer.CREATOR)
                                        .binding(new PlatformCommandBindings(PlatformPlugin.this))
                                        .binding(new BukkitBindings()).asyncExecutor(
                                            runnable -> getServer().getScheduler()
                                                .runTaskAsynchronously(PlatformPlugin.this,
                                                    runnable)).build();

                                    blade.register(new ChatCommand(PlatformPlugin.this));
                                    blade.register(new MagicCaseCommand(PlatformPlugin.this));
                                    blade.register(new TeleportCommand(PlatformPlugin.this));
                                    blade.register(new TeleportHereCommand(PlatformPlugin.this));
                                    blade.register(new SetRankCommand(PlatformPlugin.this));
                                    blade.register(new SectorCommand(PlatformPlugin.this));
                                    blade.register(new TurboDropCommand(PlatformPlugin.this));
                                    blade.register(new FreezeCommand(PlatformPlugin.this));
                                    blade.register(new BossBarCommand(PlatformPlugin.this));
                                    blade.register(new FlightCommand(PlatformPlugin.this));
                                    blade.register(new SpeedCommand(PlatformPlugin.this));
                                    blade.register(new AlertCommand(PlatformPlugin.this));
                                    blade.register(new ClearCommand(PlatformPlugin.this));
                                    blade.register(new HealCommand(PlatformPlugin.this));
                                    blade.register(new GameModeCommand(PlatformPlugin.this));
                                    blade.register(new VanishCommand(PlatformPlugin.this));
                                    blade.register(new GodCommand(PlatformPlugin.this));
                                    blade.register(new EnderChestCommand(PlatformPlugin.this));
                                    blade.register(new WorkbenchCommand(PlatformPlugin.this));
                                    blade.register(new SpawnCommand(PlatformPlugin.this));
                                    blade.register(new RepairCommand(PlatformPlugin.this));
                                    blade.register(new RepairPickaxeCommand(PlatformPlugin.this));
                                    blade.register(new BlocksCommand(PlatformPlugin.this));
                                    blade.register(new SetSpawnCommand(PlatformPlugin.this));
                                    blade.register(new TeleportRequestCommand(PlatformPlugin.this));
                                    blade.register(new PrivateMessageCommand(PlatformPlugin.this));
                                    blade.register(new SafeDescriptionCommand(PlatformPlugin.this));
                                    blade.register(new CobbleXCommand(PlatformPlugin.this));
                                    blade.register(new LevelCommand(PlatformPlugin.this));
                                    blade.register(new IgnoreCommand(PlatformPlugin.this));
                                    blade.register(new OpenInventoryCommand());
                                    blade.register(new AdminItemsCommand());
                                    blade.register(new KitCommand());
                                    blade.register(new DepositCommand());
                                    blade.register(new HomeCommand());
                                    blade.register(new DropCommand());
                                    blade.register(new ChatSettingsCommand());
                                    blade.register(new DiscoCommand());
                                    for (final SimpleCustomCommandWrapper wrapper : platformConfiguration.simpleCustomCommandWrapperList) {
                                      final SimpleCustomCommand command = SimpleCustomCommand.compile(
                                          wrapper);
                                      blade.register(command);
                                    }

                                    if (sector.getType().equals(SectorType.END)) {
                                      endPointFactory = new EndPointFactory(PlatformPlugin.this);
                                      endPortalPointEditingSessionFactory = new EndPortalPointEditingSessionFactory();
                                      AdapterPlugin.getInstance().getScoreboardFactory()
                                          .setSelectedScoreboardProfile(
                                              new EndScoreboardProfile(PlatformPlugin.this));
                                      blade.register(
                                          new EndPortalPointCommand(PlatformPlugin.this));
                                      new EndPointUpdateTask(PlatformPlugin.this);
                                      new EndShockwaveTask(PlatformPlugin.this);
                                    } else if (sector.getType().equals(SectorType.SPAWN)) {
                                      AdapterPlugin.getInstance().getScoreboardFactory()
                                          .setSelectedScoreboardProfile(
                                              new SpawnScoreboardProfile(PlatformPlugin.this));
                                      blade.register(new ChannelCommand());
                                      blade.register(new TrashCommand());
                                    }

                                    new PlatformUserSynchronizeSomeDropSettingsDataTask(
                                        PlatformPlugin.this);
                                    new PlatformUserCombatUpdateTask(PlatformPlugin.this);
                                    new PlatformUserTurboDropUpdateTask(PlatformPlugin.this);
                                    new PlatformUserVanishUpdateTask(PlatformPlugin.this);
                                    new PlatformUserFreezeInformationTask(PlatformPlugin.this);
                                    new PlatformUserSpecialBarUpdateTask(PlatformPlugin.this);
                                    new PlatformUserCheckGoldenHeadTask(PlatformPlugin.this);
                                    new DiscoEffectUpdateTask(PlatformPlugin.this);
                                    new DiscoUserUpdateTask(PlatformPlugin.this);
                                    new VanishingBlockResetTask(PlatformPlugin.this);
                                    new PlatformUserRemoveDiamondArmorTask(PlatformPlugin.this);
                                    if (!sector.getType().equals(SectorType.SPAWN)) {
                                      new DepositItemCheckTask(PlatformPlugin.this);
                                    }

                                    completionStage.removeWaiter(waiter);
                                  });
                                }

                                @Override
                                public void error(final String ignored) {
                                }
                              }, "rhc_master_controller");
                        });
                      }

                      @Override
                      public void error(final String ignored) {
                      }
                    }, "rhc_master_controller");
              });
            }

            @Override
            public void error(final String ignored) {
            }
          }, "rhc_master_controller");
    }
  }

  @Override
  public void onDisable() {
    if (SectorsPlugin.getInstance().isLoaded()) {
      if (SectorsPlugin.getInstance().getSectorFactory().getCurrentSector().getType()
          .equals(SectorType.END)) {
        PlatformUserSectorSpoofHelper.spoofAllSectors();
      }

      this.vanishingBlockFactory.reset(true);
      for (final Player player : this.getServer().getOnlinePlayers()) {
        this.platformUserFactory.findUserByUniqueId(player.getUniqueId()).ifPresent(user -> {
          final PlatformUserDropSettings dropSettings = user.getDropSettings();
          this.getRedisAdapter().sendPacket(
              new PlatformUserSynchronizeSomeDropSettingsDataPacket(user.getUniqueId(),
                  dropSettings.getTurboDropMultiplier(), dropSettings.isCobbleStone(),
                  dropSettings.getTurboDropTime(), dropSettings.getCurrentXP(),
                  dropSettings.getNeededXP(), dropSettings.getLevel()), "rhc_master_controller",
              "rhc_platform");
        });
      }
    }
  }

  public PlatformConfiguration getPlatformConfiguration() {
    return this.platformConfiguration;
  }

  public void setPlatformConfiguration(PlatformConfiguration platformConfiguration) {
    this.platformConfiguration = platformConfiguration;
  }

  public RankFactory getRankFactory() {
    return this.rankFactory;
  }

  public MagicCaseFactory getMagicCaseFactory() {
    return this.magicCaseFactory;
  }

  public CobbleXItemFactory getCobbleXItemFactory() {
    return this.cobbleXItemFactory;
  }

  public DropFactory getDropFactory() {
    return this.dropFactory;
  }

  public KitFactory getKitFactory() {
    return this.kitFactory;
  }

  public CraftingRecipeFactory getCraftingRecipeFactory() {
    return this.craftingRecipeFactory;
  }

  public PlatformUserFactory getPlatformUserFactory() {
    return this.platformUserFactory;
  }

  public SafeFactory getSafeFactory() {
    return this.safeFactory;
  }

  public TimerTaskFactory getTimerTaskFactory() {
    return this.timerTaskFactory;
  }

  public EndPointFactory getEndPointFactory() {
    return this.endPointFactory;
  }

  public EndPortalPointEditingSessionFactory getEndPortalPointEditingSessionFactory() {
    return this.endPortalPointEditingSessionFactory;
  }

  public VanishingBlockFactory getVanishingBlockFactory() {
    return this.vanishingBlockFactory;
  }

  public HologramFactory getHologramFactory() {
    return this.hologramFactory;
  }

  private void executeOrDisable(final SafeRunnable action) {
    try {
      action.run();
    } catch (final Throwable ex) {
      this.getLogger().log(Level.SEVERE, "Plugin nie mógł zostać poprawnie załadowany!", ex);
      this.getServer().getPluginManager().disablePlugin(this);
    }
  }

  private interface SafeRunnable {

    void run() throws Exception;
  }
}
