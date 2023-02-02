package pl.rosehc.controller.configuration.impl.configuration;

import com.google.gson.annotations.SerializedName;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import pl.rosehc.controller.configuration.ConfigurationData;
import pl.rosehc.controller.configuration.impl.configuration.PlatformConfiguration.CustomItemType;
import pl.rosehc.controller.configuration.impl.configuration.PlatformConfiguration.CustomItemsWrapper;
import pl.rosehc.controller.configuration.impl.configuration.PlatformConfiguration.CustomItemsWrapper.CustomItemWrapper;
import pl.rosehc.controller.wrapper.global.BarColorWrapper;
import pl.rosehc.controller.wrapper.global.BarStyleWrapper;
import pl.rosehc.controller.wrapper.guild.GuildGroupColorWrapper;
import pl.rosehc.controller.wrapper.guild.GuildPermissionTypeWrapper;
import pl.rosehc.controller.wrapper.guild.GuildTypeWrapper;
import pl.rosehc.controller.wrapper.guild.gui.GuildItemPreviewGuiElementWrapper;
import pl.rosehc.controller.wrapper.spigot.DefaultSpigotGuiElementWrapper;
import pl.rosehc.controller.wrapper.spigot.SpigotGuiWrapper;

public final class GuildsConfiguration extends ConfigurationData {

  @SerializedName("messages")
  public MessagesWrapper messagesWrapper = new MessagesWrapper();
  @SerializedName("plugin")
  public PluginWrapper pluginWrapper = new PluginWrapper();
  @SerializedName("inventories")
  public Map<String, SpigotGuiWrapper> inventoryMap = new HashMap<String, SpigotGuiWrapper>() {{
    this.put("type_selection", createTypeSelectionGuiWrapper());
    this.put("guild_items", createItemsGuiWrapper());
  }};
  @SerializedName("tablist")
  public List<TabListElementsWrapper> tabListElementsWrapperList = Arrays.asList(
      createTabListElementsWrapper(
          new LinkedHashMap<Integer, String>() {{
            for (int index = 0; index < 80; index++) {
              this.put(index, "&awitaj {PLAYER_NAME} na slocie " + index);
            }
          }},
          "&aTEST HEADER 1", "&cTEST HEADER 2",
          "30s"
      ),
      createTabListElementsWrapper(
          new LinkedHashMap<Integer, String>() {{
            for (int index = 0; index < 80; index++) {
              this.put(index, "&awitaj {PLAYER_NAME} na slocie " + index);
            }
          }},
          "&aTEST HEADER 1", "&cTEST HEADER 2",
          "30s"
      )
  );

  private static SpigotGuiWrapper createTypeSelectionGuiWrapper() {
    final SpigotGuiWrapper typeSelectionGuiWrapper = new SpigotGuiWrapper();
    final DefaultSpigotGuiElementWrapper fillElementWrapper = new DefaultSpigotGuiElementWrapper();
    final DefaultSpigotGuiElementWrapper smallTypeElementWrapper = new DefaultSpigotGuiElementWrapper();
    final DefaultSpigotGuiElementWrapper mediumTypeElementWrapper = new DefaultSpigotGuiElementWrapper();
    final DefaultSpigotGuiElementWrapper largeTypeElementWrapper = new DefaultSpigotGuiElementWrapper();
    smallTypeElementWrapper.material = "WOOD_SWORD";
    smallTypeElementWrapper.name = "&6MAŁA";
    mediumTypeElementWrapper.material = "IRON_SWORD";
    mediumTypeElementWrapper.name = "&cŚREDNIA";
    mediumTypeElementWrapper.slot = 2;
    largeTypeElementWrapper.material = "DIAMOND_SWORD";
    largeTypeElementWrapper.name = "&4DUŻA";
    largeTypeElementWrapper.slot = 4;
    fillElementWrapper.material = "STAINED_GLASS_PANE";
    fillElementWrapper.name = "&8#";
    fillElementWrapper.data = (byte) 15;
    typeSelectionGuiWrapper.inventoryName = "&dWybierz typ gildii";
    typeSelectionGuiWrapper.inventoryType = "HOPPER";
    typeSelectionGuiWrapper.fillElement = fillElementWrapper;
    typeSelectionGuiWrapper.elements = new LinkedHashMap<>();
    typeSelectionGuiWrapper.elements.put("SMALL", smallTypeElementWrapper);
    typeSelectionGuiWrapper.elements.put("MEDIUM", mediumTypeElementWrapper);
    typeSelectionGuiWrapper.elements.put("LARGE", largeTypeElementWrapper);
    return typeSelectionGuiWrapper;
  }

  private static SpigotGuiWrapper createItemsGuiWrapper() {
    final SpigotGuiWrapper itemsGuiWrapper = new SpigotGuiWrapper();
    final DefaultSpigotGuiElementWrapper fillElementWrapper = new DefaultSpigotGuiElementWrapper();
    final GuildItemPreviewGuiElementWrapper itemPreviewElementWrapper = new GuildItemPreviewGuiElementWrapper();
    fillElementWrapper.material = "STAINED_GLASS_PANE";
    fillElementWrapper.name = "&8#";
    fillElementWrapper.data = (byte) 15;
    itemsGuiWrapper.inventoryName = "&cItemy";
    itemsGuiWrapper.inventorySize = 27;
    itemsGuiWrapper.fillElement = fillElementWrapper;
    itemPreviewElementWrapper.material = "COBBLESTONE";
    itemPreviewElementWrapper.name = "&cCOBBLESTONE";
    itemPreviewElementWrapper.lore = Arrays.asList(
        "&7Ilość: &d{INVENTORY_AMOUNT}&8/&d{REQUIRED_AMOUNT}",
        "&7Procent ilości: &d{AMOUNT_PERCENTAGE}%"
    );
    itemPreviewElementWrapper.slot = 13;
    itemsGuiWrapper.elements = new LinkedHashMap<>();
    itemsGuiWrapper.elements.put("item0", itemPreviewElementWrapper);
    return itemsGuiWrapper;
  }

  private static TabListElementsWrapper createTabListElementsWrapper(
      final Map<Integer, String> elementsMap, final String header, final String footer,
      final String updateTime) {
    final TabListElementsWrapper wrapper = new TabListElementsWrapper();
    wrapper.elementsMap = elementsMap;
    wrapper.header = header;
    wrapper.footer = footer;
    wrapper.updateTime = updateTime;
    return wrapper;
  }

