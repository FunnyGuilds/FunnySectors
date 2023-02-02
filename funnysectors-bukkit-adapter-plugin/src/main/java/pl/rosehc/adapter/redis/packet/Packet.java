package pl.rosehc.adapter.redis.packet;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import java.io.Serializable;

/**
 * @author stevimeister on 17/06/2021
 **/
@JsonTypeInfo(use = Id.CLASS)
public abstract class Packet implements Serializable {

  public abstract void handle(final PacketHandler handler);
}