package org.hypermedea.ros;

import ch.unisg.ics.interactions.wot.td.bindings.Response;
import ch.unisg.ics.interactions.wot.td.schemas.DataSchema;
import edu.wpi.rail.jrosbridge.Topic;
import edu.wpi.rail.jrosbridge.messages.Message;

import java.io.IOException;

public class ROSSubscribeOperation extends ROSOperation {

    private final Topic topic;

    public ROSSubscribeOperation(Topic topic) {
        this.topic = topic;
    }

    @Override
    public void setPayload(DataSchema schema, Object payload) {

    }

    @Override
    public Response execute() throws IOException {
        topic.subscribe((Message message) -> {
            System.out.println(message);

            Object payload = parseJson(message.toJsonObject());
            // TODO notify subscriber of payload
        });

        return null;
    }

}
