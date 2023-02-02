package pl.rosehc.controller.packet.controller;

import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketHandler;
import pl.rosehc.controller.ControllerPanicHelper;

public final class ControllerUpdateTimePacket extends Packet {

  @Override
  public void handle(final PacketHandler ignored) {
    ControllerPanicHelper.update();
  }
}
