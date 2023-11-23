package org.hypermedea.ros;

/**
 * <p>
 *   Vocabulary to declare ROS form fields in Hypermedea operations.
 * </p>
 * <p>
 *   Example for basic publish/subscribe, where the path of the target URI corresponds to a topic name:
 *   <code><pre>
ns("https://github.com/RobotWebTools/rosbridge_suite/blob/ros1/ROSBRIDGE_PROTOCOL.md#") .

+!call_cmdvel : ns(ROS) <-
  .concat(ROS, "messageType", MessageType) ;
  put(
    "ros+ws://example.org/turtlesim/cmd_vel",
    json([kv("linear", ...), kv("angular", ...)]),
    [kv(MessageType, "geometry_msgs/Twist")]
  ) .</pre></code>
 * </p>
 * <p>
 *   Example for <code>actionlib</code> operations, where the path of the target URI corresponds to an action name
 *   and the URI fragment corresponds to the ID of an on-going action:
 *   <code><pre>+!call_shape : ns(ROS) <-
  .concat(ROS, "goalId", GoalId) ;
  post("ros+ws://example.org/turtle_shape#{id}") ;
  .wait({ +rdf("ros+ws://example.org/turtle_shape", GoalId, ID) }) ;
 .print(ID) .</pre></code>
 * </p>
 */
public class ROS {

    public static final String NAMESPACE = "https://github.com/RobotWebTools/rosbridge_suite/blob/ros1/ROSBRIDGE_PROTOCOL.md#";

    public static final String messageType = NAMESPACE + "messageType";

    public static final String goalId = NAMESPACE + "goalId";

}
