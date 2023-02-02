package pl.rosehc.controller.configuration.impl.configuration;

import com.google.gson.annotations.SerializedName;
import pl.rosehc.controller.configuration.ConfigurationData;

public final class LinkerRandomTPConfiguration extends ConfigurationData {

  @SerializedName("group_messages")
  public GroupMessagesWrapper groupMessagesWrapper = new GroupMessagesWrapper();
  @SerializedName("solo_messages")
  public SoloMessagesWrapper soloMessagesWrapper = new SoloMessagesWrapper();
  public int groupTeleportRadius = 5;

  public static final class GroupMessagesWrapper {

    public String youNeedToBePlate = "&cMusisz stać na półpłytce!";
    public String cannotTeleportByYourself = "&cNie możesz się teleportować samemu!";
    public String noGroupTeleportsSectorFound = "&cNie znaleziono żadnego wolnego sektora teparkowego!";
    public String noGuildSectorFound = "&cNie znaleziono żadnego wolnego sektora gildyjnego!";
    public String noFreeArenasFound = "&cBrak wolnych aren na sektorze {SECTOR_NAME}!";
    public String teleportationSucceedArena = "&7Pomyślnie przeteleportowałeś się na sektor &d{SECTOR_NAME} &7razem z graczem &d{PLAYER_NAME}&7!";
    public String teleportationSucceedNormal = "&7Pomyślnie przeteleportowano cię na sektor &d{SECTOR_NAME} &7z graczem &d{PLAYER_NAME}&7! &8(&7X: &d{X}, &7Y: &d{Y}, &7Z: &d{Z})";
  }

  public static final class SoloMessagesWrapper {

    public String noGuildSectorFound = "&cNie znaleziono żadnego wolnego sektora gildyjnego!";
    public String noFreeLocationWasFound = "&cNie można było znaleźć żadnej wolnej lokacji na sektorze {SECTOR_NAME}.";
    public String teleportationSucceed = "&7Pomyślnie przeteleportowano cię na sektor &d{SECTOR_NAME}&7! &8(&7X: &d{X}, &7Y: &d{Y}, &7Z: &d{Z})";
  }
}
