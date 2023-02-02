package pl.rosehc.sectors;

import com.google.gson.annotations.SerializedName;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import pl.rosehc.adapter.configuration.ConfigurationData;
import pl.rosehc.controller.wrapper.global.BarColorWrapper;
import pl.rosehc.controller.wrapper.global.BarStyleWrapper;
import pl.rosehc.controller.wrapper.sector.SectorWrapper;
import pl.rosehc.sectors.sector.SectorType;

public final class SectorsConfiguration extends ConfigurationData {

  @SerializedName("sectors")
  public Map<String, SectorWrapper> sectorMap = new LinkedHashMap<String, SectorWrapper>() {{
    for (int index = 0; index < 3; index++) {
      this.put("spawn_" + (index + 1), createSectorWrapper(SectorType.SPAWN, -100, 100, -100, 100));
    }

    this.put("s1", createSectorWrapper(SectorType.GAME, -100, 1000, 100, 1000));
    this.put("w1", createSectorWrapper(SectorType.GAME, -1000, -100, -100, 1000));
    this.put("e1", createSectorWrapper(SectorType.GAME, 100, 1000, -1000, 100));
    this.put("n1", createSectorWrapper(SectorType.GAME, -1000, 100, -1000, -100));
  }};
  @SerializedName("border_size")
  public int borderSize = 1000;

  @SerializedName("proxies")
  public List<Integer> proxyList = Arrays.asList(1, 2);

  @SerializedName("messages")
  public MessagesWrapper messagesWrapper = new MessagesWrapper();
  @SerializedName("border_bar_style")
  public BarStyleWrapper borderBarStyleWrapper = BarStyleWrapper.SEGMENTED_10;
  @SerializedName("border_bar_color")
  public BarColorWrapper borderBarColorWrapper = BarColorWrapper.PINK;

  private static SectorWrapper createSectorWrapper(SectorType type, int minX, int maxX, int minZ,
      int maxZ) {
    SectorWrapper wrapper = new SectorWrapper();
    wrapper.type = type;
    wrapper.minX = minX;
    wrapper.maxX = maxX;
    wrapper.minZ = minZ;
    wrapper.maxZ = maxZ;
    return wrapper;
  }

  public static final class MessagesWrapper {

    public String sectorIsOffline = "&cSektor, na który chcesz się połączyć jest aktualnie offline!";
    public String sectorIsHeavilyLoaded = "&cSektor, na który chcesz się połączyć jest aktualnie przeciążony";
    public String cannotSynchronizeYourData = "&cWystapił niespodziewany problem podczas próby synchronizacji twoich danych.";
    public String connectingInfo = "&aPomyślnie zsynchronizowano twoje dane! Łączę z sektorem {SECTOR_NAME}";
    public String sectorNotFound = "&cNie odnaleziono sektora na tej lokalizacji.";
    public String cannotBreakBlocksOnSpawn = "&cNie możesz niszczyć bloków na spawnie!";
    public String cannotBreakBlocksInEnd = "&cNie możesz niszczyć bloków w endzie!";
    public String cannotBreakBlocksNearSector = "&cNie możesz niszczyć bloków przy granicy sektora!";
    public String cannotPlaceBlocksOnSpawn = "&cNie możesz stawiać bloków na spawnie!";
    public String cannotPlaceBlocksInEnd = "&cNie możesz stawiać bloków w endzie!";
    public String cannotPlaceBlocksNearSector = "&cNie możesz stawiać bloków przy granicy sektora!";
    public String cannotFillTheBucketOnSpawn = "&cNie możesz napełniać wiaderka na spawnie!";
    public String cannotFillTheBucketNearSector = "&cNie możesz napełniać wiaderka przy granicy sektora!";
    public String cannotEmptyTheBucketOnSpawn = "&cNie możesz opróżniać wiaderka na spawnie!";
    public String cannotEmptyTheBucketNearSector = "&cNie możesz opróżniać wiaderka przy granicy sektora!";
    public String playerIsAlreadyOnlineOnThisSector = "&cPodany gracz jest już online na tym sektorze!";
    public String playerProfileNotFound = "&cNie posiadasz profilu.";
    public String nearSectorBossBarTitle = "&dZbliżasz się do granicy sektora &8| &7{DISTANCE}m";
  }
}
