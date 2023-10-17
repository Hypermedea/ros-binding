package org.hypermedea.ros;

import edu.wpi.rail.jrosbridge.messages.Message;
import org.hypermedea.ct.RepresentationHandlers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

public class ROSPublishOperation extends ROSOperation {

    public ROSPublishOperation(String targetURI, Map<String, Object> formFields) {
        super(targetURI, formFields);
    }

    /**
     * Publish message with the provided payload to the topic declared in form.
     * Note that the <code>rosbridge</code> protocol provides no guarantee that the request was indeed
     * received by the Thing. The operation thus builds an empty response immediately after sending
     * the request.
     */
    @Override
    protected void sendSingleRequest() throws IOException {
        super.sendSingleRequest();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        RepresentationHandlers.serialize(payload, out, target);

        Message m = payload.isEmpty() ? new Message() : new Message(out.toString());
        topic.publish(m);

        onResponse(new ROSResponse(this));
    }

    @Override
    protected String getTopicName(String path) {
        return path;
    }

}
