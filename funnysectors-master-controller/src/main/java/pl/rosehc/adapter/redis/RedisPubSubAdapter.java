package pl.rosehc.adapter.redis;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import pl.rosehc.adapter.redis.packet.Packet;
import pl.rosehc.adapter.redis.packet.PacketCoderHelper;
import redis.clients.jedis.BinaryJedisPubSub;

@SuppressWarnings({"rawtypes", "unchecked"})
public final class RedisPubSubAdapter extends BinaryJedisPubSub {

  private static final ObjectMapper MAPPER = new ObjectMapper().setSerializationInclusion(
      Include.NON_NULL);

  static {
    MAPPER.setVisibility(MAPPER.getSerializationConfig().getDefaultVisibilityChecker()
        .withFieldVisibility(Visibility.ANY).withGetterVisibility(Visibility.NONE)
        .withSetterVisibility(Visibility.NONE).withCreatorVisibility(Visibility.NONE));
    MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    MAPPER.enable(Feature.WRITE_BIGDECIMAL_AS_PLAIN);
    MAPPER.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    MAPPER.enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY);
  }

  private final Map<Class<? extends Packet>, Consumer> handlerMap = new ConcurrentHashMap<>();

  @Override
  public void onMessage(final byte[] channel, final byte[] message) {
    final Packet packet = PacketCoderHelper.readValue(message);
    if (packet != null) {
      this.handlerMap.entrySet().stream()
          .filter(entry -> entry.getKey().isAssignableFrom(packet.getClass())).findFirst()
          .ifPresent(entry -> entry.getValue().accept(packet));
    }
  }

  <T extends Packet> void registerHandler(final Class<T> type, final Consumer<T> handler) {
    this.handlerMap.put(type, handler);
  }
}
