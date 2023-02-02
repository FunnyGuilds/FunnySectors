package pl.rosehc.adapter.redis.packet;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;

public final class PacketCoderHelper {

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

  private PacketCoderHelper() {
  }

  public static Packet readValue(final byte[] data) {
    try {
      return MAPPER.readValue(data, Packet.class);
    } catch (final IOException e) {
      return null;
    }
  }

  public static byte[] writeValue(final Packet packet) {
    try {
      return MAPPER.writeValueAsBytes(packet);
    } catch (final JsonProcessingException e) {
      throw new UnsupportedOperationException(e);
    }
  }
}
