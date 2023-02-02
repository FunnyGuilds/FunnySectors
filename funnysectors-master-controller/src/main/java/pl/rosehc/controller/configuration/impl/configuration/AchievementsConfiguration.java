package pl.rosehc.controller.configuration.impl.configuration;

import com.google.gson.annotations.SerializedName;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import pl.rosehc.controller.configuration.ConfigurationData;
import pl.rosehc.controller.configuration.impl.configuration.AchievementsConfiguration.AchievementRewardWrapper.AchievementBoostTypeWrapper;
import pl.rosehc.controller.configuration.impl.configuration.AchievementsConfiguration.AchievementRewardWrapper.AchievementRewardItemWrapper;
import pl.rosehc.controller.configuration.impl.configuration.AchievementsConfiguration.AchievementWrapper.AchievementTypeWrapper;
import pl.rosehc.controller.configuration.impl.configuration.PlatformConfiguration.CustomItemType;
import pl.rosehc.controller.configuration.impl.configuration.PlatformConfiguration.CustomItemsWrapper;
import pl.rosehc.controller.configuration.impl.configuration.PlatformConfiguration.CustomItemsWrapper.CustomItemWrapper;
import pl.rosehc.controller.wrapper.achievements.AchievementsAchievementPreviewGuiElementWrapper;
import pl.rosehc.controller.wrapper.achievements.AchievementsTypePreviewGuiElementWrapper;
import pl.rosehc.controller.wrapper.achievements.AchievementsUserProfileAllStatisticsGuiElementWrapper;
import pl.rosehc.controller.wrapper.spigot.DefaultSpigotGuiElementWrapper;
import pl.rosehc.controller.wrapper.spigot.SpigotGuiWrapper;

public final class AchievementsConfiguration extends ConfigurationData {

  @SerializedName("inventories")
  public Map<String, SpigotGuiWrapper> inventoryMap = new LinkedHashMap<String, SpigotGuiWrapper>() {{
    this.put("main", createMainGuiWrapper());
    this.put("user_profile", createUserProfileGuiWrapper());
    this.put(AchievementTypeWrapper.EATEN_GOLDEN_HEADS.name().toLowerCase(),
        createTypeGuiWrapper(true));
    this.put(AchievementTypeWrapper.MINING_LEVEL.name().toLowerCase(), createTypeGuiWrapper(false));
  }};
  @SerializedName("achievements")
  public List<AchievementWrapper> achievementWrapperList = Arrays.asList(
      createAchievementWrapper(
          AchievementTypeWrapper.EATEN_GOLDEN_HEADS,
          Arrays.asList(
              createAchievementRewardWrapper(
                  createAchievementRewardItemWrapper(
                      CustomItemsWrapper.createCustomItemWrapper(
                          null,
                          null,
                          null,
                          null,
                          (short) 0,
                          1
                      ),
                      CustomItemType.GHEAD
                  ),
                  null,
                  0
              ),
              createAchievementRewardWrapper(
                  createAchievementRewardItemWrapper(
                      CustomItemsWrapper.createCustomItemWrapper(
                          "DIAMOND",
                          null,
                          null,
                          null,
                          (short) 0,
                          1
                      ),
                      null
                  ),
                  null,
                  0
              )
          ),
          "150",
          1
      ),
      createAchievementWrapper(
          AchievementTypeWrapper.MINING_LEVEL,
          Collections.singletonList(
              createAchievementRewardWrapper(
                  null,
                  AchievementBoostTypeWrapper.DROP_CHANCE,
                  0.25D
              )
          ),
          "25",
          1
      )
  );
  @SerializedName("reward_receive_messages")
  public Map<String, String> rewardReceiveMessageMap = new LinkedHashMap<String, String>() {{
    this.put(AchievementTypeWrapper.EATEN_GOLDEN_HEADS.name() + ":1",
        "Gracz {PLAYER_NAME} odebral osiagniecie ZJEDZONE GHEADY (1) i otrzymal GHEADAx1 + DIAMENTx1!");
    this.put(AchievementTypeWrapper.MINING_LEVEL.name() + ":1",
        "Gracz {PLAYER_NAME} odebral osiagniecie POZIOM KOPANIA (1) i otrzymal boosta szansy! (0.25%)");
  }};

