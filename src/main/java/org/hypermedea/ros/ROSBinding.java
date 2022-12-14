package org.hypermedea.ros;

import ch.unisg.ics.interactions.wot.td.affordances.Form;
import ch.unisg.ics.interactions.wot.td.bindings.BaseProtocolBinding;
import ch.unisg.ics.interactions.wot.td.bindings.InvalidFormException;
import ch.unisg.ics.interactions.wot.td.bindings.Operation;
import ch.unisg.ics.interactions.wot.td.vocabularies.TD;
import edu.wpi.rail.jrosbridge.Ros;
import edu.wpi.rail.jrosbridge.Topic;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Web of Things (WoT) protocol binding for ROS 1, via the
 * <a href="https://github.com/RobotWebTools/rosbridge_suite/blob/ros1/ROSBRIDGE_PROTOCOL.md">rosbridge protocol</a>.
 */
public class ROSBinding extends BaseProtocolBinding {

    public static final String ROSBRIDGE_PROTOCOL = "rosbridge";

    public static final String URI_SCHEME = "ros+ws";

    public static final String DEFAULT_MESSAGE_TYPE = "std_msgs/String";

    /**
     * ROS connections currently open for known hosts.
     */
    private final Map<String, Ros> connections = new HashMap<>();

    @Override
    public String getProtocol() {
        return ROSBRIDGE_PROTOCOL;
    }

    @Override
    public Collection<String> getSupportedSchemes() {
        Set<String> singleton = new HashSet<>();
        singleton.add(URI_SCHEME);

        return singleton;
    }

    @Override
    public Optional<String> getDefaultMethod(String operationType) {
        return Optional.empty();
    }

    @Override
    public Optional<String> getDefaultSubProtocol(String operationType) {
        return Optional.empty();
    }

    @Override
    public Operation bind(Form form, String operationType) {
        try {
            URI targetURI = new URI(form.getTarget());

            if (!targetURI.getScheme().equals(URI_SCHEME)) {
                throw new InvalidFormException(String.format("URI unrecognized by ROS binding: %s", form.getTarget()));
            }

            String host = targetURI.getHost();
            String topic = targetURI.getPath();

            String msgType = (String) form.getAdditionalProperties().get(ROS.messageType);
            if (msgType == null) msgType = DEFAULT_MESSAGE_TYPE;

            if (!connections.containsKey(host)) {
                connections.put(host, new Ros(host));
            }

            Ros ros = connections.get(host);
            Topic t = new Topic(ros, topic, msgType);

            switch (operationType) {
                case TD.writeProperty: return new ROSPublishOperation(t);
                case TD.observeProperty: return new ROSSubscribeOperation(t);

                default: throw new InvalidFormException(String.format("Operation type not supported by ROS binding: ", operationType));
            }
        } catch (URISyntaxException e) {
            throw new InvalidFormException(e);
        }
    }

}
