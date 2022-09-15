package org.hypermedea.ros;

import ch.unisg.ics.interactions.wot.td.bindings.Operation;
import edu.wpi.rail.jrosbridge.Topic;
import edu.wpi.rail.jrosbridge.messages.Message;

import javax.json.JsonObject;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ROSPublishOperation extends ROSOperation {

    private final Topic topic;

    private JsonObject payload = null;

    public ROSPublishOperation(Topic topic) {
        this.topic = topic;
    }

    /**
     * Implement {@link Operation#sendRequest()} by publishing to the topic declared in form.
     * Note that the rosbridge protocol provides no guarantee that the request was indeed
     * received by the Thing. The operation thus builds an empty response immediately after
     * sending the request.
     *
     * TODO is that true?
     */
    @Override
    public void sendRequest() throws IOException {
        if (!topic.getRos().isConnected()) topic.getRos().connect();

        Message m = payload == null ? new Message() : new Message(payload);
        topic.publish(m);

        onResponse(new ROSResponse());
    }

    @Override
    protected void setObjectPayload(Map<String, Object> payload) {
        this.payload = (JsonObject) buildJson(payload);
    }

    @Override
    protected void setArrayPayload(List<Object> payload) {
        this.payload = (JsonObject) buildJson(payload);
    }

    @Override
    protected void setStringPayload(String payload) {
        // TODO wrap payload in object?
        throw new IllegalArgumentException("Primitive JSON value not supported as payload: " + payload);
    }

    @Override
    protected void setBooleanPayload(Boolean payload) {
        // TODO wrap payload in object?
        throw new IllegalArgumentException("Primitive JSON value not supported as payload: " + payload);
    }

    @Override
    protected void setIntegerPayload(Long payload) {
        // TODO wrap payload in object?
        throw new IllegalArgumentException("Primitive JSON value not supported as payload: " + payload);
    }

    @Override
    protected void setNumberPayload(Double payload) {
        // TODO wrap payload in object?
        throw new IllegalArgumentException("Primitive JSON value not supported as payload: " + payload);
    }

}
