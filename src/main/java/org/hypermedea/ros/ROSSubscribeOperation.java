package org.hypermedea.ros;

import ch.unisg.ics.interactions.wot.td.bindings.Response;
import ch.unisg.ics.interactions.wot.td.schemas.DataSchema;
import edu.wpi.rail.jrosbridge.Topic;
import edu.wpi.rail.jrosbridge.messages.Message;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ROSSubscribeOperation extends ROSOperation {

    private final Topic topic;

    public ROSSubscribeOperation(Topic topic) {
        this.topic = topic;
    }

    @Override
    public void setPayload(DataSchema schema, Object payload) {
        // TODO warn the payload will be ignored
    }

    @Override
    public void sendRequest() throws IOException {
        topic.subscribe((Message message) -> {
            Object payload = parseJson(message.toJsonObject());
            Response r = new ROSResponse(payload);

            onResponse(r);

            // TODO if message is error?
        });
    }

    @Override
    protected void setObjectPayload(Map<String, Object> payload) {
        // do nothing
    }

    @Override
    protected void setArrayPayload(List<Object> payload) {
        // do nothing
    }

    @Override
    protected void setStringPayload(String payload) {
        // do nothing
    }

    @Override
    protected void setBooleanPayload(Boolean payload) {
        // do nothing
    }

    @Override
    protected void setIntegerPayload(Long payload) {
        // do nothing
    }

    @Override
    protected void setNumberPayload(Double payload) {
        // do nothing
    }

}
