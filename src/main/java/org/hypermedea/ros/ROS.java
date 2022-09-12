package org.hypermedea.ros;

/**
 * <p>
 *   Vocabulary to declare ROS form attributes in W3C Thing Description documents, as described in the
 *   <a href="https://w3c.github.io/wot-binding-templates/">W3C Web of Things (WoT) Binding Templates</a>
 *   specification.
 * </p>
 *
 * <p>
 *   Example:
 *   <code><pre>
 *     [] a hctl:Form ;
 *        hctl:target "ros+ws://example.org/turtlesim/cmd_vel" ;
 *        ros:messageType "geometry_msgs/Twist" .
 *   </pre></code>
 * </p>
 */
public class ROS {

    public static final String NAMESPACE = "https://github.com/RobotWebTools/rosbridge_suite/blob/ros1/ROSBRIDGE_PROTOCOL.md#";

    public static final String messageType = NAMESPACE + "messageType";

    public static final String methodName = NAMESPACE + "methodName";

}
