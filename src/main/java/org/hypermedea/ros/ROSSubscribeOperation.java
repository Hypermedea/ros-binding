package org.hypermedea.ros;

import edu.wpi.rail.jrosbridge.messages.Message;
import org.hypermedea.op.Response;

import java.io.IOException;
import java.util.Map;

public class ROSSubscribeOperation extends ROSOperation {

    public ROSSubscribeOperation(String targetURI, Map<String, Object> formFields) {
        super(targetURI, formFields);
    }

    @Override
    protected void sendSingleRequest() throws IOException {
        super.sendSingleRequest();

        topic.subscribe((Message message) -> {
            Response r = new ROSResponse(message.toJsonObject(), ROSSubscribeOperation.this);

            onResponse(r);

            // TODO if message is error?
        });
    }

    @Override
    protected String getTopicName(String path) {
        return path;
    }

}
