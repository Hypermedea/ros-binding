package org.hypermedea.ros;

import ch.unisg.ics.interactions.wot.td.affordances.Form;
import edu.wpi.rail.jrosbridge.messages.Message;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.io.IOException;
import java.util.Map;

public class ROSSendGoalOperation extends ROSOperation {

    private static long counter = 0;

    private JsonObject payload;

    public ROSSendGoalOperation(Form form, String operationType) {
        super(form, operationType);
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
     *     The current implementation provides no guarantee that the action server accepted the request.
     *     This may change in the future.
     * </p>
     *
     * @throws IOException
     */
    @Override
    public void sendRequest() throws IOException {
        super.sendRequest();

        JsonObject goal = wrapPayload(payload);

        Message msg = new Message(goal);
        topic.publish(msg);

        String id = goal.getJsonObject("goal_id").getString("id");
        ROSResponse res = new ROSResponse(this);
        res.addLink("", form.getTarget() + "#" + id);

        // TODO instead, subscribe to feedback and wait for status to equal ACTIVE
        onResponse(res);
    }

    @Override
    protected String getTopicName(String path) {
        if (!path.endsWith("/")) path += "/";
        return path + "goal";
    }

    @Override
    protected Object getPayload() {
        return null;
    }

    @Override
    protected void setObjectPayload(Map<String, Object> payload) {
        this.payload = (JsonObject) buildJson(payload);
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

        if (payload != null) builder.add ("goal", payload);

        return builder.build();
    }

}