  public static final class PluginWrapper {

    public UUID defaultGroupUUID = UUID.randomUUID();
    public String cuboidSchematicFilePath = "./schematics/cuboid.schematic";
    public Map<UUID, ConfigurationGroupWrapper> groupMap = new LinkedHashMap<UUID, ConfigurationGroupWrapper>() {{
      this.put(UUID.randomUUID(), createGroupWrapper(
          new HashSet<>(Arrays.asList(
              GuildPermissionTypeWrapper.PLACING_BLOCKS,
              GuildPermissionTypeWrapper.BREAKING_BLOCKS,
              GuildPermissionTypeWrapper.CHEST_ACCESS,
              GuildPermissionTypeWrapper.FURNACES_ACCESS,
              GuildPermissionTypeWrapper.ANVIL_PLACING_ACCESS,
              GuildPermissionTypeWrapper.WATER_PLACING_ACCESS,
              GuildPermissionTypeWrapper.LAVA_PLACING_ACCESS,
              GuildPermissionTypeWrapper.LOG_BLOCK_ACCESS,
              GuildPermissionTypeWrapper.ACCEPTING_TELEPORTS_ON_TERRAIN,
              GuildPermissionTypeWrapper.PLACING_LAPIS_BLOCKS,
              GuildPermissionTypeWrapper.PLACING_COAL,
              GuildPermissionTypeWrapper.PLACING_OBSIDIAN,
              GuildPermissionTypeWrapper.CHANGING_PVP_STATE_IN_GUILD,
              GuildPermissionTypeWrapper.PLACING_SAND
          )),
          GuildGroupColorWrapper.GREEN,
          "ZAUFANY",
          false,
          false
      ));
      this.put(UUID.randomUUID(), createGroupWrapper(
          new HashSet<>(Arrays.asList(
              GuildPermissionTypeWrapper.PLACING_BLOCKS,
              GuildPermissionTypeWrapper.BREAKING_BLOCKS,
              GuildPermissionTypeWrapper.CHEST_ACCESS,
              GuildPermissionTypeWrapper.FURNACES_ACCESS,
              GuildPermissionTypeWrapper.ANVIL_PLACING_ACCESS,
              GuildPermissionTypeWrapper.WATER_PLACING_ACCESS
          )),
          GuildGroupColorWrapper.BLUE,
          "CZŁONEK",
          false,
          false
      ));
      this.put(defaultGroupUUID, createGroupWrapper(
          new HashSet<>(Arrays.asList(
              GuildPermissionTypeWrapper.PLACING_BLOCKS,
              GuildPermissionTypeWrapper.BREAKING_BLOCKS,
              GuildPermissionTypeWrapper.CHEST_ACCESS,
              GuildPermissionTypeWrapper.FURNACES_ACCESS,
              GuildPermissionTypeWrapper.ANVIL_PLACING_ACCESS
          )),
          GuildGroupColorWrapper.PINK,
          "REKRUT",
          false,
          false
      ));
      this.put(UUID.randomUUID(), createGroupWrapper(
          new HashSet<>(EnumSet.allOf(GuildPermissionTypeWrapper.class)),
          GuildGroupColorWrapper.YELLOW,
          "LIDER",
          true,
          false
      ));
      this.put(UUID.randomUUID(), createGroupWrapper(
          new HashSet<>(EnumSet.allOf(GuildPermissionTypeWrapper.class)),
          GuildGroupColorWrapper.RED,
          "ZASTĘPCA",
          false,
          true
      ));
    }};
    @SerializedName("blocked_terrain_commands")
    public List<String> blockedTerrainCommandList = Arrays.asList(
        "/home", "/sethome",
        "/dom", "/domki",
        "/ustawdom"
    );

    @SerializedName("guild_items")
    public Map<GuildTypeWrapper, List<GuildItemWrapper>> guildItemWrapperMap = new LinkedHashMap<GuildTypeWrapper, List<GuildItemWrapper>>() {{
      final List<GuildItemWrapper> guildItemWrapperList = Arrays.asList(
          createGuildItemWrapper(
              CustomItemsWrapper.createCustomItemWrapper("BOOKSHELF", null, null, null, (byte) 0,
                  64), null),
          createGuildItemWrapper(
              CustomItemsWrapper.createCustomItemWrapper("ENDER_PEARL", null, null, null, (byte) 0,
                  16), null),
          createGuildItemWrapper(
              CustomItemsWrapper.createCustomItemWrapper("GOLDEN_APPLE", null, null, null, (byte) 0,
                  64), null),
          createGuildItemWrapper(
              CustomItemsWrapper.createCustomItemWrapper("SKULL_ITEM", null, null, null, (byte) 0,
                  8), CustomItemType.GHEAD),
          createGuildItemWrapper(
              CustomItemsWrapper.createCustomItemWrapper("ANVIL", null, null, null, (byte) 0, 32),
              null),
          createGuildItemWrapper(
              CustomItemsWrapper.createCustomItemWrapper("SLIME_BLOCK", null, null, null, (byte) 0,
                  8), null),
          createGuildItemWrapper(
              CustomItemsWrapper.createCustomItemWrapper("DIAMOND_BLOCK", null, null, null,
                  (byte) 0, 48), null),
          createGuildItemWrapper(
              CustomItemsWrapper.createCustomItemWrapper("ENCHANTMENT_TABLE", null, null, null,
                  (byte) 0, 48), null)
      );
      for (final GuildTypeWrapper wrapper : GuildTypeWrapper.values()) {
        this.put(wrapper, guildItemWrapperList);
      }
    }};
    @SerializedName("guild_items_percentage_changes")
    public Map<String, Double> guildItemPercentageChangeMap = new LinkedHashMap<String, Double>() {{
      this.put("guilds-guild-items-vip", 25D);
      this.put("guilds-guild-items-svip", 35D);
      this.put("guilds-guild-items-champion", 50D);
    }};
    @SerializedName("regeneration_block_replacements")
    public Map<String, MaterialDataWrapper> regenerationBlockReplacementMap = new LinkedHashMap<String, MaterialDataWrapper>() {{
      this.put(createMaterialDataWrapper("GOLD_BLOCK", (byte) 0).toString(),
          createMaterialDataWrapper("STONE", (byte) 0));
      this.put(createMaterialDataWrapper("DIAMOND_BLOCK", (byte) 0).toString(),
          createMaterialDataWrapper("STONE", (byte) 0));
    }};
    @SerializedName("explosion_chances")
    public Map<String, Double> explosionChanceMap = new LinkedHashMap<String, Double>() {{
      this.put(createMaterialDataWrapper("OBSIDIAN", (byte) 0).toString(), 20D);
    }};

