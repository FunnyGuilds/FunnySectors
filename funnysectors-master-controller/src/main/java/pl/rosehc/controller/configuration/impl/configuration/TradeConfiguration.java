package pl.rosehc.controller.configuration.impl.configuration;

import com.google.gson.annotations.SerializedName;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import pl.rosehc.controller.configuration.ConfigurationData;
import pl.rosehc.controller.wrapper.spigot.DefaultSpigotGuiElementWrapper;
import pl.rosehc.controller.wrapper.spigot.SpigotGuiWrapper;
import pl.rosehc.controller.wrapper.trade.TradeAcceptItemSpigotGuiElementWrapper;

public final class TradeConfiguration extends ConfigurationData {

  @SerializedName("gui")
  public SpigotGuiWrapper tradeGuiWrapper = createTradeGuiWrapper();
  public List<Integer> leftSlots = Arrays.asList(
      0, 1, 2, 3,
      9, 10, 11, 12,
      18, 19, 20, 21,
      27, 28, 29, 30,
      36, 37, 38, 39,
      45, 46, 47
  );
  public List<Integer> rightSlots = Arrays.asList(
      5, 6, 7, 8,
      14, 15, 16, 17,
      23, 24, 25, 26,
      32, 33, 34, 35,
      41, 42, 43, 44,
      51, 52, 53
  );
  public String youAreCurrentlyInTrade = "&cJesteś już podczas wymiany z innym graczem!";
  public String playerIsCurrentlyInTrade = "&cPodany gracz jest aktualnie podczas wymiany!";
  public String requestAlreadySent = "&cWysłałeś już prośbę o wymianę do tego gracza!";
  public String requestSentReceiver = "&7Dostałeś prośbę o wymianę do gracza &d{PLAYER_NAME}&7! Aby zaakceptować, kliknij shiftem na tego oto gracza!";
  public String requestSentSender = "&7Pomyślnie wysłałeś prośbę o wymianę do gracza &d{PLAYER_NAME}&7!";
  public String tradeCompleted = "&aPomyślnie wymieniłeś się z graczem {PLAYER_NAME}!";
  public int secondsToAccept = 5;

  private static SpigotGuiWrapper createTradeGuiWrapper() {
    final SpigotGuiWrapper tradeGuiWrapper = new SpigotGuiWrapper();
    tradeGuiWrapper.inventorySize = 54;
    tradeGuiWrapper.inventoryName = "Twoje Itemy           Jego Itemy";
    tradeGuiWrapper.elements = new LinkedHashMap<>();
    int index = 0;
    for (final int slot : new int[]{4, 13, 22, 31, 40, 49}) {
      final DefaultSpigotGuiElementWrapper fillElementWrapper = new DefaultSpigotGuiElementWrapper();
      fillElementWrapper.slot = slot;
      fillElementWrapper.material = "VINE";
      fillElementWrapper.name = "&8#";
      tradeGuiWrapper.elements.put("vine" + (index++), fillElementWrapper);
    }

    final TradeAcceptItemSpigotGuiElementWrapper firstTradeAcceptItemElementWrapper = new TradeAcceptItemSpigotGuiElementWrapper(), secondTradeAcceptItemElementWrapper = new TradeAcceptItemSpigotGuiElementWrapper();
    firstTradeAcceptItemElementWrapper.material = "STAINED_GLASS_PANE";
    firstTradeAcceptItemElementWrapper.slot = 48;
    secondTradeAcceptItemElementWrapper.material = "STAINED_GLASS_PANE";
    secondTradeAcceptItemElementWrapper.slot = 50;
    tradeGuiWrapper.elements.put("accept_first", firstTradeAcceptItemElementWrapper);
    tradeGuiWrapper.elements.put("accept_second", secondTradeAcceptItemElementWrapper);
    return tradeGuiWrapper;
  }
}
