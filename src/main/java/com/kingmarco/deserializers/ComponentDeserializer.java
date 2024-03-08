package com.kingmarco.deserializers;

import com.google.gson.*;
import com.kingmarco.components.Component;

import java.lang.reflect.Type;

/**
 * A class responsible for deserializer and serializer for components.
 */
public class ComponentDeserializer implements JsonSerializer<Component>, JsonDeserializer<Component> {
    /**
     * Deserializes a JSON element into a component.
     *
     * @param json    The JSON element to deserialize.
     * @param typeOfT The type of the component.
     * @param context The deserialization context.
     * @return The deserialized component.
     * @throws JsonParseException If an error occurs during deserialization.
     */
    @Override
    public Component deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String type = jsonObject.get("type").getAsString();
        JsonElement element = jsonObject.get("properties");

        try {
            return context.deserialize(element, Class.forName(type));
        } catch (ClassNotFoundException e) {
            throw new JsonParseException("Unknown element type: " + type, e);
        }
    }

    /**
     * Serializes a component into a JSON element.
     *
     * @param src     The component to serialize.
     * @param typeOfSrc The type of the component.
     * @param context The serialization context.
     * @return The serialized JSON element.
     */
    @Override
    public JsonElement serialize(Component src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.add("type", new JsonPrimitive(src.getClass().getCanonicalName()));
        result.add("properties", context.serialize(src, src.getClass()));
        return result;
    }
}
