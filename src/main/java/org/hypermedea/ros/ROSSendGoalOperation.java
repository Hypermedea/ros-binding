package org.hypermedea.ros;

import edu.wpi.rail.jrosbridge.Topic;
import edu.wpi.rail.jrosbridge.messages.Message;
import org.hypermedea.ct.RepresentationHandlers;
import org.hypermedea.op.Response;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class ROSSendGoalOperation extends ROSOperation {

    private static final long TIMEOUT = 60l;

    public ROSSendGoalOperation(String targetURI, Map<String, Object> formFields) {
        super(targetURI, formFields);
    }

    /**
     * <p>
     *     Publish payload wrapped in a message with auto-generated
     *     <a href="http://docs.ros.org/en/api/actionlib_msgs/html/msg/GoalID.html"><code>GoalID</code></a>
     *     to the <code>goal</code> topic of the action server declared in form. The response returned to
     *     the caller includes a link to the on-going action, for querying and cancellation (action ID
     *     given as URI fragment).
     * </p>
     * <p>
     *     The current implementation returns a response as soon as the action server acknowledges
     *     the request and provides a status code for it (see {@link ROSGetStatusOperation}).
     * </p>
     *
     * @throws IOException
     */
    @Override
    protected void sendSingleRequest() throws IOException {
        super.sendSingleRequest();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        RepresentationHandlers.serialize(payload, out, target);
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());

        JsonObject p = Json.createReader(in).readObject();
        JsonObject goal = wrapPayload(p);

        String id = goal.getJsonObject("goal_id").getString("id");

        long requestTime = System.currentTimeMillis();

        String statusTopicName = topic.getName().replaceFirst("/goal$", "/status");
        String msgType = ROSGoalStatusArrayWrapper.GOAL_STATUS_ARRAY_MESSAGE_TYPE;
        Topic statusTopic = new Topic(topic.getRos(), statusTopicName, msgType);
        statusTopic.subscribe((Message msg) -> {
            ROSGoalStatusArrayWrapper wrapper = new ROSGoalStatusArrayWrapper(msg);
            Optional<ROSGoalStatusArrayWrapper.GoalStatus> statusOpt = wrapper.getStatus(id);

            if (statusOpt.isEmpty()) {
                long delay = System.currentTimeMillis() - requestTime;
                if (delay > TIMEOUT * 1000) onError();
            } else {
                ROSGoalStatusArrayWrapper.GoalStatus rosStatus = statusOpt.get();

                Response.ResponseStatus status;
                switch (rosStatus) {
                    case PENDING:
                    case ACTIVE:
                    case PREEMPTED:
                    case SUCCEEDED:
                    case RECALLED:
                    case PREEMPTING:
                    case RECALLING:
                        status = Response.ResponseStatus.OK;
                        break;

                    case REJECTED:
                        status = Response.ResponseStatus.CLIENT_ERROR;
                        break;

                    case ABORTED:
                        status = Response.ResponseStatus.SERVER_ERROR;
                        break;

                    case LOST:
                    default:
                        status = Response.ResponseStatus.UNKNOWN_ERROR;
                        break;
                }

                ROSResponse res = new ROSResponse(status, this);
                res.addLink(ROS.goalId, target + "#" + id);

                statusTopic.unsubscribe();

                onResponse(res);
            }

        });

        Message msg = new Message(goal);
        topic.publish(msg);
    }

    @Override
    protected String getTopicName(String path) {
        if (!path.endsWith("/")) path += "/";
        return path + "goal";
    }

    private JsonObject wrapPayload(JsonObject payload) {
        long time = System.currentTimeMillis();
        long secs = time / 1000;
        long nsecs = time % 1000 * 1000;

        JsonObjectBuilder stamp = Json.createObjectBuilder();
        stamp.add("secs", secs);
        stamp.add("nsecs", nsecs);

        JsonObjectBuilder meta = Json.createObjectBuilder();
        meta.add("stamp", stamp);
        meta.add("id", topic.getName() + "-" + secs + "." + nsecs);

        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("goal_id", meta);

        if (payload != null) builder.add("goal", payload);

        return builder.build();
    }

}
