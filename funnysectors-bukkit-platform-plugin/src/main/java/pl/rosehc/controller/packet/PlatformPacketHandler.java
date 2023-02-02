package pl.rosehc.controller.packet;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.adapter.helper.ConfigurationHelper;
import pl.rosehc.adapter.helper.SerializeHelper;
import pl.rosehc.adapter.helper.TimeHelper;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.bossbar.BossBarBuilder;
import pl.rosehc.bossbar.BossBarPlugin;
import pl.rosehc.bossbar.user.UserBar;
import pl.rosehc.bossbar.user.UserBarConstants;
import pl.rosehc.bossbar.user.UserBarType;
import pl.rosehc.controller.packet.configuration.ConfigurationSynchronizePacket;
import pl.rosehc.controller.packet.platform.PlatformAlertMessagePacket;
import pl.rosehc.controller.packet.platform.PlatformChatStateChangePacket;
import pl.rosehc.controller.packet.platform.PlatformDropSettingsUpdateTurboDropPacket;
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
import pl.rosehc.platform.PlatformConfiguration;
import pl.rosehc.platform.PlatformConfiguration.DropSettingsWrapper.DropWrapper;
import pl.rosehc.platform.PlatformConfiguration.SpecialBossBarWrapper;
import pl.rosehc.platform.PlatformPlugin;
import pl.rosehc.platform.cobblex.CobbleXItem;
import pl.rosehc.platform.cobblex.CobbleXItemFactory;
import pl.rosehc.platform.crafting.CraftingRecipe;
import pl.rosehc.platform.crafting.CraftingRecipeFactory;
import pl.rosehc.platform.disco.DiscoEffectType;
import pl.rosehc.platform.drop.Drop;
import pl.rosehc.platform.kit.Kit;
import pl.rosehc.platform.kit.KitFactory;
import pl.rosehc.platform.rank.Rank;
import pl.rosehc.platform.rank.RankEntry;
import pl.rosehc.platform.rank.RankFactory;
import pl.rosehc.platform.safe.Safe;
import pl.rosehc.platform.user.PlatformUser;
import pl.rosehc.platform.user.PlatformUserFreezeEntityHelper;
import pl.rosehc.sectors.SectorsPlugin;

