package org.hypermedea.ros;

import ch.unisg.ics.interactions.wot.td.affordances.Form;
import edu.wpi.rail.jrosbridge.messages.Message;

import javax.json.JsonObject;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ROSPublishOperation extends ROSOperation {

    private JsonObject payload = null;

    public ROSPublishOperation(Form form, String operationType) {
        super(form, operationType);
    }

    /**
     * Publish message with the provided payload to the topic declared in form.
     * Note that the <code>rosbridge</code> protocol provides no guarantee that the request was indeed
     * received by the Thing. The operation thus builds an empty response immediately after sending
     * the request.
     */
    @Override
    public void sendRequest() throws IOException {
        super.sendRequest();

        Message m = payload == null ? new Message() : new Message(payload);
        topic.publish(m);

        onResponse(new ROSResponse(this));
    }

    @Override
    public Object getPayload() {
        return parseJson(payload);
    }

    @Override
    protected String getTopicName(String path) {
        return path;
    }

    @Override
    protected void setObjectPayload(Map<String, Object> payload) {
        this.payload = (JsonObject) buildJson(payload);
    }

    @Override
    protected void setArrayPayload(List<Object> payload) {
        this.payload = (JsonObject) buildJson(payload);
    }

}