  private static SpigotGuiWrapper createMainGuiWrapper() {
    final SpigotGuiWrapper mainGuiWrapper = new SpigotGuiWrapper();
    final DefaultSpigotGuiElementWrapper fillElementWrapper = new DefaultSpigotGuiElementWrapper();
    final AchievementsTypePreviewGuiElementWrapper killsTypePreviewElementWrapper = new AchievementsTypePreviewGuiElementWrapper(), pointsTypePreviewElementWrapper = new AchievementsTypePreviewGuiElementWrapper();
    final AchievementsTypePreviewGuiElementWrapper killStreakTypePreviewElementWrapper = new AchievementsTypePreviewGuiElementWrapper(), miningLevelTypePreviewElementWrapper = new AchievementsTypePreviewGuiElementWrapper();
    final AchievementsTypePreviewGuiElementWrapper spendTimeTypePreviewElementWrapper = new AchievementsTypePreviewGuiElementWrapper(), eatenGoldenHeadsTypePreviewElementWrapper = new AchievementsTypePreviewGuiElementWrapper();
    final AchievementsTypePreviewGuiElementWrapper traveledKilometersTypePreviewElementWrapper = new AchievementsTypePreviewGuiElementWrapper(), openedMagicCasesTypePreviewElementWrapper = new AchievementsTypePreviewGuiElementWrapper();
    killsTypePreviewElementWrapper.material = "DIAMOND_SWORD";
    killsTypePreviewElementWrapper.name = "&cZABÓJSTWA";
    killsTypePreviewElementWrapper.slot = 10;
    pointsTypePreviewElementWrapper.material = "NETHER_STAR";
    pointsTypePreviewElementWrapper.name = "&eRANKING";
    pointsTypePreviewElementWrapper.slot = 11;
    killStreakTypePreviewElementWrapper.material = "SKULL_ITEM";
    killStreakTypePreviewElementWrapper.name = "&eRANKING";
    killStreakTypePreviewElementWrapper.slot = 12;
    killStreakTypePreviewElementWrapper.data = 2;
    miningLevelTypePreviewElementWrapper.material = "DIAMOND_PICKAXE";
    miningLevelTypePreviewElementWrapper.name = "&5LEVEL KOPANIA";
    miningLevelTypePreviewElementWrapper.slot = 13;
    spendTimeTypePreviewElementWrapper.material = "WATCH";
    spendTimeTypePreviewElementWrapper.name = "&9SPĘDZONY CZAS";
    spendTimeTypePreviewElementWrapper.slot = 14;
    eatenGoldenHeadsTypePreviewElementWrapper.material = "GHEAD";
    eatenGoldenHeadsTypePreviewElementWrapper.name = "&6ZJEDZONE GHEADY";
    eatenGoldenHeadsTypePreviewElementWrapper.slot = 15;
    traveledKilometersTypePreviewElementWrapper.material = "COMPASS";
    traveledKilometersTypePreviewElementWrapper.name = "&dPRZEBIEGNIĘTE KILOMETRY";
    traveledKilometersTypePreviewElementWrapper.slot = 16;
    openedMagicCasesTypePreviewElementWrapper.material = "MAGIC_CASE";
    openedMagicCasesTypePreviewElementWrapper.name = "&dPRZEBIEGNIĘTE KILOMETRY";
    openedMagicCasesTypePreviewElementWrapper.slot = 22;
    //noinspection DuplicatedCode
    fillElementWrapper.material = "STAINED_GLASS_PANE";
    fillElementWrapper.name = "&8#";
    fillElementWrapper.data = 15;
    mainGuiWrapper.inventoryName = "&dOsiągnięcia";
    mainGuiWrapper.inventorySize = 27;
    mainGuiWrapper.fillElement = fillElementWrapper;
    mainGuiWrapper.elements = new LinkedHashMap<>();
    mainGuiWrapper.elements.put("kills", killStreakTypePreviewElementWrapper);
    mainGuiWrapper.elements.put("points", pointsTypePreviewElementWrapper);
    mainGuiWrapper.elements.put("killstreak", killStreakTypePreviewElementWrapper);
    mainGuiWrapper.elements.put("mining_level", miningLevelTypePreviewElementWrapper);
    mainGuiWrapper.elements.put("spend_time", spendTimeTypePreviewElementWrapper);
    mainGuiWrapper.elements.put("eaten_golden_heads", eatenGoldenHeadsTypePreviewElementWrapper);
    mainGuiWrapper.elements.put("traveled_kilometers", traveledKilometersTypePreviewElementWrapper);
    mainGuiWrapper.elements.put("opened_magic_cases", openedMagicCasesTypePreviewElementWrapper);
    return mainGuiWrapper;
  }