public final class PlatformPacketHandler implements PacketHandler,
    ConfigurationSynchronizePacketHandler {

  private final PlatformPlugin plugin;

  public PlatformPacketHandler(final PlatformPlugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public void handle(final ConfigurationSynchronizePacket packet) {
    if (SectorsPlugin.getInstance().isLoaded() && packet.getConfigurationName()
        .equals("pl.rosehc.controller.configuration.impl.configuration.PlatformConfiguration")) {
      final PlatformConfiguration lastConfiguration = this.plugin.getPlatformConfiguration();
      final PlatformConfiguration newConfiguration = ConfigurationHelper.deserializeConfiguration(
          packet.getSerializedConfiguration(), PlatformConfiguration.class);
      final RankFactory rankFactory = this.plugin.getRankFactory();
      newConfiguration.parsedCombatTime = TimeHelper.timeFromString(newConfiguration.combatTime);
      this.plugin.setPlatformConfiguration(newConfiguration);
      if (Objects.nonNull(rankFactory) && Objects.nonNull(this.plugin.getPlatformUserFactory())) {
        rankFactory.getRankMap().clear();
        rankFactory.getRankMap().putAll(newConfiguration.rankList.stream().map(
            rankWrapper -> new Rank(rankWrapper.name, rankWrapper.chatPrefix,
                rankWrapper.chatSuffix, rankWrapper.nameTagPrefix, rankWrapper.nameTagSuffix,
                rankWrapper.permissions, rankWrapper.priority, rankWrapper.defaultRank)).collect(
            Collectors.toConcurrentMap(rank -> rank.getName().toLowerCase(), rank -> rank)));
        rankFactory.updateDefaultRank();
        for (final PlatformUser user : this.plugin.getPlatformUserFactory().getUserMap().values()) {
          final RankEntry rank = user.getRank();
          final boolean previousRankNotFound =
              Objects.nonNull(rank.getPreviousRank()) && !rankFactory.getRankMap()
                  .containsKey(rank.getPreviousRank().getName().toLowerCase());
          if (previousRankNotFound || !rankFactory.getRankMap()
              .containsKey(rank.getCurrentRank().getNameTagPrefix().toLowerCase())) {
            final RankEntry newRank = RankEntry.create(
                previousRankNotFound ? rankFactory.getDefaultRank() : rank.getPreviousRank(),
                !rankFactory.getRankMap().containsKey(rank.getCurrentRank().getName().toLowerCase())
                    ? rankFactory.getDefaultRank() : rank.getCurrentRank(),
                !previousRankNotFound ? rank.getExpirationTime() : 0L);
            user.setRank(newRank);
            user.reloadPermissions();
          }
        }
      }

      final Map<String, Drop> dropMap = newConfiguration.dropSettingsWrapper.dropWrapperList.stream()
          .map(DropWrapper::asDrop)
          .collect(Collectors.toMap(drop -> drop.getName().toLowerCase(), drop -> drop));
      this.plugin.getDropFactory().getDropMap().clear();
      this.plugin.getDropFactory().getDropMap().putAll(dropMap);
      for (final PlatformUser user : this.plugin.getPlatformUserFactory().getUserMap().values()) {
        final Set<Drop> disabledDropSet = new HashSet<>();
        for (final Drop previousDrop : new HashSet<>(user.getDropSettings().getDisabledDropSet())) {
          final Drop newDrop = dropMap.get(previousDrop.getName().toLowerCase());
          if (Objects.nonNull(newDrop)) {
            disabledDropSet.add(newDrop);
          }
        }

        user.getDropSettings().getDisabledDropSet().clear();
        user.getDropSettings().getDisabledDropSet().addAll(disabledDropSet);
      }

      final KitFactory kitFactory = this.plugin.getKitFactory();
      if (Objects.nonNull(kitFactory)) {
        kitFactory.getKitMap().clear();
        kitFactory.getKitMap().putAll(newConfiguration.kitWrapperList.stream().map(
                wrapper -> new Kit(wrapper.name, wrapper.permission, wrapper.items.stream().map(
                        kitItemWrapper -> new SimpleEntry<>(kitItemWrapper.id,
                            kitItemWrapper.asItemStack()))
                    .collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue)),
                    TimeHelper.timeFromString(wrapper.time)))
            .collect(Collectors.toConcurrentMap(kit -> kit.getName().toLowerCase(), kit -> kit)));
      }

      final CraftingRecipeFactory craftingRecipeFactory = this.plugin.getCraftingRecipeFactory();
      if (Objects.nonNull(craftingRecipeFactory)) {
        craftingRecipeFactory.getRecipeSet().clear();
        craftingRecipeFactory.getRecipeSet().addAll(
            newConfiguration.customItemsWrapper.customCraftingWrapperList.stream().map(
                    wrapper -> new CraftingRecipe(wrapper.resultWrapper.asItemStack(),
                        wrapper.ingredientWrappersMap.entrySet().stream().map(
                                entry -> new SimpleEntry<>(entry.getKey(), entry.getValue().asItemStack()))
                            .collect(Collectors.toMap(Entry::getKey, Entry::getValue))))
                .collect(Collectors.toSet()));
        craftingRecipeFactory.registerRecipes();
      }

      final CobbleXItemFactory cobbleXItemFactory = this.plugin.getCobbleXItemFactory();
      if (Objects.nonNull(cobbleXItemFactory)) {
        cobbleXItemFactory.executeLocked(cobbleXItemList -> {
          cobbleXItemList.clear();
          cobbleXItemList.addAll(
              newConfiguration.cobbleXItemWrappers.stream().map(CobbleXItem::create)
                  .collect(Collectors.toList()));
        });
      }

      final SpecialBossBarWrapper specialBossBarWrapper = newConfiguration.specialBossBarWrapper;
      if (specialBossBarWrapper != null) {
        final long delta = specialBossBarWrapper.expiryTime - System.currentTimeMillis();
        final float progress = specialBossBarWrapper.expiryTime != 0L ? Math.min(
            TimeUnit.MILLISECONDS.toSeconds(delta) / specialBossBarWrapper.expiryMaxBars, 1F) : 1F;
        for (final Player player : this.plugin.getServer().getOnlinePlayers()) {
          final UserBar userBar = BossBarPlugin.getInstance().getUserBarFactory()
              .getUserBar(player);
          if (!userBar.hasBossBar(UserBarType.SPECIAL_BAR)) {
            userBar.addBossBar(UserBarType.SPECIAL_BAR,
                BossBarBuilder.add(UserBarConstants.SPECIAL_BAR_UUID)
                    .color(specialBossBarWrapper.barColorWrapper.toOriginal())
                    .style(specialBossBarWrapper.barStyleWrapper.toOriginal()).progress(progress)
                    .title(TextComponent.fromLegacyText(ChatHelper.colored(
                        specialBossBarWrapper.expiryTime != 0L
                            ? specialBossBarWrapper.title.replace("{TIME}",
                            TimeHelper.timeToString(delta)) : specialBossBarWrapper.title))));
          } else {
            userBar.updateBossBar(UserBarType.SPECIAL_BAR, ChatHelper.colored(
                    specialBossBarWrapper.expiryTime != 0L ? specialBossBarWrapper.title.replace(
                        "{TIME}", TimeHelper.timeToString(delta)) : specialBossBarWrapper.title),
                progress, specialBossBarWrapper.barColorWrapper.toOriginal(),
                specialBossBarWrapper.barStyleWrapper.toOriginal());
          }
        }
      } else {
        for (final Player player : this.plugin.getServer().getOnlinePlayers()) {
          BossBarPlugin.getInstance().getUserBarFactory().findUserBar(player)
              .ifPresent(userBar -> userBar.removeBossBar(UserBarType.SPECIAL_BAR));
        }
      }

      final boolean lastServerFreezeState = lastConfiguration.serverFreezeState;
      final boolean serverFreezeState = newConfiguration.serverFreezeState;
      if (serverFreezeState && !lastServerFreezeState) {
        this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
          for (final Player player : this.plugin.getServer().getOnlinePlayers()) {
            PlatformUserFreezeEntityHelper.spawnAndMountEntity(player);
          }
        });
      } else if (!serverFreezeState && lastServerFreezeState) {
        this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
          for (final Player player : this.plugin.getServer().getOnlinePlayers()) {
            PlatformUserFreezeEntityHelper.removeAndDisMountEntity(player);
          }
        });
      }

      newConfiguration.antiGriefSettingsWrapper.parsedRemovalTime = TimeHelper.timeFromString(
          newConfiguration.antiGriefSettingsWrapper.removalTime);
    }
  }

  public void handle(final PlatformSpecialBossBarUpdatePacket packet) {
    if (SectorsPlugin.getInstance().isLoaded()) {
      final SpecialBossBarWrapper specialBossBarWrapper = packet.getSpecialBossBarWrapper();
      this.plugin.getPlatformConfiguration().specialBossBarWrapper = specialBossBarWrapper;
      if (specialBossBarWrapper != null) {
        final long delta = specialBossBarWrapper.expiryTime - System.currentTimeMillis();
        final float progress = specialBossBarWrapper.expiryTime != 0L ? Math.min(
            TimeUnit.MILLISECONDS.toSeconds(delta) / specialBossBarWrapper.expiryMaxBars, 1F) : 1F;
        for (final Player player : this.plugin.getServer().getOnlinePlayers()) {
          final UserBar userBar = BossBarPlugin.getInstance().getUserBarFactory()
              .getUserBar(player);
          if (!userBar.hasBossBar(UserBarType.SPECIAL_BAR)) {
            userBar.addBossBar(UserBarType.SPECIAL_BAR,
                BossBarBuilder.add(UserBarConstants.SPECIAL_BAR_UUID)
                    .color(specialBossBarWrapper.barColorWrapper.toOriginal())
                    .style(specialBossBarWrapper.barStyleWrapper.toOriginal()).progress(progress)
                    .title(TextComponent.fromLegacyText(ChatHelper.colored(
                        specialBossBarWrapper.expiryTime != 0L
                            ? specialBossBarWrapper.title.replace("{TIME}",
                            TimeHelper.timeToString(delta)) : specialBossBarWrapper.title))));
          } else {
            userBar.updateBossBar(UserBarType.SPECIAL_BAR, ChatHelper.colored(
                    specialBossBarWrapper.expiryTime != 0L ? specialBossBarWrapper.title.replace(
                        "{TIME}", TimeHelper.timeToString(delta)) : specialBossBarWrapper.title),
                progress, specialBossBarWrapper.barColorWrapper.toOriginal(),
                specialBossBarWrapper.barStyleWrapper.toOriginal());
          }
        }
      } else {
        for (final Player player : this.plugin.getServer().getOnlinePlayers()) {
          BossBarPlugin.getInstance().getUserBarFactory().findUserBar(player)
              .ifPresent(userBar -> userBar.removeBossBar(UserBarType.SPECIAL_BAR));
        }
      }
    }
  }

  public void handle(final PlatformAlertMessagePacket packet) {
    if (!SectorsPlugin.getInstance().isLoaded()) {
      return;
    }

    final String[] messages;
    if (packet.isTitle()) {
      final String[] titleData = packet.getMessage().split("\\|");
      messages = new String[]{
          titleData.length < 2
              ? this.plugin.getPlatformConfiguration().messagesWrapper.alertPrefixTitle
              : titleData[0],
          titleData.length > 1 ? StringUtils.join(titleData, "|", 1, titleData.length)
              : titleData[0]
      };
    } else {
      messages = new String[]{packet.getMessage()};
    }

    for (final Player player : this.plugin.getServer().getOnlinePlayers()) {
      if (packet.isTitle()) {
        ChatHelper.sendTitle(player, messages[0], messages[1]);
      } else {
        ChatHelper.sendMessage(player, messages[0]);
      }
    }
  }

  public void handle(final PlatformUserMessagePacket packet) {
    if (!SectorsPlugin.getInstance().isLoaded()) {
      return;
    }

    final List<Player> players = new ArrayList<>();
    if (!packet.getUniqueIds().isEmpty()) {
      for (final UUID uniqueId : packet.getUniqueIds()) {
        final Player player = this.plugin.getServer().getPlayer(uniqueId);
        if (Objects.nonNull(player)) {
          players.add(player);
        }
      }
    } else {
      players.addAll(Bukkit.getOnlinePlayers());
    }

    players.forEach(player -> ChatHelper.sendMessage(player, packet.getMessage()));
  }

  public void handle(final PlatformSetFreezeStatePacket packet) {
    if (SectorsPlugin.getInstance().isLoaded()) {
      final boolean lastServerFreezeState = this.plugin.getPlatformConfiguration().serverFreezeState;
      final boolean serverFreezeState = packet.getState();
      this.plugin.getPlatformConfiguration().serverFreezeState = serverFreezeState;
      if (serverFreezeState && !lastServerFreezeState) {
        this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
          for (final Player player : this.plugin.getServer().getOnlinePlayers()) {
            PlatformUserFreezeEntityHelper.spawnAndMountEntity(player);
          }
        });
      } else if (!serverFreezeState && lastServerFreezeState) {
        this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
          for (final Player player : this.plugin.getServer().getOnlinePlayers()) {
            PlatformUserFreezeEntityHelper.removeAndDisMountEntity(player);
          }
        });
      }
    }
  }

  public void handle(final PlatformUserRankUpdatePacket packet) {
    if (SectorsPlugin.getInstance().isLoaded()) {
      this.plugin.getPlatformUserFactory().findUserByUniqueId(packet.getUniqueId())
          .ifPresent(user -> {
            user.setRank(RankEntry.create(
                Objects.nonNull(packet.getPreviousRankName()) ? this.plugin.getRankFactory()
                    .findRank(packet.getPreviousRankName()).orElse(null) : null,
                this.plugin.getRankFactory().findRank(packet.getCurrentRankName())
                    .orElse(this.plugin.getRankFactory().getDefaultRank()),
                packet.getExpirationTime()));
            user.reloadPermissions();
          });
    }
  }

  public void handle(final PlatformUserSynchronizeChatSettingsPacket packet) {
    if (SectorsPlugin.getInstance().isLoaded()) {
      this.plugin.getPlatformUserFactory().findUserByUniqueId(packet.getUniqueId())
          .map(PlatformUser::getChatSettings).ifPresent(platformUserChatSettings -> {
            platformUserChatSettings.setGlobal(packet.isGlobal());
            platformUserChatSettings.setItemShop(packet.isItemShop());
            platformUserChatSettings.setKills(packet.isKills());
            platformUserChatSettings.setDeaths(packet.isDeaths());
            platformUserChatSettings.setCases(packet.isCases());
            platformUserChatSettings.setAchievements(packet.isAchievements());
            platformUserChatSettings.setRewards(packet.isRewards());
            platformUserChatSettings.setPrivateMessages(packet.isPrivateMessages());
          });
    }
  }

  public void handle(final PlatformUserSynchronizeSomeDropSettingsDataPacket packet) {
    if (SectorsPlugin.getInstance().isLoaded()) {
      this.plugin.getPlatformUserFactory().findUserByUniqueId(packet.getUniqueId())
          .map(PlatformUser::getDropSettings).ifPresent(platformUserDropSettings -> {
            platformUserDropSettings.setTurboDropMultiplier(packet.getTurboDropMultiplier());
            platformUserDropSettings.setTurboDropTime(packet.getTurboDropTime());
            platformUserDropSettings.setCobbleStone(packet.isCobbleStone());
            platformUserDropSettings.setCurrentXP(packet.getCurrentXP());
            platformUserDropSettings.setNeededXP(packet.getNeededXP());
            platformUserDropSettings.setLevel(packet.getLevel());
          });
    }
  }

  public void handle(final PlatformUserIgnoredPlayerUpdatePacket packet) {
    if (SectorsPlugin.getInstance().isLoaded()) {
      this.plugin.getPlatformUserFactory().findUserByUniqueId(packet.getSenderUniqueId())
          .ifPresent(user -> {
            if (packet.isAdd()) {
              user.addIgnoredPlayer(packet.getTargetUniqueId());
            } else {
              user.removeIgnoredPlayer(packet.getTargetUniqueId());
            }
          });
    }
  }

  public void handle(final PlatformUserTeleportRequestUpdatePacket packet) {
    if (SectorsPlugin.getInstance().isLoaded()) {
      this.plugin.getPlatformUserFactory().findUserByUniqueId(packet.getTargetUniqueId())
          .ifPresent(user -> {
            if (packet.isRemove()) {
              user.removeTeleportRequest(packet.getFromUniqueId());
            } else {
              user.addTeleportRequest(packet.getFromUniqueId());
            }
          });
    }
  }

  public void handle(final PlatformUserLastPrivateMessageUpdatePacket packet) {
    if (SectorsPlugin.getInstance().isLoaded()) {
      this.plugin.getPlatformUserFactory().findUserByUniqueId(packet.getFromUniqueId()).ifPresent(
          fromUser -> this.plugin.getPlatformUserFactory()
              .findUserByUniqueId(packet.getTargetUniqueId()).ifPresent(targetUser -> {
                fromUser.setLastPrivateMessage(targetUser.getUniqueId());
                targetUser.setLastPrivateMessage(fromUser.getUniqueId());
              }));
    }
  }

  public void handle(final PlatformUserCreatePacket packet) {
    if (SectorsPlugin.getInstance().isLoaded() && !this.plugin.getPlatformUserFactory()
        .findUserByUniqueId(packet.getUniqueId()).isPresent()) {
      final PlatformUser user = new PlatformUser(packet.getUniqueId(), packet.getNickname());
      user.setFirstJoin(true);
      this.plugin.getPlatformUserFactory().addUser(user);
    }
  }

  public void handle(final PlatformSafeCreatePacket packet) {
    if (SectorsPlugin.getInstance().isLoaded() && !this.plugin.getSafeFactory()
        .findSafe(packet.getUniqueId()).isPresent()) {
      final Safe safe = new Safe(packet.getUniqueId(), packet.getOwnerUniqueId(),
          packet.getOwnerNickname(), packet.getCreationTime());
      this.plugin.getSafeFactory().addSafe(safe);
    }
  }

  public void handle(final PlatformSafeOwnerUpdatePacket packet) {
    if (SectorsPlugin.getInstance().isLoaded()) {
      this.plugin.getSafeFactory().findSafe(packet.getUniqueId()).ifPresent(safe -> {
        safe.setOwnerUniqueId(packet.getOwnerUniqueId());
        safe.setOwnerNickname(packet.getOwnerNickname());
      });
    }
  }

  public void handle(final PlatformSafeContentsModifyPacket packet) {
    if (SectorsPlugin.getInstance().isLoaded()) {
      this.plugin.getSafeFactory().findSafe(packet.getUniqueId()).ifPresent(safe -> {
        safe.setContents(
            (ItemStack[]) SerializeHelper.deserializeBukkitObjectFromBytes(packet.getContents()));
        safe.setModifierUuid(null);
      });
    }
  }

  public void handle(final PlatformEndPortalPointCreatePacket packet) {
    if (SectorsPlugin.getInstance().isLoaded()
        && !this.plugin.getPlatformConfiguration().endPortalPointWrapperList.contains(
        packet.getWrapper())) {
      this.plugin.getPlatformConfiguration().endPortalPointWrapperList.add(packet.getWrapper());
    }
  }

  public void handle(final PlatformUserAddDepositLimitsPacket packet) {
    if (SectorsPlugin.getInstance().isLoaded()) {
      this.plugin.getPlatformUserFactory().findUserByUniqueId(packet.getUniqueId()).ifPresent(
          user -> packet.getAddedDepositLimitMap()
              .forEach((type, difference) -> user.addItemToDeposit(type.toOriginal(), difference)));
    }
  }

  public void handle(final PlatformUserRemoveDepositLimitsPacket packet) {
    if (SectorsPlugin.getInstance().isLoaded()) {
      this.plugin.getPlatformUserFactory().findUserByUniqueId(packet.getUniqueId()).ifPresent(
          user -> packet.getRemovedDepositLimitMap()
              .forEach((type, amount) -> user.removeItemFromDeposit(type.toOriginal(), amount)));
    }
  }

  public void handle(final PlatformUserDropSettingsAddDisabledDropPacket packet) {
    if (SectorsPlugin.getInstance().isLoaded()) {
      this.plugin.getPlatformUserFactory().findUserByUniqueId(packet.getUniqueId()).ifPresent(
          user -> this.plugin.getDropFactory().findDrop(packet.getDropName())
              .ifPresent(user.getDropSettings().getDisabledDropSet()::add));
    }
  }

  public void handle(final PlatformUserDropSettingsRemoveDisabledDropPacket packet) {
    if (SectorsPlugin.getInstance().isLoaded()) {
      this.plugin.getPlatformUserFactory().findUserByUniqueId(packet.getUniqueId()).ifPresent(
          user -> user.getDropSettings().getDisabledDropSet()
              .removeIf(drop -> drop.getName().equals(packet.getDropName())));
    }
  }

  public void handle(final PlatformUserReceiveKitPacket packet) {
    if (SectorsPlugin.getInstance().isLoaded()) {
      this.plugin.getKitFactory().findKit(packet.getKitName()).ifPresent(
          kit -> this.plugin.getPlatformUserFactory().findUserByUniqueId(packet.getUniqueId())
              .ifPresent(user -> user.receiveKit(kit, packet.getKitTime())));
    }
  }

  public void handle(final PlatformUserSetHomePacket packet) {
    if (SectorsPlugin.getInstance().isLoaded()) {
      this.plugin.getPlatformUserFactory().findUserByUniqueId(packet.getUniqueId()).ifPresent(
          user -> user.setHome(SerializeHelper.deserializeLocation(packet.getHomeLocation()),
              packet.getHomeId()));
    }
  }

  public void handle(final PlatformDropSettingsUpdateTurboDropPacket packet) {
    if (SectorsPlugin.getInstance().isLoaded()) {
      this.plugin.getPlatformConfiguration().dropSettingsWrapper.turboDropMultiplier = packet.getTurboDropMultiplier();
      this.plugin.getPlatformConfiguration().dropSettingsWrapper.turboDropTime = packet.getTurboDropTime();
    }
  }

  public void handle(final PlatformUserSelectedDiscoEffectTypeUpdatePacket packet) {
    if (SectorsPlugin.getInstance().isLoaded()) {
      this.plugin.getPlatformUserFactory().findUserByUniqueId(packet.getUniqueId()).ifPresent(
          user -> user.setSelectedDiscoEffectType(
              DiscoEffectType.valueOf(packet.getSelectedDiscoEffectTypeName())));
    }
  }

  public void handle(final PlatformUserCooldownSynchronizePacket packet) {
    if (SectorsPlugin.getInstance().isLoaded()) {
      this.plugin.getPlatformUserFactory().findUserByUniqueId(packet.getUniqueId()).ifPresent(
          user -> user.getCooldownCache().putUserCooldown(packet.getType(), packet.getTime()));
    }
  }

  public void handle(final PlatformUserDiscordRewardStateUpdatePacket packet) {
    if (SectorsPlugin.getInstance().isLoaded()) {
      this.plugin.getPlatformUserFactory().findUserByUniqueId(packet.getUniqueId())
          .ifPresent(user -> user.getRewardSettings().setDiscordRewardReceived(true));
    }
  }

  public void handle(final PlatformUserCombatTimeUpdatePacket packet) {
    if (SectorsPlugin.getInstance().isLoaded()) {
      this.plugin.getPlatformUserFactory().findUserByUniqueId(packet.getUniqueId())
          .ifPresent(user -> user.setCombatTime(packet.getCombatTime()));
    }
  }

  public void handle(final PlatformSafeDescriptionUpdatePacket packet) {
    if (SectorsPlugin.getInstance().isLoaded()) {
      this.plugin.getSafeFactory().findSafe(packet.getUniqueId())
          .ifPresent(safe -> safe.setDescription(packet.getDescription()));
    }
  }

  public void handle(final PlatformSafeLastOpenedTimeUpdatePacket packet) {
    if (SectorsPlugin.getInstance().isLoaded()) {
      this.plugin.getSafeFactory().findSafe(packet.getUniqueId())
          .ifPresent(safe -> safe.setLastOpenedTime(packet.getLastOpenedTime()));
    }
  }

  public void handle(final PlatformSafeModifierUpdatePacket packet) {
    if (SectorsPlugin.getInstance().isLoaded()) {
      this.plugin.getSafeFactory().findSafe(packet.getUniqueId())
          .ifPresent(safe -> safe.setModifierUuid(packet.getModifierUniqueId()));
    }
  }

  public void handle(final PlatformUserNicknameUpdatePacket packet) {
    if (SectorsPlugin.getInstance().isLoaded()) {
      this.plugin.getPlatformUserFactory().findUserByUniqueId(packet.getUniqueId())
          .ifPresent(user -> user.setNickname(packet.getNickname()));
    }
  }

  public void handle(final PlatformUserVanishStateUpdatePacket packet) {
    if (SectorsPlugin.getInstance().isLoaded()) {
      this.plugin.getPlatformUserFactory().findUserByUniqueId(packet.getUniqueId())
          .ifPresent(user -> user.setVanish(packet.getState()));
    }
  }

  public void handle(final PlatformUserDisableFirstJoinStatePacket packet) {
    if (SectorsPlugin.getInstance().isLoaded()) {
      this.plugin.getPlatformUserFactory().findUserByUniqueId(packet.getUniqueId())
          .ifPresent(user -> user.setFirstJoin(false));
    }
  }

  public void handle(final PlatformUserGodStateUpdatePacket packet) {
    if (SectorsPlugin.getInstance().isLoaded()) {
      this.plugin.getPlatformUserFactory().findUserByUniqueId(packet.getUniqueId())
          .ifPresent(user -> user.setGod(packet.getState()));
    }
  }

  public void handle(final PlatformEndPortalPointDeletePacket packet) {
    if (SectorsPlugin.getInstance().isLoaded() && packet.getPointId()
        < this.plugin.getPlatformConfiguration().endPortalPointWrapperList.size()) {
      this.plugin.getPlatformConfiguration().endPortalPointWrapperList.remove(packet.getPointId());
    }
  }

  public void handle(final PlatformSetSpawnPacket packet) {
    if (SectorsPlugin.getInstance().isLoaded()) {
      this.plugin.getPlatformConfiguration().spawnLocationWrapper = packet.getSpawnLocationWrapper();
    }
  }

  public void handle(final PlatformChatStateChangePacket packet) {
    if (SectorsPlugin.getInstance().isLoaded()) {
      this.plugin.getPlatformConfiguration().chatStatusType = packet.getType();
    }
  }

  public void handle(final PlatformSetSlotsPacket packet) {
    if (SectorsPlugin.getInstance().isLoaded() && !packet.isProxy()) {
      this.plugin.getPlatformConfiguration().slotWrapper.spigotSlots = packet.getSlots();
    }
  }
}