    @SerializedName("enlarge_item")
    public GuildItemWrapper enlargeItemWrapper = createGuildItemWrapper(
        CustomItemsWrapper.createCustomItemWrapper("DIAMOND", null, null, null, (short) 0, 5),
        null);
    @SerializedName("validity_item")
    public GuildItemWrapper validityItemWrapper = createGuildItemWrapper(
        CustomItemsWrapper.createCustomItemWrapper("EMERALD", null, null, null, (short) 0, 192),
        null);
    public String killerPointsCalculation = "NO_CALCULATION";
    public String victimPointsCalculation = "NO_CALCULATION";
    public String killerAndVictimPointsCalculation = "abs({VICTIM_POINTS} - {KILLER_POINTS}) / 2";
    public String enlargeAmountCalculation = "({REGION_SIZE} / {ENLARGE_ITEM_AMOUNT}) * 2";
    public String validityAmountCalculation = "{VALIDITY_ITEM_AMOUNT} + {GUILD_REGION_SIZE}";
    public String victimKillerConsiderationTimeoutTime = "30s";
    public String victimKillerLastKillTimeoutTime = "1h";
    public String startGuildProtectionTime = "1d";
    public String startGuildValidityTime = "3d";
    public String addGuildValidityTime = "1d";
    public String whenGuildValidityTime = "1d";
    public String maxGuildValidityTime = "7d";
    public String minGuildValidityTime = "30h";
    public String guildMemberInviteConsiderationTimeoutTime = "2m";
    public String guildAllyInviteConsiderationTimeoutTime = "2m";
    public String tntExplosionTime = "2m";
    public String tntExplosionNotificationTime = "30s";
    public String guildNeedHelpWaypointTime = "30s";
    public int startUserPoints = 1000;
    public int startGuildSize = 50;
    public int startGuildLives = 4;
    public int enlargeGuildSize = 10;
    public int maxGuildSize = 100;
    public int minGuildDistance = 10;
    public int guildY = 35;
    public int minSectorDistance = 100;
    public int minTagLength = 3, maxTagLength = 5;
    public int minNameLength = 4, maxNameLength = 22;
    public int guildNeedHelpNormalLimit = 10, guildNeedHelpAllyLimit = 5;
    public int pistonLimit = 1000;
    public int guildDeputyLimit = 2;
    public int guildStartHealth = 500;

    public static MaterialDataWrapper createMaterialDataWrapper(final String material,
        final byte data) {
      final MaterialDataWrapper wrapper = new MaterialDataWrapper();
      wrapper.material = material;
      wrapper.data = data;
      return wrapper;
    }

    private static ConfigurationGroupWrapper createGroupWrapper(
        final Set<GuildPermissionTypeWrapper> permissions, final GuildGroupColorWrapper color,
        final String name, final boolean leader, final boolean deputy) {
      final ConfigurationGroupWrapper wrapper = new ConfigurationGroupWrapper();
      wrapper.permissions = permissions;
      wrapper.color = color;
      wrapper.name = name;
      wrapper.leader = leader;
      wrapper.deputy = deputy;
      return wrapper;
    }

    private static GuildItemWrapper createGuildItemWrapper(
        final CustomItemWrapper normalItemWrapper, final CustomItemType customItemType) {
      final GuildItemWrapper wrapper = new GuildItemWrapper();
      wrapper.normalItemWrapper = normalItemWrapper;
      wrapper.customItemType = customItemType;
      return wrapper;
    }

    public static final class GuildItemWrapper {

      @SerializedName("normal_item")
      public CustomItemWrapper normalItemWrapper;
      @SerializedName("custom_item")
      public CustomItemType customItemType;
    }

    public static final class ConfigurationGroupWrapper {

      public Set<GuildPermissionTypeWrapper> permissions;
      public GuildGroupColorWrapper color;
      public String name;
      public boolean leader, deputy;
    }

    public static final class MaterialDataWrapper {

      public String material;
      public byte data;

      @Override
      public String toString() {
        return this.material + ":" + this.data;
      }
    }
  }

  @SuppressWarnings("SpellCheckingInspection")
  public static final class MessagesWrapper {

    @SerializedName("killInfoMessages")
    public Map<String, String> killInfoMessageMap = new HashMap<String, String>() {{
      this.put("kill_info_message_without_guild",
          "&7Gracz &d{VICTIM_PLAYER_NAME} &8(&5-{VICTIM_POINTS_CHANGE}&8) &7został zabity przez gracza &d{KILLER_PLAYER_NAME} &8(&5+{KILLER_POINTS_CHANGE})");
      this.put("kill_info_message_with_victim_guild",
          "&7Gracz &8[&d{VICTIM_GUILD_TAG}&8] &d{VICTIM_PLAYER_NAME} &8(&5-{VICTIM_POINTS_CHANGE}&8) &7został zabity przez gracza &d{KILLER_PLAYER_NAME} &8(&5+{KILLER_POINTS_CHANGE})");
      this.put("kill_info_message_with_killer_guild",
          "&7Gracz &d{VICTIM_PLAYER_NAME} &8(&5-{VICTIM_POINTS_CHANGE}&8) &7został zabity przez gracza &8[&d{KILLER_GUILD_TAG}&8] &d{KILLER_PLAYER_NAME} &8(&5+{KILLER_POINTS_CHANGE})");
      this.put("kill_info_message_with_both_guild",
          "&7Gracz &8[&d{VICTIM_GUILD_TAG}&8] &d{VICTIM_PLAYER_NAME} &8(&5-{VICTIM_POINTS_CHANGE}&8) &7został zabity przez gracza &8[&d{KILLER_GUILD_TAG}&8] &d{KILLER_PLAYER_NAME} &8(&5+{KILLER_POINTS_CHANGE})");
    }};

