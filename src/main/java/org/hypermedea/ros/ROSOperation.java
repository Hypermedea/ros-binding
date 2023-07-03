package org.hypermedea.ros;

import ch.unisg.ics.interactions.wot.td.affordances.Form;
import ch.unisg.ics.interactions.wot.td.bindings.BaseOperation;
import edu.wpi.rail.jrosbridge.Ros;
import edu.wpi.rail.jrosbridge.Topic;

import javax.json.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ROSOperation extends BaseOperation {

    public static final String DEFAULT_MESSAGE_TYPE = "std_msgs/String";

    protected final Topic topic;

    public ROSOperation(Form form, String operationType) {
        super(form, operationType);

        try {
            URI targetURI = new URI(form.getTarget());

            String host = targetURI.getHost();
            String topicName = getTopicName(targetURI.getPath());

            String msgType = (String) form.getAdditionalProperties().get(ROS.messageType);
            if (msgType == null) msgType = getDefaultMessageType();

            Ros ros = new Ros(host);

            topic = new Topic(ros, topicName, msgType);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendRequest() throws IOException {
        if (!topic.getRos().isConnected()) topic.getRos().connect();
    }

    /**
     * Map the path element of the operation's target URI to a ROS topic.
     *
     * @param path path of the target URI declared in the input form
     * @return a full topic name for the operation
     */
    protected abstract String getTopicName(String path);

    /**
     * Return the operation-specific default message type.
     *
     * @return a message type, e.g. {@value DEFAULT_MESSAGE_TYPE}
     */
    protected String getDefaultMessageType() {
        return DEFAULT_MESSAGE_TYPE;
    }

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
                else if (v instanceof JsonNumber) parsed = parseJsonNumber((JsonNumber) v);
                else if (v.equals(JsonValue.TRUE)) parsed = Boolean.TRUE;
                else if (v.equals(JsonValue.FALSE)) parsed = Boolean.FALSE;
                // FIXME null values?

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
                else if (v instanceof JsonNumber) parsed = parseJsonNumber((JsonNumber) v);
                else if (v.equals(JsonValue.TRUE)) parsed = Boolean.TRUE;
                else if (v.equals(JsonValue.FALSE)) parsed = Boolean.FALSE;
                // FIXME null values?

                l.add(parsed);
            }

            return l;
        } else {
            return null;
        }
    }

    protected Number parseJsonNumber(JsonNumber nb) {
        if (nb.isIntegral()) return nb.longValue();
        else return nb.doubleValue();
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

    @Override
    protected void setArrayPayload(List<Object> payload) {
        throw new IllegalArgumentException("JSON array not supported as payload: " + payload);
    }

    @Override
    protected void setStringPayload(String payload) {
        throw new IllegalArgumentException("Primitive JSON value not supported as payload: " + payload);
    }

    @Override
    protected void setBooleanPayload(Boolean payload) {
        throw new IllegalArgumentException("Primitive JSON value not supported as payload: " + payload);
    }

    @Override
    protected void setIntegerPayload(Long payload) {
        throw new IllegalArgumentException("Primitive JSON value not supported as payload: " + payload);
    }

    @Override
    protected void setNumberPayload(Double payload) {
        throw new IllegalArgumentException("Primitive JSON value not supported as payload: " + payload);
    }

}
