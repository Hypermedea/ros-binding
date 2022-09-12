package org.hypermedea.ros;

import ch.unisg.ics.interactions.wot.td.bindings.Response;
import ch.unisg.ics.interactions.wot.td.schemas.DataSchema;
import edu.wpi.rail.jrosbridge.Topic;
import edu.wpi.rail.jrosbridge.messages.Message;

import javax.json.JsonObject;
import java.io.IOException;

public class ROSPublishOperation extends ROSOperation {

    private final Topic topic;

    private JsonObject payload = null;

    public ROSPublishOperation(Topic topic) {
        this.topic = topic;
    }

    @Override
    public void setPayload(DataSchema schema, Object payload) {
        // TODO schema verification

        try {
            this.payload = (JsonObject) buildJson(payload);
        } catch (ClassCastException e) {
            // TODO throw dedicated exception (ROS bridge maps messages to JSON objects only)
            e.printStackTrace();
        }
    }

    @Override
    public Response execute() throws IOException {
        if (!topic.getRos().isConnected()) topic.getRos().connect();

        Message m = payload == null ? new Message() : new Message(payload);
        topic.publish(m);

        return new ROSResponse();
    }

}
