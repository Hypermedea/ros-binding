package org.hypermedea.ros;

import edu.wpi.rail.jrosbridge.messages.Message;
import org.hypermedea.op.InvalidFormException;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class ROSCancelOperation extends ROSOperation {

    /**
     * See
     * <a href="http://docs.ros.org/en/api/actionlib_msgs/html/msg/GoalID.html"><code>actionlib_msgs/GoalID</code></a>.
     */
    public static final String GOAL_ID_MSG_TYPE = "actionlib_msgs/GoalID";

    private final String id;

    public ROSCancelOperation(String targetURI, Map<String, Object> formFields) {
        super(targetURI, formFields);

        try {
            URI uri = new URI(targetURI);
            id = uri.getFragment();
        } catch (URISyntaxException e) {
            throw new InvalidFormException(e);
        }
    }

    /**
     * Publish message of type {@value GOAL_ID_MSG_TYPE} to the <code>cancel</code> topic of the
     * action server declared in form. Note that the <code>rosbridge</code> protocol provides no
     * guarantee that the request was indeed received by the Thing. The operation thus builds an
     * empty response immediately after sending the request.
     *
     * @throws IOException
     */
    @Override
    protected void sendSingleRequest() throws IOException {
        super.sendSingleRequest();

        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("id", id);
        JsonObject payload = builder.build();

        Message msg = new Message(payload);
        msg.setMessageType(GOAL_ID_MSG_TYPE);

        topic.publish(msg);

        onResponse(new ROSResponse(this));
    }

    @Override
    protected String getTopicName(String path) {
        if (!path.endsWith("/")) path += "/";
        return path + "cancel";
    }

}
