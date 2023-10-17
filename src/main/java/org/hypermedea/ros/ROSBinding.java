package org.hypermedea.ros;

import edu.wpi.rail.jrosbridge.Ros;
import org.hypermedea.op.BaseOperation;
import org.hypermedea.op.BaseProtocolBinding;
import org.hypermedea.op.InvalidFormException;
import org.hypermedea.op.Operation;

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
    public Operation bind(String targetURI, Map<String, Object> formFields) {
        try {
            URI uri = new URI(targetURI);

            if (!uri.getScheme().equals(URI_SCHEME)) {
                throw new InvalidFormException(String.format("URI unrecognized by ROS binding: %s", targetURI));
            }

            String method = (String) formFields.get(Operation.METHOD_NAME_FIELD);
            String id = uri.getFragment();

            if (uri.getFragment() == null || id.isEmpty()) {
                switch (method) {
                    case Operation.PUT:
                        BaseOperation publishOp = new ROSPublishOperation(targetURI, formFields);
                        return publishOp;

                    case Operation.GET:
                    case Operation.WATCH:
                        BaseOperation subscribeOp = new ROSSubscribeOperation(targetURI, formFields);
                        subscribeOp.setTimeout(0);
                        return subscribeOp;

                    case Operation.POST:
                        BaseOperation sendGoalOp = new ROSSendGoalOperation(targetURI, formFields);
                        return sendGoalOp;

                    case Operation.DELETE:
                        throw new InvalidFormException("Non-action resources cannot be deleted in ROS");

                    default:
                        throw new InvalidFormException(String.format("Method not supported by ROS binding for non-action resources: ", method));
                }
            } else {
                switch (method) {
                    case Operation.GET:
                        BaseOperation getStatusOp = new ROSGetStatusOperation(targetURI, formFields);
                        return getStatusOp;

                    case Operation.DELETE:
                        BaseOperation cancelOp = new ROSCancelOperation(targetURI, formFields);
                        return cancelOp;

                    default:
                        throw new InvalidFormException(String.format("Method not supported by ROS binding for action resources: ", method));
                }
            }
        } catch (URISyntaxException e) {
            throw new InvalidFormException(e);
        }
    }

}
