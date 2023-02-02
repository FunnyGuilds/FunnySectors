package pl.rosehc.sectors;

import com.google.gson.annotations.SerializedName;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import pl.rosehc.adapter.configuration.ConfigurationData;
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

  @SerializedName("proxies")
  public List<Integer> proxyList = Arrays.asList(1, 2);

  @SerializedName("messages")
  public MessagesWrapper messagesWrapper = new MessagesWrapper();

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

  public static final class SectorWrapper {

    public SectorType type;
    public int minX, maxX;
    public int minZ, maxZ;
  }

  public static final class MessagesWrapper {

    public String playerIsAlreadyOnline = "&cGracz o tym nicku jest już online!";
  }
}
