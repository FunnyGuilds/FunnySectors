package pl.rosehc.controller.platform.user;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import pl.rosehc.adapter.database.DatabaseAdapter;
import pl.rosehc.adapter.database.DatabaseRepository;
import pl.rosehc.controller.platform.rank.Rank;

public final class PlatformUserRepository extends DatabaseRepository<UUID, PlatformUser> {

  public PlatformUserRepository(final DatabaseAdapter databaseAdapter) throws SQLException {
    super(databaseAdapter);
  }

  @Override
  public Map<UUID, PlatformUser> loadAll() throws SQLException {
    final Map<UUID, PlatformUser> userMap = new ConcurrentHashMap<>();
    this.doSelect("SELECT * FROM rhc_platform_users", result -> {
      final PlatformUser user = new PlatformUser(result);
      userMap.put(user.getUniqueId(), user);
    });
    return userMap;
  }

  @Override
  public PlatformUser load(final UUID ignored) {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public void prepareTable() throws SQLException {
    this.consumeConnection(connection -> {
      try (final Statement statement = connection.createStatement()) {
        //noinspection SqlDialectInspection,SqlNoDataSourceInspection
        statement.executeUpdate(
            "CREATE TABLE IF NOT EXISTS rhc_platform_users (uniqueId CHAR(36) PRIMARY KEY NOT NULL, nickname VARCHAR(16) NOT NULL,"
                + " lastPrivateMessage TEXT, cooldowns TEXT, previousRankName TEXT, currentRankName TEXT, homes TEXT,"
                + " receivedKits TEXT, depositItems TEXT, computerUid BYTEA, rankExpirationTime BIGINT, firstJoin BOOLEAN,"
                + " vanish BOOLEAN, god BOOLEAN, disabledDrops TEXT, turboDropMultiplier DOUBLE PRECISION, cobbleStone BOOLEAN,"
                + " turboDropTime BIGINT, currentXP INT, neededXP INT, level INT, globalChatStatus BOOLEAN, itemShopChatStatus BOOLEAN,"
                + " killsChatStatus BOOLEAN, deathsKillStatus BOOLEAN, casesChatStatus BOOLEAN, achievementsChatStatus BOOLEAN,"
                + " rewardsChatStatus BOOLEAN, privateChatMessagesStatus BOOLEAN, discordUserId BIGINT, discordRewardReceived BOOLEAN,"
                + " selectedDiscoEffectTypeName TEXT, ignoredPlayers TEXT);");
      }
    });
  }

  @Override
  public void insert(final PlatformUser user) throws SQLException {
    this.doUpdate(
        "INSERT INTO rhc_platform_users (uniqueId, nickname, cooldowns, lastPrivateMessage, previousRankName,"
            + " currentRankName, homes, receivedKits, depositItems, rankExpirationTime,"
            + " computerUid, firstJoin, vanish, god, disabledDrops,"
            + " turboDropMultiplier, cobbleStone, turboDropTime, currentXP, neededXP, level,"
            + " globalChatStatus, itemShopChatStatus, killsChatStatus, deathsKillStatus, casesChatStatus,"
            + " achievementsChatStatus, rewardsChatStatus, privateChatMessagesStatus, discordUserId,"
            + " discordRewardReceived, selectedDiscoEffectTypeName, ignoredPlayers)"
            + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
        statement -> {
          statement.setString(1, user.getUniqueId().toString());
          statement.setString(2, user.getNickname());
          statement.setString(3, null);
          statement.setString(4, null);
          statement.setString(5, null);
          statement.setString(6, user.getRank().getCurrentRank().getName());
          statement.setString(7, null);
          statement.setString(8, null);
          statement.setString(9, null);
          statement.setLong(10, 0L);
          statement.setBytes(11, null);
          statement.setBoolean(12, true);
          statement.setBoolean(13, false);
          statement.setBoolean(14, false);
          statement.setString(15, null);
          statement.setDouble(16, 0D);
          statement.setBoolean(17, true);
          statement.setLong(18, 0L);
          statement.setInt(19, 0);
          statement.setInt(20, 500);
          statement.setInt(21, 1);
          statement.setBoolean(22, true);
          statement.setBoolean(23, true);
          statement.setBoolean(24, true);
          statement.setBoolean(25, true);
          statement.setBoolean(26, true);
          statement.setBoolean(27, true);
          statement.setBoolean(28, true);
          statement.setBoolean(29, true);
          statement.setLong(30, 0L);
          statement.setBoolean(31, false);
          statement.setString(32, null);
          statement.setString(33, null);
        });
  }

  @Override
  public void update(final PlatformUser ignored) {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public void updateAll(final Collection<PlatformUser> platformUserCollection) throws SQLException {
    this.doUpdate(
        "UPDATE rhc_platform_users SET nickname = ?, lastPrivateMessage = ?, cooldowns = ?, previousRankName = ?,"
            + " currentRankName = ?, homes = ?, receivedKits = ?, depositItems = ?, computerUid = ?,"
            + " rankExpirationTime = ?, firstJoin = ?, vanish = ?, god = ?, disabledDrops = ?,"
            + " turboDropMultiplier = ?, cobbleStone = ?, turboDropTime = ?, currentXP = ?,"
            + " neededXP = ?, level = ?, globalChatStatus = ?, itemShopChatStatus = ?,"
            + " killsChatStatus = ?, deathsKillStatus = ?, casesChatStatus = ?, achievementsChatStatus = ?,"
            + " rewardsChatStatus = ?, privateChatMessagesStatus = ?, discordUserId = ?, discordRewardReceived = ?,"
            + " selectedDiscoEffectTypeName = ?, ignoredPlayers = ? WHERE uniqueId = ?",
        true, statement -> {
          for (final PlatformUser user : platformUserCollection) {
            final Optional<Rank> previousRank = Optional.ofNullable(
                user.getRank().getPreviousRank());
            statement.setString(1, user.getNickname());
            statement.setString(2,
                user.getLastPrivateMessage() != null ? user.getLastPrivateMessage().toString()
                    : null);
            statement.setString(3, PlatformUserSerializationHelper.serializeCooldownMap(
                user.getCooldownCache().getUserCooldownMap()));
            statement.setString(4, previousRank.map(Rank::getName).orElse(null));
            statement.setString(5, user.getRank().getCurrentRank().getName());
            statement.setString(6,
                user.getHomes() != null ? String.join("@", user.getHomes()) : null);
            statement.setString(7,
                PlatformUserSerializationHelper.serializeReceivedKitMap(user.getReceivedKitMap()));
            statement.setString(8,
                PlatformUserSerializationHelper.serializeDepositItemMap(user.getDepositItemMap()));
            statement.setBytes(9, user.getComputerUid());
            statement.setLong(10,
                previousRank.map(ignored -> user.getRank().getExpirationTime()).orElse(0L));
            statement.setBoolean(11, user.isFirstJoin());
            statement.setBoolean(12, user.isVanish());
            statement.setBoolean(13, user.isGod());
            statement.setString(14, String.join(",", user.getDropSettings().getDisabledDropSet()));
            statement.setDouble(15, user.getDropSettings().getTurboDropMultiplier());
            statement.setBoolean(16, user.getDropSettings().isCobbleStone());
            statement.setLong(17, user.getDropSettings().getTurboDropTime());
            statement.setInt(18, user.getDropSettings().getCurrentXP());
            statement.setInt(19, user.getDropSettings().getNeededXP());
            statement.setInt(20, user.getDropSettings().getLevel());
            statement.setBoolean(21, user.getChatSettings().isGlobal());
            statement.setBoolean(22, user.getChatSettings().isItemShop());
            statement.setBoolean(23, user.getChatSettings().isKills());
            statement.setBoolean(24, user.getChatSettings().isDeaths());
            statement.setBoolean(25, user.getChatSettings().isCases());
            statement.setBoolean(26, user.getChatSettings().isAchievements());
            statement.setBoolean(27, user.getChatSettings().isRewards());
            statement.setBoolean(28, user.getChatSettings().isPrivateMessages());
            statement.setLong(29, user.getRewardSettings().getDiscordUserId());
            statement.setBoolean(30, user.getRewardSettings().isDiscordRewardReceived());
            statement.setString(31,
                user.getIgnoredPlayerSet() != null ? user.getIgnoredPlayerSet().stream()
                    .map(UUID::toString).collect(Collectors.joining(",")) : null);
            statement.setString(32, user.getSelectedDiscoEffectTypeName() != null
                ? user.getSelectedDiscoEffectTypeName() : null);
            statement.setString(33, user.getUniqueId().toString());
            statement.addBatch();
          }
        });
  }

  @Override
  public void delete(final PlatformUser ignored) {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public void deleteAll(final Collection<PlatformUser> ignored) {
    throw new UnsupportedOperationException("Not implemented");
  }
}
