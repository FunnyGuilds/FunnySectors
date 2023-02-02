package pl.rosehc.controller.configuration.impl.configuration;

import pl.rosehc.controller.configuration.ConfigurationData;

public final class AuthConfiguration extends ConfigurationData {

  public String queueServerName = "queue";
  public String maxAccountsPerIp = "&cPosiadasz maksymalna ilosc kont na tym IP!";
  public String tooManyRequests = "&cW tym momencie zbyt duzo osób próbuje się połączyć!";
  public String invalidNickname = "&cTwoj nick jest niepoprawny!";
  public String cantVerifyYourAccount = "&cSerwery do autoryzacji &6logowania &cpremium przestaly\n&6dzialac! &cPoczekaj kilkanascie minut i sprobuj polaczyc sie ponownie.";
  public String usernameDidntMatch = "&cNiepoprawny nick! Poprawny to {REAL_NICKNAME}!";
  public String loggedInAsPremium = "&aZostałeś zalogowany z konta premium.";
  public String cannotExecuteThisCommandAsPremium = "&cNie możesz wykonywać tej komendy jako gracz premium.";
  public String notRegistered = "&cNie jesteś zarejestrowany, nie możesz tego teraz zrobić.";
  public String notLogged = "&cNie jesteś zalogowany, nie możesz tego teraz zrobić.";
  public String alreadyRegistered = "&cJestes już zarejestrowany, nie możesz wykonać tego ponownie.";
  public String alreadyLogged = "&cJesteś już zalogowany, nie możesz wykonać tego ponownie.";
  public String passwordDidntMatch = "&cPodane hasło się nie zgadza!";
  public String passwordIsTooShort = "&cPodane hasło jest zbyt małe!";
  public String passwordsAreTheSame = "&cPodane hasła są takie same! Próba zmiany nieudana.";
  public String passwordsAreNotTheSame = "&cPodane hasła nie są takie same! Próba rejestracji nieudana.";
  public String successfullyLoggedIn = "&aPomyślnie zalogowałeś się na swoje konto!";
  public String successfullyRegistered = "&aPomyślnie zarejestrowałeś swoje konto!";
  public String successfullyChangedYourPassword = "&7Pomyslnie zmieniles swoje &dhaslo&7!";
  public String registerTimeoutInfo = "&7Twój pozostały czas na zarejestrowanie się: &d{TIME}&7!";
  public String loginTimeoutInfo = "&7Twój pozostały czas na zalogowanie się: &d{TIME}&7!";
  public String registerTimeoutReached = "&cTwój czas na zarejestrowanie się na serwerze minął!";
  public String loginTimeoutReached = "&cTwój czas na zalogowanie się na serwer minął!";
  public String youNeedToRegisterInfo = "&7Zarejestruj się używając komendy: &d/register <hasło> <powtórz_hasło>";
  public String youNeedToLoginInfo = "&7Zaloguj się używając komendy: &d/login <hasło>";
  public String userDataNotFound = "&cTwoje dane nie zostały znalezione!";
  public String userNotFound = "&cNie odnaleziono użytkownika {PLAYER_NAME} w bazie danych!";
  public String userAccountIsAlreadyRegistered = "&cKonto gracza o nazwie {PLAYER_NAME} już istnieje!";
  public String yourAccountHaveBeenUnregistered = "&7Twoje konto zostało odrejestrowane przez administratora o nicku &d{PLAYER_NAME}&7!";
  public String yourAccountStatusHasBeenChangedToPremium = "&aTwój status konta został zmieniony na PREMIUM!";
  public String yourAccountStatusHasBeenChangedToNonPremium = "&aTwój status konta został zmieniony na NONPREMIUM!";
  public String successfullyRegisteredUser = "&7Pomyślnie zarejestrowałeś użytkownika o nicku &d{PLAYER_NAME}&7!";
  public String successfullyChangedUserPassword = "&7Pomyślnie zmieniłeś hasło użytkownika o nicku &d{PLAYER_NAME}&7!";
  public String successfullyUnregisteredUser = "&7Pomyślnie odrejestrowałeś użytkownika o nicku &d{PLAYER_NAME}&7!";
  public String successfullyChangedUserStatusToPremium = "&7Pomyślnie zmieniłeś status konta gracza &d{PLAYER_NAME} &7na &5PREMIUM&7!";
  public String successfullyChangedUserStatusToNonPremium = "&7Pomyślnie zmieniłeś status konta gracza &d{PLAYER_NAME} &7na &5NONPREMIUM&7!";
  public String authUserInfo = "&7Użytkownik: &d{PLAYER_NAME}\n"
      + "&7IP: &d{IP_ADDRESS}\n"
      + "&7Kiedy dołączył: &d{FIRST_JOIN_DATE}\n"
      + "&7Kiedy ostatnio był aktywny: &d{LAST_ONLINE_TIME}\n"
      + "&7Czy aktywny w tym momencie: &d{IS_ACTIVE}\n"
      + "&7Ostatni sektor: &d{LAST_SECTOR_NAME}";
  public String authCommandUsage =
      "&5/auth register <nick> <hasło> - &dRejestruje gracza na podane hasło"
          + "\n&5/auth unregister <nick> - &dOdrejestrowuje gracza"
          + "\n&5/auth changepassword <nick> <hasło> - &dZmienia hasło danemu graczu"
          + "\n&5/auth setpremium <nick> [true/false] - &dUstawia status konta gracza na PREMIUM jeżeli 2 argument to true,"
          + " w innym przypadku ustawia status konta na NONPREMIUM"
          + "\n&5/auth info <nick> - &dWyświetla informacje o koncie gracza";
  public String blazingpackAuthenticationError = "&cWystąpił niespodziewany problem podczas autoryzacji blazingpacka.";
  public String profileErrorOccurred = "&cWystąpił niespodziewany problem z profilem, relognij.";
}
