package org.hypermedea.ros;

import edu.wpi.rail.jrosbridge.messages.Message;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import java.util.Optional;

public class ROSGoalStatusArrayWrapper {

    /**
     * Taken from
     * <a href="http://docs.ros.org/en/api/actionlib_msgs/html/msg/GoalStatus.html"><code>actionlib_msgs/GoalStatus</code></a>.
     */
    public static enum GoalStatus {
        PENDING,
        ACTIVE,
        PREEMPTED,
        SUCCEEDED,
        ABORTED,
        REJECTED,
        PREEMPTING,
        RECALLING,
        RECALLED,
        LOST
    }

    public static final String GOAL_STATUS_ARRAY_MESSAGE_TYPE = "actionlib_msgs/GoalStatusArray";

    private final JsonArray array;

    public ROSGoalStatusArrayWrapper(Message msg) {
        if (!msg.toJsonObject().containsKey("status_list")) {
            throw new IllegalArgumentException("Input message isn't of type "
                    + GOAL_STATUS_ARRAY_MESSAGE_TYPE + ": "
                    + msg);
        }

        array = msg.toJsonObject().getJsonArray("status_list");
    }

    public Optional<JsonValue> getFullStatus(String goalId) {
        return array.stream().filter(status -> {
            String id = ((JsonObject) status).getJsonObject("goal_id").getString("id");
            return id.equals(goalId);
        }).findAny();
    }

    public Optional<GoalStatus> getStatus(String goalId) {
        Optional<JsonValue> opt = getFullStatus(goalId);

        if (opt.isEmpty()) return Optional.empty();

        JsonObject fullStatus = (JsonObject) opt.get();
        int statusCode = fullStatus.getInt("status");

        switch (statusCode) {
            case 0: return Optional.of(GoalStatus.PENDING);
            case 1: return Optional.of(GoalStatus.ACTIVE);
            case 2: return Optional.of(GoalStatus.PREEMPTED);
            case 3: return Optional.of(GoalStatus.SUCCEEDED);
            case 4: return Optional.of(GoalStatus.ABORTED);
            case 5: return Optional.of(GoalStatus.REJECTED);
            case 6: return Optional.of(GoalStatus.PREEMPTING);
            case 7: return Optional.of(GoalStatus.RECALLING);
            case 8: return Optional.of(GoalStatus.RECALLED);
            case 9: return Optional.of(GoalStatus.LOST);
            default:
                String msg = String.format("Unknown goal status (%d) returned by action server for goal id %s", statusCode, goalId);
                throw new RuntimeException(msg);
        }
    }

}