    public String guildNotFound = "&cGildia o tagu {TAG} nie istnieje!";
    public String deathInfo = "&cGracz {PLAYER_NAME} zginął z niewyjaśnionych przyczyn! (-1)";
    public String cannotKillLastVictimToVictim = "&cTwój ranking nie został zabrany, ponieważ zostałeś ostatnio zabity przez tą samą osobę!";
    public String cannotKillLastVictimToKiller = "&cNie dostałeś rankingu za tego gracza, ponieważ ostatnio go zabiłeś!";
    public String cannotBeKilledByLastKillerToVictim = "&cTwój ranking nie został zabrany, ponieważ zostałeś ostatnio zabity przez tą samą osobę!";
    public String cannotBeKilledByLastKillerToKiller = "&cTen gracz był ostatnio zabity przez ciebie, punkty nie zostają dodane!";
    public String foundMultiAccountToVictim = "&cZnaleziono twoje multi konto, nie zabieram punktów!";
    public String foundMultiAccountToKiller = "&cZnaleziono twoje multi konto, nie dodaję punktów!";
    public String guildCreateYouAlreadyHaveAGuild = "&cPosiadasz już gildię! Nie możesz założyć kolejnej.";
    public String guildCreateGuildCanBeOnlyCreatedOnGameSector = "&cGildię możesz założyć tylko na sektorze grywalnym!";
    public String guildCreateGuildCannotBeCreatedNearBorder = "&cNie możesz stworzyć gildii blisko sektora! Minimalna odległość to: {DISTANCE}";
    public String guildCreateTagIsTooSmall = "&cTag gildii jest za mały! Minimalna ilość znaków to: {MIN_TAG_LENGTH}";
    public String guildCreateTagIsTooBig = "&cTag gildii jest zbyt wielki! Maksymalna ilość znaków to: {MAX_TAG_LENGTH}";
    public String guildCreateNameIsTooSmall = "&cNazwa gildii jest za mała! Minimalna ilość znaków to: {MIN_NAME_LENGTH}";
    public String guildCreateNameIsTooBig = "&Nazwa gildii jest zbyt wielka! Maksymalna ilość znaków to: {MAX_NAME_LENGTH}";
    public String guildCreateNoItemsStart = "&cBrak wymaganych itemów! Wymagana ilość itemów do założenia gildii:";
    public String guildCreateNoItemsFormat = "&d{ITEM_NAME} &8- &d{COUNTED_AMOUNT}&8/&d{REQUIRED_AMOUNT}";
    public String guildCreateNoItemsEnd = "";
    public String guildCreateNameAndTagCannotBeTheSame = "&cNazwa i tag gildii musi się od siebie różnić!";
    public String guildCreateTagIsNotAlphanumeric = "&cTag gildii nie jest alfanumeryczny!";
    public String guildCreateNameIsNotAlphanumeric = "&cNazwa gildii nie jest alfanumeryczny!";
    public String guildCreateThatGuildTagAlreadyExists = "&cPodany tag gildii jest już zajęty!";
    public String guildCreateThatGuildNameAlreadyExists = "&cPodana nazwa gildii jest już zajęta!";
    public String guildCreateGuildCannotBeCreatedNearGuild = "&cNie możesz stworzyć gildii blisko innej gildii!";
    public String guildCreateSuccessBroadcastMessage = "&7Gildia &d{NAME} &8- &d{TAG} &7została założona przez użytkownika &d{PLAYER_NAME}&7!";
    public String guildDeleteYouDontHaveAnyGuild = "&cNie posiadasz żadnej gildii do usunięcia!";
    public String guildDeleteYouAreNotALeader = "&cNie jesteś liderem tej gildii!";
    public String guildDeleteDeletionPrepared = "&7Aby usunąć gildię, wpisz &d/g usuń &7po raz kolejny w ciągu jednej minuty!";
    public String guildDeleteSucceedBroadcast = "&7Gildia &d{NAME} &8- &d{TAG} &7została rozwiązana przez gracza &d{PLAYER_NAME}!";
    public String guildHomeNoGuildFound = "&cNie posiadasz gildii, do której możesz się przeteleportować!";
    public String guildSetHomeNoGuildFound = "&cNie posiadasz gildii, w której możesz ustawić nowy dom!";
    public String guildSetHomeYouCannotManage = "&cNie możesz zarządzać domem gildyjnym ponieważ nie jesteś liderem lub zastępcą!";
    public String guildSetHomeSuccess = "&aPomyślnie ustawiono nową bazę gildyjną!";
    public String guildPvpNoGuildFound = "&cNie posiadasz gildii, w której mógłbyś zmienić status pvp!";
    public String guildPvpCannotChangeStatusInGuild = "&cNie posiadasz uprawnień do zmiany statusu PvP w gildii!";
    public String guildPvpCannotChangeStatusInAlly = "&cNie posiadasz uprawnień do zmiany statusu PvP w sojuszach!";
    public String guildPvpSuccessfullyEnabledInGuild = "&aWłączono pvp w gildii!";
    public String guildPvpSuccessfullyDisabledInGuild = "&cWyłączono pvp w gildii :(";
    public String guildPvpSuccessfullyEnabledInAlly = "&aWłączono pvp w sojuszach!";
    public String guildPvpSuccessfullyDisabledInAlly = "&cWyłączono pvp w sojuszach :(";
    public String guildInviteNoGuildFound = "&cNie posiadasz gildii, do której możesz zaprosić tego gracza.";
    public String guildInviteCannotInvitePlayer = "&cNie posiadasz uprawnień do zapraszania graczy do gildii!";
    public String guildInviteThisUserAlreadyHaveGuild = "&cTen użytkownik posiada już gildię, nie możesz go zaprosić.";
    public String guildInviteCannotInviteWhenGuildIsFull = "&cGildia jest pełna, nie możesz zaprosić tej osoby!";
    public String guildInviteCancelledSender = "&aPomyślnie anulowałeś zaproszenie gracza {PLAYER_NAME} do twojej gildii.";
    public String guildInviteCancelledReceiver = "&cTwoje zaproszenie do gildii {TAG} zostało zanulowane przez użytkownika {PLAYER_NAME}!";
    public String guildInviteSuccessSender = "&aPomyślnie wysłałeś prośbę o dołączenie do gracza {PLAYER_NAME}!";
    public String guildInviteSuccessReceiver = "&7Dostałeś prośbę o dołączenie od gildii &d{TAG} &8- &d{NAME}&7, aby zaakceptować wpisz: &d/g dolacz {TAG}";
    public String guildJoinYouAlreadyHaveGuild = "&cPosiadasz już gildię! Nie możesz dołączyć do innej.";
    public String guildJoinYouDontHaveInviteFromThisGuild = "&cNie posiadasz zaproszenia od tej gildii!";
    public String guildJoinNoFreeSlotWasFound = "&cNie można było dołączyć do tej gildii ponieważ jest pełna!";
    public String guildJoinSuccessSender = "&7Pomyślnie dołączyłeś do gildii &d{TAG} &8- &d{NAME}&7!";
    public String guildJoinSuccessGuild = "&7Gracz &d{PLAYER_NAME} &7dołączył do twojej gildii!";
    public String guildJoinSuccessBroadcast = "&7Gracz &d{PLAYER_NAME} &7dołączył do gildii &d{TAG} &8- &d{NAME}&7!";
    public String guildLeaveYouDontHaveAnyGuild = "&cNie posiadasz żadnej gildii, którą możesz opuścić!";
    public String guildLeaveBadErrorOccurred = "&cWystąpił niespodziewany problem podczas próby opuszczania gildii, zgłoś go do administracaji (GUILD_MEMBER_OBJECT_NOT_FOUND)";
    public String guildLeaveYouCannotLeaveAsLeader = "&cNie możesz opuścić gildii będąc liderem!";
    public String guildLeaveSuccessSender = "&7Pomyślnie opuściłeś gildię &d{TAG} &8- &d{NAME}";
    public String guildLeaveSuccessGuild = "&7Gracz &d{PLAYER_NAME} &7opuścił twoją gildię!";
    public String guildLeaveSuccessBroadcast = "&7Gracz &d{PLAYER_NAME} &7opuścił gildię &d{TAG} &8- &d{NAME}&7!";
    public String guildKickYouDontHaveAnyGuild = "&cNie posiadasz żadnej gildii, z której możesz wyrzucić tego gracza!";
    public String guildKickTargetDontHaveAnyGuild = "&cPodany gracz nie posiada żadnej gildii!";
    public String guildKickTargetIsNotInYourGuild = "&cPodany gracz nie jest w twojej gildii!";
    public String guildKickYouCannotKickAnyone = "&cNie posiadasz uprawnień do wyrzucania kogokolwiek z gildii!";
    public String guildKickBadErrorOccurred = "&cWystąpił niespodziewany problem podczas próby wyrzucenia graczy gildii, zgłoś go do administracaji (GUILD_MEMBER_OBJECT_NOT_FOUND)";
    public String guildKickYouCannotKickYourself = "&cNie możesz wyrzucić samego siebie z gildii!";
    public String guildKickYouCannotKickLeader = "&cNie możesz wyrzucić lidera z gildii!";
    public String guildKickSuccessSender = "&7Pomyślnie wyrzuciłeś gracza &d{PLAYER_NAME} &7z twojej gildii!";
    public String guildKickSuccessReceiver = "&7Zostałeś wyrzucony z gildii &d{TAG} &8- &d{TAG} &7przez użytkownika &d{PLAYER_NAME}&7!";
    public String guildKickSuccessBroadcast = "&7Gracz &d{RECEIVER_PLAYER_NAME} &7został wyrzucony z gildii &d{TAG} &8- &d{NAME} &7przez gracza &d{SENDER_PLAYER_NAME}&7!";
    public String guildEnlargeYouDontHaveAnyGuild = "&cNie posiadasz żadnej gildii, której teren może zostać powiększony!";
    public String guildEnlargeYouCannotEnlarge = "&cNie możesz powiększyć terenu gildii ponieważ nie masz do tego uprawnień!";
    public String guildEnlargeGuildHasMaximumSize = "&cGildia posiada maksymalną wielkość, nie możesz jej powiększyć!";
    public String guildEnlargeAnotherGuildIsNearYour = "&cInna gildia jest blisko twojej, nie możesz jej powiększyć!";
    public String guildEnlargeNoRequiredItem = "&cAby powiększyć teren gildii, potrzebujesz Diamentx{AMOUNT_NEEDED}!";
    public String guildEnlargeSuccessSender = "&7Pomyślnie powiększyłeś gildię do nowego rozmiaru! &8(&d{SIZE}&7x&d{SIZE}&8)";
    public String guildEnlargeSuccessGuild = "&7Gildia została powiększona do rozmiaru &d{SIZE}&7x&d{SIZE} &7przez gracza &d{PLAYER_NAME}&7!";
    public String guildValidityYouDontHaveAnyGuild = "&cNie posiadasz żadnej gildii, którą możesz przedłużyć!";
    public String guildValidityYouCannotExtendTime = "&cNie możesz przedłużyć ważności gildii ponieważ nie masz do tego uprawnień!";
    public String guildValidityWhenYouCanExtendTime = "&cNastępny raz gildię możesz przedłużyć za: {TIME}!";
    public String guildValidityMaxTimeExceed = "&cOsiągnąłeś maksymalny okres przedłużenia gildii! (7d)";
    public String guildValidityNoRequiredItem = "&cAby przedłużyć ważność gildii, potrzebujesz Emeraldx{AMOUNT_NEEDED}!";
    public String guildValiditySuccessSender = "&7Pomyślnie przedłużyłeś gildię do &d{TIME}&7! &8(&d{DATE}&8)";
    public String guildValiditySuccessGuild = "&7Gildia została przedłużona do &d{TIME} &8(&d{DATE}&8) &7przez gracza &d{PLAYER_NAME}&7!";
    public String guildAllyYouDontHaveAnyGuild = "&cNie posiadasz żadnej gildii, aby zawrzeć sojusz z inną!";
    public String guildAllyYouCannotInviteYourOwnGuild = "&cNie możesz wysłać zaproszenia do sojuszu z własną gildią!";
    public String guildAllyYouCannotInviteOtherGuild = "&cNie możesz wysłać zaproszenia do sojuszu z inną gildią, ponieważ nie masz uprawnień!";
    public String guildAllyYouAlreadyHaveOtherAlly = "&cPosiadasz już sojusz z inną gildią, nie możesz wysłać drugiego!";
    public String guildAllyTargetAlreadyHaveOtherAlly = "&cPodana gildia posiada już sojusz z inną gildią, nie możesz wysłać drugiego!";
    public String guildAllyInviteCancelledSender = "&7Pomyślnie cofnięto zaproszenie do sojuszu z gildią &d{TAG} &8- &d{NAME}&7!";
    public String guildAllyInviteCancelledReceiver = "&7Gildia &d{TAG} &8- &d{NAME} &7cofnęła zaproszenie do sojuszu z twoją gildią!";
    public String guildAllySentSender = "&7Pomyślnie wysłałeś zaproszenie do sojuszu z gildią &d{TAG} &8- &d{NAME}&7!";
    public String guildAllySentGuildSender = "&7Twoja gildia wysłała zaproszenie do sojuszu z &d{TAG} &8- &d{NAME}&7!";
    public String guildAllySentGuildReceiver =
        "&7Twoja gildia otrzymała zaproszenie do sojuszu z gildią &d{TAG} &8- &d{NAME}&7!\n"
            + "&7Aby zaakceptować, wpisz: &d/g sojusz {TAG}";
    public String guildAllySuccessSender = "&7Pomyślnie zawarłeś sojusz z gildią &d{TAG} &8- &d{NAME}&7!";
    public String guildAllySuccessGuildSender = "&7Twoja gildia zawarła sojusz z gildią &d{TAG} &8- &d{NAME}&7!";
    public String guildAllySuccessGuildReceiver = "&7Gildia &d{TAG} &8- &d{NAME} &7zawarła sojusz z twoją gildią!";
    public String guildAllySuccessBroadcast = "&7Gildia &d{FIRST_TAG} &8- &d{FIRST_NAME} &7zawarła sojusz z gildią &d{SECOND_TAG} &8- &d{SECOND_NAME}&7!";
    public String guildBreakYouDontHaveAnyGuild = "&cNie posiadasz żadnej gildii, aby zerwać sojusz z inną!";
    public String guildBreakYouCannotBreakAllyWithYourOwnGuild = "&cNie możesz zerwać sojuszu z własną gildią!";
    public String guildBreakYouCannotBreakAllyWithOtherGuild = "&cNie możesz zerwać sojuszu z inną gildią, ponieważ nie posiadasz uprawnień!";
    public String guildBreakYouDontHaveAllyWithThisGuild = "&cNie posiadasz sojuszu z tą gildią! Nie możesz go zerwać.";
    public String guildBreakSuccessSender = "&7Pomyślnie zerwałeś sojusz z gildią &d{TAG} &8- &d{NAME}&7!";
    public String guildBreakSuccessGuildSender = "&7Twoja gildia zerwała sojusz z gildią &d{TAG} &8- &d{NAME}&7!";
    public String guildBreakSuccessGuildReceiver = "&7Gildia &d{TAG} &8- &d{NAME} &7zerwała sojusz z twoją gildią!";
    public String guildBreakSuccessBroadcast = "&7Gildia &d{FIRST_TAG} &8- &d{FIRST_NAME} &7zerwała sojusz z gildią &d{SECOND_TAG} &8- &d{SECOND_NAME}&7!";
    public String guildAlertYouAreCooldowned = "&cNastępny raz powiadomienie możesz wysłać za: {LEFT_TIME}!";
    public String guildAlertYouDontHaveAnyGuild = "&cNie posiadasz żadnej gildii, w której możesz wysłać powiadomienie!";
    public String guildAlertYouCannotSendAlert = "&cNie możesz wysłać powiadomienia, ponieważ nie posiadasz uprawnień!";
    public String guildAlertTitle = "&8[&d&lALERT GILDYJNY&8]";
    public String guildSetJoinAlertYouDontHaveAnyGuild = "&cNie posiadasz żadnej gildii, w której mógłbyś ustawić powiadomienie po wejściu!";
    public String guildSetJoinAlertYouCantSetAlert = "&cNie możesz ustawić powiadomienia po wejściu, ponieważ nie posiadasz uprawnień!";
    public String guildSetJoinAlertSetSuccess = "&7Pomyślnie ustawiłeś powiadomienie gildyjne po wejściu na serwer na: &d{ALERT_MESSAGE}&7!";
    public String guildSetJoinAlertRemoveSuccess = "&7Pomyślnie usunąłeś powiadomienie gildyjne po wejściu na serwer!";
    public String guildNeedHelpYouDontHaveAnyGuild = "&cNie masz żadnej gildii, w której możesz wysłać prośbę o pomoc!";
    public String guildNeedHelpIsCooldownedGuild = "&cKolejny raz prośbę o pomoc do gildii możesz wysłać za: {LEFT_TIME}";
    public String guildNeedHelpIsCooldownedAlly = "&cKolejny raz prośbę o pomoc do sojuszy możesz wysłać za: {LEFT_TIME}";
    public String guildNeedHelpTooManyHelpsGuild = "&cGildia posiada maksymalną ilość próśb o pomoc! (10";
    public String guildNeedHelpTooManyHelpsAlly = "&cGildia posiada maksymalną ilość próśb o pomoc sojuszniczą! (5)";
    public String guildNeedHelpRequestSuccessGuild = "&aPomyślnie wysłałeś prośbę o pomoc do gildii!";
    public String guildNeedHelpRequestSuccessAlly = "&aPomyślnie wysłałeś prośbę o pomoc do sojuszy!";
    public String guildExpiryBroadcast = "&7Gildia &d{TAG} &8- &d{NAME} &7wygasła! Jej koordynaty: X: &d{X}&7, Y: &d{Y}&7, Z: &d{Z}";
    public String guildHelpWaypointTitle = "{PLAYER_NAME} potrzebuje pomocy! X: {X}, Y: {Y}, Z: {Z}";
    public TitleMessageWrapper killTitleWithoutGuild = createTitleMessageWrapper(
        "&aZabójstwo!", "&7Zabiłeś &d{PLAYER_NAME} +&7(&d{POINTS}&8)",
        0, 20, 30
    );
    public TitleMessageWrapper killTitleWithGuild = createTitleMessageWrapper(
        "&aZabójstwo!", "&7Zabiłeś &7[&d{TAG}&7] &d{PLAYER_NAME} +&7(&d{POINTS}&8)",
        0, 20, 30
    );
    public String guildPistonScannerPreparingActionBarMessage = "&aSkaner pistonów jest właśnie preparowany...";
    public String guildPistonScannerUpdatingActionBarMessage =
        "&7Limit pistonów na terenie gildii jest aktualnie &dodświeżany&7!"
            + "\n&7Progres: &d{PROCESSED_BLOCKS}&8/&d{MAX_BLOCKS} &8(&d{PERCENTAGE}%&8)";
    public String nameTagEnemyGuildPrefix = "&7[&c{TAG}&7]";
    public String nameTagYourGuildPrefix = "&7[&a{TAG}&7]";
    public String nameTagAllyGuildPrefix = "&7[&b{TAG}&7]";
    public TitleMessageWrapper guildWillSoonExpireJoinTitle = createTitleMessageWrapper(
        "&cGildia wygasa",
        "&cTwoja gildia wygasa za {TIME}, opłać ją aby jej nie stracić!",
        0, 100, 20
    );
    @SerializedName("top_user_rank_format")
    public Map<Integer, String> topUserRankFormatMap = new LinkedHashMap<Integer, String>() {{
      for (int index = 0; index < 16; index++) {
        this.put(index, "&f{PLAYER_NAME} &8[&d{POINTS}] {ONLINE_COLOR}●");
      }
    }};
    @SerializedName("top_guild_rank_format")
    public Map<Integer, String> topGuildRankFormatMap = new LinkedHashMap<Integer, String>() {{
      for (int index = 0; index < 16; index++) {
        this.put(index, "&f{GUILD_TAG} &8[&d{POINTS}]");
      }
    }};

