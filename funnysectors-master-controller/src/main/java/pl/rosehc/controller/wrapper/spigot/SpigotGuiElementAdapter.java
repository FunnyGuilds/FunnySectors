package pl.rosehc.controller.wrapper.spigot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;

public final class SpigotGuiElementAdapter<T extends SpigotGuiElementWrapper> implements
    JsonSerializer<T>, JsonDeserializer<T> {

  @Override
  public T deserialize(final JsonElement element, final Type type,
      final JsonDeserializationContext context) throws JsonParseException {
    final JsonObject object = element.getAsJsonObject();
    try {
      return context.deserialize(element,
          Class.forName(object.get("_guiElementType").getAsString()));
    } catch (final ClassNotFoundException ex) {
      throw new JsonParseException(ex);
    }
  }

  @Override
  public JsonElement serialize(final T value, final Type type,
      final JsonSerializationContext context) {
    final JsonObject object = context.serialize(value, value.getClass()).getAsJsonObject();
    object.addProperty("_guiElementType", value.getClass().getName());
    return object;
  }
}
