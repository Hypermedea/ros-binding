package org.hypermedea.ros;

import ch.unisg.ics.interactions.wot.td.bindings.BaseOperation;

import javax.json.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ROSOperation extends BaseOperation {

    protected Object parseJson(JsonStructure json) {
        JsonValue.ValueType vt = json.getValueType();

        if (vt.equals(JsonValue.ValueType.OBJECT)) {
            JsonObject obj = (JsonObject) json;
            Map<String, Object> map = new HashMap<>();

            for (Map.Entry<String, JsonValue> kv : obj.entrySet()) {
                JsonValue v = kv.getValue();
                Object parsed = null;

                if (v instanceof JsonStructure) parsed = parseJson((JsonStructure) v);
                else if (v instanceof JsonString) parsed = ((JsonString) v).getString();
                else if (v instanceof JsonNumber) parsed = ((JsonNumber) v).doubleValue();
                // FIXME boolean, null values?

                map.put(kv.getKey(), parsed);
            }

            return map;
        } else if (vt.equals(JsonValue.ValueType.ARRAY)) {
            JsonArray arr = (JsonArray) json;
            List<Object> l = new ArrayList<>();

            for (JsonValue v : arr) {
                Object parsed = null;

                if (v instanceof JsonStructure) parsed = parseJson((JsonStructure) v);
                else if (v instanceof JsonString) parsed = ((JsonString) v).getString();
                else if (v instanceof JsonNumber) parsed = ((JsonNumber) v).doubleValue();
                // FIXME boolean, null values?

                l.add(parsed);
            }

            return l;
        } else {
            return null;
        }
    }

    protected JsonStructure buildJson(Object ref) {
        if (ref instanceof Map) {
            Map<String, Object> m = (Map<String, Object>) ref;

            JsonObjectBuilder builder = Json.createObjectBuilder();
            for (Map.Entry<String, Object> kv : m.entrySet()) {
                String k = kv.getKey();
                Object v = kv.getValue();

                if (v instanceof String) builder.add(k, (String) v);
                else if (v instanceof Boolean) builder.add(k, (Boolean) v);
                else if (v instanceof Long) builder.add(k, (Long) v);
                else if (v instanceof Double) builder.add(k, (Double) v);
                else builder.add(k, buildJson(v));
            }

            return builder.build();
        } else if (ref instanceof List) {
            List<Object> l = (List<Object>) ref;

            JsonArrayBuilder builder = Json.createArrayBuilder();
            for (Object member : l) {
                if (member instanceof String) builder.add((String) member);
                else if (member instanceof Boolean) builder.add((Boolean) member);
                else if (member instanceof Long) builder.add((Long) member);
                else if (member instanceof Double) builder.add((Double) member);
                else builder.add(buildJson(member));
            }

            return builder.build();
        } else {
            return null;
        }
    }

}