    public String guildInfo = "";
    public List<String> guildHelp = Arrays.asList(
        "&7tutaj bedzie",
        "&apomoc gildyjna"
    );
    public String guildGolemTitleEnemy = "&8[&c{TAG}&8] &8(&d{HEALTH} &4❤&8)";
    public String guildGolemTitleYour = "&8[&a{TAG}&8] &8(&d{HEALTH} &4❤&8)";
    public String guildGolemTitleAlly = "&8[&9{TAG}&8] &8(&d{HEALTH} &4❤&8)";
    public String guildHeadTitleEnemy = "&8[&c{TAG}&8]";
    public String guildHeadTitleYour = "&8[&a{TAG}&8]";
    public String guildHeadTitleAlly = "&8[&9{TAG}&8]";
    public String guildChatFormatGuild = "&2&l[DO GILDII] &a{PLAYER_NAME}: {MESSAGE}";
    public String guildChatFormatAlly = "&6&l[DO SOJUSZU] &b{PLAYER_NAME}: {MESSAGE}";
    public String youCannotPlaceBlocksOnEnemyGuild = "&cNie możesz kłaść bloków na terenie wrogiej gildii!";
    public String youCannotPlaceBlocksInCenter = "&cNie możesz stawiać bloków w środku gildii!";
    public String youCannotPlacePistonsWhenPistonScannerIsEnabled = "&cNie możesz stawiać pistonów gdy praca skanera jest aktywna!";
    public String youCannotPlacePistonsBecauseLimitHasBeenReached = "&cNie możesz stawiać teraz pistonów ponieważ przekroczyłeś ich limit!";
    public String youCannotPlaceBlocksNowBecauseTntExploded = "&cNie możesz stawiać teraz bloków ponieważ tnt wybuchło!";
    public String youCannotPlaceBlocksBecauseBadErrorOccurred = "&cNie możesz kłaść bloków ponieważ wystąpił niespodziewany problem z twoim kontem!";
    public String youCannotPlaceBlocksOnThisGuildBecauseYouDontHavePermission = "&cNie masz uprawnień do stawiania bloków na terenie tej gildii!";
    public String youCannotPlaceCoalOnThisGuildBecauseYouDontHavePermission = "&cNie masz uprawnień do stawiania węgla na terenie tej gildii!";
    public String youCannotPlaceLapisOnThisGuildBecauseYouDontHavePermission = "&cNie masz uprawnień do stawiania lapisu na terenie tej gildii!";
    public String youCannotPlaceObsidianOnThisGuildBecauseYouDontHavePermission = "&cNie masz uprawnień do stawiania obsydianu na terenie tej gildii!";
    public String youCannotPlaceSandOnThisGuildBecauseYouDontHavePermission = "&cNie masz uprawnień do stawiania piasku na terenie tej gildii!";
    public String youCannotPlaceRedStoneOnThisGuildBecauseYouDontHavePermission = "&cNie masz uprawnień do stawiania rzeczy związanych z redstonem na terenie tej gildii!";
    public String youCannotPlaceAnvilOnThisGuildBecauseYouDontHavePermission = "&cNie masz uprawnień do stawiania kowadeł na terenie tej gildii!";
    public String youCannotPlaceTntOnThisGuildBecauseYouDontHavePermission = "&cNie masz uprawnień do stawiania kowadeł na terenie tej gildii!";
    public String youCannotBreakBlocksOnEnemyGuild = "&cNie możesz niszczyć bloków na terenie wrogiej gildii!";
    public String youCannotBreakBlocksInCenter = "&cNie możesz niszczyć bloków w środku gildii!";
    public String youCannotBreakPistonsWhenPistonScannerIsEnabled = "&cNie możesz niszczyć pistonów gdy praca skanera jest aktywna!";
    public String youCannotBreakBlocksNowBecauseTntExploded = "&cNie możesz niszczyć teraz bloków ponieważ tnt wybuchło!";
    public String youCannotBreakBlocksBecauseBadErrorOccurred = "&cNie możesz niszczyć bloków ponieważ wystąpił niespodziewany problem z twoim kontem!";
    public String youCannotBreakBlocksBecauseYouDontHavePermission = "&cNie masz uprawnień do niszczenia bloków na terenie tej gildii!";
    public String youCannotEmptyBucketsOnEnemyGuild = "&cNie możesz rozlewać rzeczy na terenie wrogiej gildii!";
    public String youCannotEmptyBucketBecauseBadErrorOccurred = "&cNie możesz rozlewać rzeczy ponieważ wystąpił niespodziewany problem z twoim kontem!";
    public String youCannotEmptyWaterBecauseYouDontHavePermission = "&cNie masz uprawnien do rozlewania wody na terenie tej gildii!";
    public String youCannotEmptyLavaBecauseYouDontHavePermission = "&cNie masz uprawnien do rozlewania lawy na terenie tej gildii!";
    public String youCannotFillBucketsOnEnemyGuild = "&cNie możesz zabierać wody/lawy na terenie wrogiej gildii!";
    public String waterWillBeVanishedSoon = "&aWoda, którą postawiłeś zniknie za 5s!";
    public String waterWasSuccessfullyBorrowed = "&aPomyślnie zebrałeś wodę!";
    public String youCannotUseTeleportAcceptBecauseBadErrorOccured = "&cNie możesz używać komendy /tpaccept poniewaz wystąpił niespodziewany problem z twoim kontem!";
    public String youCannotUseTeleportAcceptOnThisGuildBecauseYouDontHavePermission = "&cNie masz uprawnień do używania komendy /tpaccept na terenie tej gildii!";
    public String youCannotPlaceBlocksOnThisYWhileInCombat = "&cNie możesz kłaść bloków na tym poziomie Y podczas PVP! Maksymalna wysokość to: {MAX_BLOCK_Y}";
    public String youCannotPlaceBlocksWhileInCombat = "&cNie możesz kłaść bloków podczas PVP!";
    public String youCannotOpenChestsBecauseBadErrorOccured = "&cNie możesz otwierać skrzynek ponieważ wystąpił niespodziewany problem z twoim kontem!";
    public String youCannotOpenFurnacesBecauseBadErrorOccured = "&cNie możesz otwierać piecyków ponieważ wystąpił niespodziewany problem z twoim kontem!";
    public String youCannotOpenChestsBecauseYouDontHavePermission = "&cNie masz uprawnień do otwierania skrzynek na terenie tej gildii!";
    public String youCannotOpenFurnacesBecauseYouDontHavePermission = "&cNie masz uprawnień do otwierania piecyków na terenie tej gildii!";
    public String tntHasJustExplodedOnYourTerrain = "&cNa twoim terenie wybuchło TNT! Nie możesz budować przez: 2m";
    public String youCannotUseThisCommandOnThisTerrain = "&cNie możesz używać tej komendy na terenie wrogiej gildii!";
    public String enemyHadJustEnteredYourGuildTerrain = "&cWróg wkroczył na teren gildii!";
    public TitleMessageWrapper guildEnterTitle = createTitleMessageWrapper(
        "", "&7Wkroczyłeś na teren gildii &8[&d{TAG}&8]",
        0, 20, 10
    ), guildLeaveTitle = createTitleMessageWrapper(
        "", "&7Opuściłeś teren gildii &8[&d{TAG}&8]",
        0, 20, 10
    );
    public BossBarMessageWrapper yourGuildWithTntProtectionBossBarMessage = createBossBarMessageWrapper(
        "&aJesteś na terenie swojej gildii &7[&2{TAG}&7]"
            + "\n&aGildia posiada ochronę przed TNT przez: &2{TIME}",
        BarStyleWrapper.SOLID, BarColorWrapper.GREEN
    ), yourGuildWithoutTntProtectionBossBarMessage = createBossBarMessageWrapper(
        "&aJesteś na terenie swojej gildii &7[&2{TAG}&7]",
        BarStyleWrapper.SOLID, BarColorWrapper.GREEN
    );
    public BossBarMessageWrapper enemyGuildWithTntProtectionBossBarMessage = createBossBarMessageWrapper(
        "&cJesteś na terenie wrogiej gildii &7[&4{TAG}&7]"
            + "\n&cGildia posiada ochronę przed TNT przez: &4{TIME}",
        BarStyleWrapper.SOLID, BarColorWrapper.RED
    ), enemyGuildWithoutTntProtectionBossBarMessage = createBossBarMessageWrapper(
        "&cJesteś na terenie wrogiej gildii &7[&4{TAG}&7]",
        BarStyleWrapper.SOLID, BarColorWrapper.RED
    );
    public BossBarMessageWrapper allyGuildWithTntProtectionBossBarMessage = createBossBarMessageWrapper(
        "&bJesteś na terenie sojuszniczej gildii &7[&9{TAG}&7]"
            + "\n&bGildia posiada ochronę przed TNT przez: &9{TIME}",
        BarStyleWrapper.SOLID, BarColorWrapper.BLUE
    ), allyGuildWithoutTntProtectionBossBarMessage = createBossBarMessageWrapper(
        "&bJesteś na terenie sojuszniczej gildii &7[&9{TAG}&7]",
        BarStyleWrapper.SOLID, BarColorWrapper.BLUE
    );

