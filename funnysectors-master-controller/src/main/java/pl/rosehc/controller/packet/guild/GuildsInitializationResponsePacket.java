package pl.rosehc.controller.packet.guild;

import pl.rosehc.adapter.redis.callback.CallbackPacket;
import pl.rosehc.adapter.redis.packet.PacketHandler;

public final class GuildsInitializationResponsePacket extends CallbackPacket {

  private byte[] configurationData, guildSchematicData;

  private GuildsInitializationResponsePacket() {
  }

  public GuildsInitializationResponsePacket(final byte[] configurationData,
      final byte[] guildSchematicData) {
    this.configurationData = configurationData;
    this.guildSchematicData = guildSchematicData;
  }

  @Override
  public void handle(final PacketHandler ignored) {
  }

  public byte[] getConfigurationData() {
    return this.configurationData;
  }

  public byte[] getGuildSchematicData() {
    return this.guildSchematicData;
  }
}
