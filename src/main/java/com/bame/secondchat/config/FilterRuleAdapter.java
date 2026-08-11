package com.bame.secondchat.config;

import com.bame.secondchat.data.ContainsRule;
import com.bame.secondchat.data.FilterRule;
import com.bame.secondchat.data.StartsWithRule;
import com.google.gson.*;

import java.lang.reflect.Type;

public class FilterRuleAdapter implements JsonSerializer<FilterRule>, JsonDeserializer<FilterRule> {

    @Override
    public JsonElement serialize(FilterRule src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", src.getType());
        jsonObject.addProperty("value", src.getValue());
        return jsonObject;
    }

    @Override
    public FilterRule deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String type = jsonObject.get("type").getAsString();
        String value = jsonObject.get("value").getAsString();

        switch (type) {
            case "contains":
                return new ContainsRule(value);
            case "starts_with":
                return new StartsWithRule(value);
            default:
                throw new JsonParseException("Unknown FilterRule type: " + type);
        }
    }
}