    private static TitleMessageWrapper createTitleMessageWrapper(final String title,
        final String subTitle, final int fadeIn, final int stay, final int fadeOut) {
      final TitleMessageWrapper wrapper = new TitleMessageWrapper();
      wrapper.title = title;
      wrapper.subTitle = subTitle;
      wrapper.fadeIn = fadeIn;
      wrapper.stay = stay;
      wrapper.fadeOut = fadeOut;
      return wrapper;
    }

    private static BossBarMessageWrapper createBossBarMessageWrapper(final String title,
        final BarStyleWrapper barStyleWrapper, final BarColorWrapper barColorWrapper) {
      final BossBarMessageWrapper wrapper = new BossBarMessageWrapper();
      wrapper.title = title;
      wrapper.barStyleWrapper = barStyleWrapper;
      wrapper.barColorWrapper = barColorWrapper;
      return wrapper;
    }

    public static final class BossBarMessageWrapper {

      public String title;
      public BarStyleWrapper barStyleWrapper;
      public BarColorWrapper barColorWrapper;
    }

    public static final class TitleMessageWrapper {

      public String title, subTitle;
      public int fadeIn, stay, fadeOut;
    }
  }

  public static final class TabListElementsWrapper {

    @SerializedName("elements")
    public Map<Integer, String> elementsMap;
    public String header, footer;
    public String updateTime;
  }
}
