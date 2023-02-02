package pl.rosehc.controller.packet.guild.guild;

import java.util.List;
import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.packet.GuildPacketHandler;
import pl.rosehc.controller.wrapper.guild.GuildRegenerationBlockStateSerializationWrapper;

public final class GuildAddRegenerationBlocksPacket extends Packet {

  private String tag;
  private List<GuildRegenerationBlockStateSerializationWrapper> blockList;

  private GuildAddRegenerationBlocksPacket() {
  }

  public GuildAddRegenerationBlocksPacket(final String tag,
      final List<GuildRegenerationBlockStateSerializationWrapper> blockList) {
    this.tag = tag;
    this.blockList = blockList;
  }

  @Override
  public void handle(final PacketHandler handler) {
    ((GuildPacketHandler) handler).handle(this);
  }

  public String getTag() {
    return this.tag;
  }

  public List<GuildRegenerationBlockStateSerializationWrapper> getBlockList() {
    return this.blockList;
  }
}