  private static SpigotGuiWrapper createUserProfileGuiWrapper() {
    final SpigotGuiWrapper userProfileGuiWrapper = new SpigotGuiWrapper();
    final DefaultSpigotGuiElementWrapper fillElementWrapper = new DefaultSpigotGuiElementWrapper();
    final AchievementsUserProfileAllStatisticsGuiElementWrapper allStatisticsElementWrapper = new AchievementsUserProfileAllStatisticsGuiElementWrapper();
    final DefaultSpigotGuiElementWrapper currentGuildElementWrapper = new DefaultSpigotGuiElementWrapper(), allBoostsElementWrapper = new DefaultSpigotGuiElementWrapper();
    allStatisticsElementWrapper.material = "CURRENT_PLAYER_HEAD";
    allStatisticsElementWrapper.name = "&5Wszystkie statystyki";
    allStatisticsElementWrapper.lore = Arrays.asList(
        "&7Zabójstwa: &d{KILLS}",
        "&7Śmierci: &d{DEATHS}",
        "&7Kill Streak: &d{KILL_STREAK}",
        "&7KD Ratio: &d{KDR}",
        "&7Spędzony czas na serwerze: &d{SPEND_TIME}",
        "&7Level kopania: &d{MINING_LEVEL}"
    );
    allStatisticsElementWrapper.slot = 11;
    currentGuildElementWrapper.material = "SKULL_ITEM";
    currentGuildElementWrapper.skinValue = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTZmYjliM2UzZjY1MGI3YjI1OGMwNGZmY2I4NWMxYjVkY2FjOTJiODFlNTJkOTkyYzUxMjRiNjcwZmU4ZDYifX19";
    currentGuildElementWrapper.skinSignature = "";
    currentGuildElementWrapper.name = "&cAktualna gildia";
    currentGuildElementWrapper.lore = Arrays.asList(
        "&7Tag aktualnej gildii: &a{TAG}",
        "&7Nazwa aktualnej gildii: &a{NAME}"
    );
    currentGuildElementWrapper.slot = 15;
    allBoostsElementWrapper.material = "POTION";
    allBoostsElementWrapper.name = "&eWszystkie boosty";
    allBoostsElementWrapper.lore = Arrays.asList(
        "&7Dodatkowy ranking: &d{ADDITIONAL_RANKING}",
        "&7Mniejsza strata rankingu: &d{LESS_RANKING_LOSS}",
        "&7Dodatkowa szansa do dropu: &d{ADDITIONAL_DROP_CHANCE}"
    );
    allBoostsElementWrapper.data = 8265;
    allBoostsElementWrapper.slot = 13;
    //noinspection DuplicatedCode
    fillElementWrapper.material = "STAINED_GLASS_PANE";
    fillElementWrapper.name = "&8#";
    fillElementWrapper.data = 15;
    userProfileGuiWrapper.fillElement = fillElementWrapper;
    userProfileGuiWrapper.elements = new LinkedHashMap<>();
    userProfileGuiWrapper.elements.put("all_statistics", allStatisticsElementWrapper);
    userProfileGuiWrapper.elements.put("current_guild", currentGuildElementWrapper);
    userProfileGuiWrapper.elements.put("all_boosts", allBoostsElementWrapper);
    return userProfileGuiWrapper;
  }

