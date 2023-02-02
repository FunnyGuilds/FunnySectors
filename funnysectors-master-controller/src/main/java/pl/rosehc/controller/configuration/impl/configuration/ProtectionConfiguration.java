package pl.rosehc.controller.configuration.impl.configuration;

import com.google.gson.annotations.SerializedName;
import pl.rosehc.controller.configuration.ConfigurationData;
import pl.rosehc.controller.wrapper.global.BarColorWrapper;
import pl.rosehc.controller.wrapper.global.BarStyleWrapper;

public final class ProtectionConfiguration extends ConfigurationData {

  // MESSAGES
  public String protectionHasExpired = "&cTwoja ochrona właśnie wygasła! Powodzenia...";
  public String protectionExpiryTimeInformation = "&aTwoja ochrona wygasa za &2{TIME}";
  public String youAreProtected = "&cPosiadasz ochronę jeszcze przez {TIME}!";
  public String targetIsProtected = "&cGracz {PLAYER_NAME} posiada ochronę jeszcze przez {TIME}!";
  public String successfullyDisabledYourProtection = "&aPomyślnie wyłączyłeś swoją ochronę!";
  public String protectionIsAlreadyDisabled = "&cTwoja ochrona jest już wyłączona!";

  // VALUES
  @SerializedName("expiry_time")
  public String expiryTime = "2m";
  @SerializedName("bar_style")
  public BarStyleWrapper barStyleWrapper = BarStyleWrapper.SOLID;
  @SerializedName("bar_color")
  public BarColorWrapper barColorWrapper = BarColorWrapper.GREEN;
}
