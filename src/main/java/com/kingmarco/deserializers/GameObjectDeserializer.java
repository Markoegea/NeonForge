package com.kingmarco.deserializers;

import com.google.gson.*;
import com.kingmarco.components.Component;
import com.kingmarco.forge.GameObject;
import com.kingmarco.components.Transform;

import java.lang.reflect.Type;

/**
 * A class responsible to deserializer for GameObjects
 * */
public class GameObjectDeserializer implements JsonDeserializer<GameObject> {
    /**
     * Deserializes a JSON element into a GameObject.
     *
     * @param json       The JSON element to deserialize.
     * @param typeOfT    The type of the GameObject.
     * @param context    The deserialization context.
     * @return The deserialized GameObject.
     * @throws JsonParseException If an error occurs during deserialization.
     */
    @Override
    public GameObject deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String name = jsonObject.get("name").getAsString();
        JsonArray components = jsonObject.getAsJsonArray("components");

        GameObject go = new GameObject(name);
        for (JsonElement e : components){
            Component c = context.deserialize(e, Component.class);
            go.addComponent(c);
        }
        go.transform = go.getComponent(Transform.class);
        return go;
    }
}