  private static SpigotGuiWrapper createTypeGuiWrapper(final boolean eatenGoldenHeads) {
    final SpigotGuiWrapper typeGuiWrapper = new SpigotGuiWrapper();
    final DefaultSpigotGuiElementWrapper fillElementWrapper = new DefaultSpigotGuiElementWrapper();
    final AchievementsAchievementPreviewGuiElementWrapper achievementPreviewElementWrapper = new AchievementsAchievementPreviewGuiElementWrapper();
    final DefaultSpigotGuiElementWrapper backElementWrapper = new DefaultSpigotGuiElementWrapper();
    backElementWrapper.name = "&cKliknij, aby powrócić do menu głównego!";
    backElementWrapper.material = "BARRIER";
    backElementWrapper.slot = 26;
    achievementPreviewElementWrapper.material = eatenGoldenHeads ? "GHEAD" : "DIAMOND_PICKAXE";
    achievementPreviewElementWrapper.name =
        eatenGoldenHeads ? "&6ZJEDZONE GHEADY #1" : "&5LEVEL KOPANIA #1";
    achievementPreviewElementWrapper.lore = Arrays.asList(
        "&7Czy odebrałeś: &r{RECEIVED}",
        "&7Progres: &d{CURRENT_STATISTIC}/{MAX_STATISTIC} &8(&r{PROGRESS_BAR}&8)",
        "&aKliknij, aby odebrać nagrodę!"
    );
    achievementPreviewElementWrapper.slot = 14;
    //noinspection DuplicatedCode
    fillElementWrapper.material = "STAINED_GLASS_PANE";
    fillElementWrapper.name = "&8#";
    fillElementWrapper.data = 15;
    typeGuiWrapper.inventoryName =
        "&dOsiągnięcia: " + (eatenGoldenHeads ? "ZJEDZONE GHEADY" : "LEVEL KOPANIA");
    typeGuiWrapper.inventorySize = 27;
    typeGuiWrapper.fillElement = fillElementWrapper;
    typeGuiWrapper.elements = new LinkedHashMap<>();
    typeGuiWrapper.elements.put("level1", achievementPreviewElementWrapper);
    typeGuiWrapper.elements.put("back", backElementWrapper);
    return typeGuiWrapper;
  }

  private static AchievementWrapper createAchievementWrapper(
      final AchievementTypeWrapper achievementTypeWrapper,
      final List<AchievementRewardWrapper> rewardList, final String requiredStatistics,
      final int level) {
    final AchievementWrapper wrapper = new AchievementWrapper();
    wrapper.achievementTypeWrapper = achievementTypeWrapper;
    wrapper.rewardList = rewardList;
    wrapper.requiredStatistics = requiredStatistics;
    wrapper.level = level;
    return wrapper;
  }

  private static AchievementRewardWrapper createAchievementRewardWrapper(
      final AchievementRewardItemWrapper rewardItemWrapper,
      final AchievementBoostTypeWrapper boostTypeWrapper, final double boostAmount) {
    final AchievementRewardWrapper wrapper = new AchievementRewardWrapper();
    wrapper.rewardItemWrapper = rewardItemWrapper;
    wrapper.boostTypeWrapper = boostTypeWrapper;
    wrapper.boostAmount = boostAmount;
    return wrapper;
  }

  private static AchievementRewardItemWrapper createAchievementRewardItemWrapper(
      final CustomItemWrapper normalItemWrapper, final CustomItemType customItemType) {
    final AchievementRewardItemWrapper wrapper = new AchievementRewardItemWrapper();
    wrapper.normalItemWrapper = normalItemWrapper;
    wrapper.customItemType = customItemType;
    return wrapper;
  }

  public static final class AchievementRewardWrapper {

    @SerializedName("item")
    public AchievementRewardItemWrapper rewardItemWrapper;
    @SerializedName("boost_type")
    public AchievementBoostTypeWrapper boostTypeWrapper;
    @SerializedName("boost_amount")
    public double boostAmount;

    public enum AchievementBoostTypeWrapper {

      KILL_POINTS, KILL_AND_DEATH_POINTS,
      DROP_CHANCE
    }

    public static final class AchievementRewardItemWrapper {

      @SerializedName("normal_item")
      public CustomItemWrapper normalItemWrapper;
      @SerializedName("custom_item")
      public CustomItemType customItemType;
    }
  }

  public static final class AchievementWrapper {

    @SerializedName("type")
    public AchievementTypeWrapper achievementTypeWrapper;
    @SerializedName("rewards")
    public List<AchievementRewardWrapper> rewardList;
    public String requiredStatistics;
    public int level;

    @SuppressWarnings("SpellCheckingInspection")
    public enum AchievementTypeWrapper {

      KILLS, POINTS, KILLSTREAK,
      MINING_LEVEL, SPEND_TIME, EATEN_GOLDEN_HEADS,
      TRAVELED_KILOMETERS, OPENED_MAGIC_CASES
    }
  }
}
