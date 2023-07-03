package org.hypermedea.ros;

import ch.unisg.ics.interactions.wot.td.affordances.Form;
import ch.unisg.ics.interactions.wot.td.bindings.InvalidFormException;
import ch.unisg.ics.interactions.wot.td.bindings.Response;
import edu.wpi.rail.jrosbridge.messages.Message;

import javax.json.JsonStructure;
import javax.json.JsonValue;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Optional;

public class ROSGetStatusOperation extends ROSOperation {

    private final String id;

    public ROSGetStatusOperation(Form form, String operationType) {
        super(form, operationType);

        try {
            URI uri = new URI(form.getTarget());
            id = uri.getFragment();
        } catch (URISyntaxException e) {
            throw new InvalidFormException(e);
        }
    }

    /**
     * <p>
     *     Subscribe to the <code>status</code> topic of the action server targeted in form
     *     and return the last status sent by the action server for the provided goal ID.
     * </p>
     * <p>
     *     If the action is not in the status array sent by the action server, a response
     *     with status "Consumer error" is returned (analogous to HTTP's 404 Not Found).
     *     Otherwise, an object with key <code>status</code> is returned. See
     *     {@link ROSGoalStatusArrayWrapper.GoalStatus} and
     *     <a href="http://docs.ros.org/en/api/actionlib_msgs/html/msg/GoalStatus.html"><code>actionlib_msgs/GoalStatus</code></a>
     *     for more details on possible status codes.
     * </p>
     * <p>
     *     The current implementation doesn't capture the message giving the result of the action.
     * </p>
     *
     * @throws IOException
     */
    @Override
    public void sendRequest() throws IOException {
        super.sendRequest();

        topic.subscribe((Message msg) -> {
            ROSGoalStatusArrayWrapper wrapper = new ROSGoalStatusArrayWrapper(msg);
            Optional<JsonValue> statusOpt = wrapper.getFullStatus(id);

            Response r;

            if (statusOpt.isPresent()) {
                JsonValue status = statusOpt.get();
                Object payload = parseJson((JsonStructure) status);
                r = new ROSResponse(payload, ROSGetStatusOperation.this);
            } else {
                r = new ROSResponse(Response.ResponseStatus.CONSUMER_ERROR, ROSGetStatusOperation.this);
            }

            onResponse(r);
        });
    }

    @Override
    protected String getTopicName(String path) {
        if (!path.endsWith("/")) path += "/";
        return path + "status";
    }

    @Override
    protected String getDefaultMessageType() {
        return ROSGoalStatusArrayWrapper.GOAL_STATUS_ARRAY_MESSAGE_TYPE;
    }

    @Override
    protected Object getPayload() {
        return null;
    }

    @Override
    protected void setObjectPayload(Map<String, Object> payload) {

    }

}
