package pl.rosehc.controller.guild.guild;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import pl.rosehc.adapter.database.DatabaseAdapter;
import pl.rosehc.adapter.database.DatabaseReference;
import pl.rosehc.adapter.database.DatabaseRepository;

public final class GuildRepository extends DatabaseRepository<String, Guild> {

  public GuildRepository(final DatabaseAdapter databaseAdapter) throws SQLException {
    super(databaseAdapter);
  }

  @Override
  public Map<String, Guild> loadAll() throws SQLException {
    final Map<String, Guild> guildMap = new ConcurrentHashMap<>();
    this.doSelect("SELECT * FROM rhc_guild_guilds", statement -> {
      final Guild guild = new Guild(statement);
      guildMap.put(guild.getTag().toLowerCase(), guild);
      guildMap.put(guild.getName().toLowerCase(), guild);
    });
    return guildMap;
  }

  @Override
  public Guild load(final String ignored) {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public void prepareTable() throws SQLException {
    this.consumeConnection(connection -> {
      try (final Statement statement = connection.createStatement()) {
        //noinspection SqlDialectInspection,SqlNoDataSourceInspection
        statement.executeUpdate(
            "CREATE TABLE IF NOT EXISTS rhc_guild_guilds (name VARCHAR(255) PRIMARY KEY NOT NULL, tag VARCHAR(255) NOT NULL, groups TEXT[] NOT NULL,"
                + " members TEXT[] NOT NULL, guildType TEXT NOT NULL, creationSector TEXT NOT NULL, alliedGuild TEXT,"
                + " regenerationBlocks TEXT[], centerLocation TEXT NOT NULL, regionSize INT,"
                + " homeLocation TEXT NOT NULL, joinAlertMessage TEXT, creationTime BIGINT,"
                + " validityTime BIGINT, protectionTime BIGINT, pvpGuild BOOLEAN,"
                + " pvpAlly BOOLEAN, lives INT, health INT, pistonsOnGuild INT);");
      }
    });
  }

  @Override
  public void insert(final Guild guild) throws SQLException {
    this.doUpdate(
        "INSERT INTO rhc_guild_guilds (name, tag, groups, members,"
            + " guildType, creationSector, centerLocation, regionSize,"
            + " homeLocation, creationTime, validityTime,"
            + " protectionTime, pvpGuild, pvpAlly, lives, health)"
            + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
        statement -> {
          statement.setString(1, guild.getName());
          statement.setString(2, guild.getTag());
          statement.setArray(3, statement.getConnection()
              .createArrayOf("text", GuildHelper.serializeGuildGroups(guild.getGuildGroupMap())));
          statement.setArray(4, statement.getConnection()
              .createArrayOf("text", GuildHelper.serializeGuildMembers(guild.getGuildMembers())));
          statement.setString(5, guild.getGuildType().name());
          statement.setString(6, guild.getCreationSector().getName());
          statement.setString(7, guild.getGuildRegion().getCenterLocation());
          statement.setInt(8, guild.getGuildRegion().getSize());
          statement.setString(9, guild.getHomeLocation());
          statement.setLong(10, guild.getCreationTime());
          statement.setLong(11, guild.getValidityTime());
          statement.setLong(12, guild.getProtectionTime());
          statement.setBoolean(13, guild.isPvpGuild());
          statement.setBoolean(14, guild.isPvpAlly());
          statement.setInt(15, guild.getLives());
          statement.setInt(16, guild.getHealth());
        });
  }

  @Override
  public void update(final Guild ignored) {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public void updateAll(final Collection<Guild> guildCollection) throws SQLException {
    this.doUpdate(
        "UPDATE rhc_guild_guilds SET groups = ?, members = ?, alliedGuild = ?, regenerationBlocks = ?,"
            + " regionSize = ?, homeLocation = ?, joinAlertMessage = ?,"
            + " validityTime = ?, protectionTime = ?, pvpGuild = ?,"
            + " pvpAlly = ?, lives = ?, health = ?, pistonsOnGuild = ? WHERE name = ?",
        true, statement -> {
          for (final Guild guild : guildCollection) {
            final String[] serializedRegenerationBlocks = GuildHelper.serializeRegenerationBlocks(
                guild.getRegenerationBlockStateList());
            statement.setArray(1, statement.getConnection()
                .createArrayOf("text", GuildHelper.serializeGuildGroups(guild.getGuildGroupMap())));
            statement.setArray(2, statement.getConnection()
                .createArrayOf("text", GuildHelper.serializeGuildMembers(guild.getGuildMembers())));
            statement.setString(3,
                guild.getAlliedGuild() != null ? guild.getAlliedGuild().getTag() : null);
            statement.setArray(4, serializedRegenerationBlocks != null ? statement.getConnection()
                .createArrayOf("text", serializedRegenerationBlocks) : null);
            statement.setInt(5, guild.getGuildRegion().getSize());
            statement.setString(6, guild.getHomeLocation());
            statement.setString(7, guild.getJoinAlertMessage());
            statement.setLong(8, guild.getValidityTime());
            statement.setLong(9, guild.getProtectionTime());
            statement.setBoolean(10, guild.isPvpGuild());
            statement.setBoolean(11, guild.isPvpAlly());
            statement.setInt(12, guild.getLives());
            statement.setInt(13, guild.getHealth());
            statement.setInt(14, guild.getPistonsOnGuild());
            statement.setString(15, guild.getName());
            statement.addBatch();
          }
        });
  }

  @Override
  public void delete(final Guild guild) throws SQLException {
    this.doUpdate("DELETE FROM rhc_guild_guilds WHERE tag = ? OR name = ?", statement -> {
      statement.setString(1, guild.getTag());
      statement.setString(2, guild.getName());
    });
  }

  @Override
  public void deleteAll(final Collection<Guild> ignored) {
    throw new UnsupportedOperationException("Not implemented");
  }

  public String fetchAlliedGuildTag(final String name) throws SQLException {
    final DatabaseReference<String> alliedGuildReference = new DatabaseReference<>();
    this.doSelect("SELECT alliedGuild FROM rhc_guild_guilds WHERE name = ?",
        statement -> statement.setString(1, name), result -> {
          final String alliedGuild = result.getString("alliedGuild");
          if (alliedGuild != null) {
            alliedGuildReference.set(alliedGuild);
          }
        });
    return alliedGuildReference.get();
  }
}
