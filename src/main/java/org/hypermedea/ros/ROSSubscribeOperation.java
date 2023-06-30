package org.hypermedea.ros;

import ch.unisg.ics.interactions.wot.td.affordances.Form;
import ch.unisg.ics.interactions.wot.td.bindings.Response;
import edu.wpi.rail.jrosbridge.messages.Message;

import java.io.IOException;
import java.util.Map;

public class ROSSubscribeOperation extends ROSOperation {

    public ROSSubscribeOperation(Form form, String operationType) {
        super(form, operationType);
    }

    @Override
    public void sendRequest() throws IOException {
        super.sendRequest();

        topic.subscribe((Message message) -> {
            Object payload = parseJson(message.toJsonObject());
            Response r = new ROSResponse(payload, ROSSubscribeOperation.this);

            onResponse(r);

            // TODO if message is error?
        });
    }

    @Override
    protected String getTopicName(String path) {
        return path;
    }

    @Override
    protected Object getPayload() {
        return null;
    }

    @Override
    protected void setObjectPayload(Map<String, Object> payload) {
        throw new IllegalArgumentException("ROS subscribe operation does not take any input");
    }

}
