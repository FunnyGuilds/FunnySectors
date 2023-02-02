package pl.rosehc.controller.wrapper.platform;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Supplier;
import org.bukkit.entity.Player;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.platform.PlatformPlugin;

public enum PlatformChatStatusType {

  ENABLED(() -> PlatformPlugin.getInstance()
      .getPlatformConfiguration().messagesWrapper.chatIsAlreadyEnabled,
      () -> PlatformPlugin.getInstance()
          .getPlatformConfiguration().messagesWrapper.chatHasBeenSuccessfullyEnabledSender,
      () -> PlatformPlugin.getInstance()
          .getPlatformConfiguration().messagesWrapper.chatHasBeenSuccessfullyEnabledGlobal,
      PlatformChatStatusTypeVerifier.EMPTY), DISABLED(() -> PlatformPlugin.getInstance()
      .getPlatformConfiguration().messagesWrapper.chatIsAlreadyDisabled,
      () -> PlatformPlugin.getInstance()
          .getPlatformConfiguration().messagesWrapper.chatHasBeenSuccessfullyDisabledSender,
      () -> PlatformPlugin.getInstance()
          .getPlatformConfiguration().messagesWrapper.chatHasBeenSuccessfullyDisabledGlobal,
      PlatformChatStatusTypeVerifier.of(() -> PlatformPlugin.getInstance()
              .getPlatformConfiguration().messagesWrapper.chatIsCurrentlyDisabled,
          "platform-chat-bypass")),
  PREMIUM(() -> PlatformPlugin.getInstance()
      .getPlatformConfiguration().messagesWrapper.chatPremiumIsAlreadyEnabled,
      () -> PlatformPlugin.getInstance()
          .getPlatformConfiguration().messagesWrapper.chatPremiumHasBeenSuccessfullyEnabledSender,
      () -> PlatformPlugin.getInstance()
          .getPlatformConfiguration().messagesWrapper.chatPremiumHasBeenSuccessfullyEnabledGlobal,
      PlatformChatStatusTypeVerifier.of(() -> PlatformPlugin.getInstance()
              .getPlatformConfiguration().messagesWrapper.chatPremiumIsCurrentlyEnabled,
          "platform-chat-premium", "platform-chat-bypass"));

  private final Supplier<String> alreadyEnabledMessageSupplier;
  private final Supplier<String> successfullyEnabledMessageSenderSupplier, successfullyEnabledMessageGlobalSupplier;
  private final PlatformChatStatusTypeVerifier verifier;

  PlatformChatStatusType(final Supplier<String> alreadyEnabledMessageSupplier,
      final Supplier<String> successfullyEnabledMessageSenderSupplier,
      final Supplier<String> successfullyEnabledMessageGlobalSupplier,
      final PlatformChatStatusTypeVerifier verifier) {
    this.alreadyEnabledMessageSupplier = alreadyEnabledMessageSupplier;
    this.successfullyEnabledMessageSenderSupplier = successfullyEnabledMessageSenderSupplier;
    this.successfullyEnabledMessageGlobalSupplier = successfullyEnabledMessageGlobalSupplier;
    this.verifier = verifier;
  }

  public String getAlreadyEnabledMessage() {
    return this.alreadyEnabledMessageSupplier.get();
  }

  public String getSuccessfullyEnabledMessageSender() {
    return this.successfullyEnabledMessageSenderSupplier.get();
  }

  public String getSuccessfullyEnabledMessageGlobal() {
    return this.successfullyEnabledMessageGlobalSupplier.get();
  }

  public PlatformChatStatusTypeVerifier getVerifier() {
    return this.verifier;
  }

  public interface PlatformChatStatusTypeVerifier {

    PlatformChatStatusTypeVerifier EMPTY = new PlatformChatStatusTypeVerifier() {

      @Override
      public String message() {
        return null;
      }

      @Override
      public String[] permissions() {
        return null;
      }
    };

    static PlatformChatStatusTypeVerifier of(final Supplier<String> message,
        final String... permissions) {
      return new PlatformChatStatusTypeVerifier() {

        @Override
        public String message() {
          return message.get();
        }

        @Override
        public String[] permissions() {
          return permissions;
        }
      };
    }

    String message();

    String[] permissions();

    default boolean verify(final Player player) {
      if (Objects.isNull(this.message()) || Objects.isNull(this.permissions())) {
        return true;
      }

      final boolean hasPermission = Arrays.stream(this.permissions()).map(player::hasPermission)
          .reduce(false, (left, right) -> left || right);
      if (!hasPermission) {
        ChatHelper.sendMessage(player, this.message());
        return false;
      }

      return true;
    }
  }
}
