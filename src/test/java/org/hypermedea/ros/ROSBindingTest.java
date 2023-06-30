package org.hypermedea.ros;

import ch.unisg.ics.interactions.wot.td.affordances.Form;
import ch.unisg.ics.interactions.wot.td.affordances.Link;
import ch.unisg.ics.interactions.wot.td.bindings.*;
import ch.unisg.ics.interactions.wot.td.schemas.ObjectSchema;
import ch.unisg.ics.interactions.wot.td.vocabularies.TD;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ROSBindingTest {

    @BeforeAll
    public static void init() {
        ProtocolBindings.registerBinding(ROSBinding.class.getName());
    }

    @Test
    public void testPublish() throws IOException {
        Form f = new Form.Builder("ros+ws://localhost:9090/turtle1/cmd_vel")
                .addProperty(ROS.messageType, "geometry_msgs/Twist")
                .build();

        ProtocolBinding b = ProtocolBindings.getBinding(f);
        Operation op = b.bind(f, TD.writeProperty);

        assertInstanceOf(ROSPublishOperation.class, op);

        Map<String, Object> linearVel = new HashMap<>();
        linearVel.put("x", -2.0);
        linearVel.put("y", 0.0);
        linearVel.put("z", 0.0);

        Map<String, Object> angularVel = new HashMap<>();
        angularVel.put("x", 0.0);
        angularVel.put("y", 0.0);
        angularVel.put("z", 0.0);

        Map<String, Object> payload = new HashMap<>();

        payload.put("linear", linearVel);
        payload.put("angular", angularVel);

        op.setPayload(new ObjectSchema.Builder().build(), payload);

        op.sendRequest();
        Response r = op.getResponse();

        assertEquals(Response.ResponseStatus.OK, r.getStatus());
    }

    @Test
    public void testSubscribe() throws IOException {
        Form f = new Form.Builder("ros+ws://localhost:9090/turtle1/pose")
                .addProperty(ROS.messageType, "turtlesim/Pose")
                .build();

        ProtocolBinding b = ProtocolBindings.getBinding(f);
        Operation op = b.bind(f, TD.observeProperty);

        ((BaseOperation) op).setTimeout(2);

        assertInstanceOf(ROSSubscribeOperation.class, op);

        // wait synchronously for the first response
        op.sendRequest();
        Response response = op.getResponse();

        assertEquals(Response.ResponseStatus.OK, response.getStatus());
        assertTrue(response.getPayload().isPresent());

        Map<String, Object> json = (Map<String, Object>) response.getPayload().get();
        assertTrue(json.containsKey("x"));
        assertInstanceOf(Double.class, json.get("x"));
    }

    @Test
    public void testAction() throws IOException {
        Form f1 = new Form.Builder("ros+ws://localhost:9090/turtle_shape").build();

        ProtocolBinding b = ProtocolBindings.getBinding(f1);
        Operation sendGoal = b.bind(f1, TD.invokeAction);

        assertInstanceOf(ROSSendGoalOperation.class, sendGoal);

        Map<String, Object> goal = new HashMap<>();
        goal.put("edges", 4l);
        goal.put("radius", Math.PI / 2);
        sendGoal.setPayload(new ObjectSchema.Builder().build(), goal);

        sendGoal.sendRequest();
        Response response = sendGoal.getResponse();

        assertTrue(response.getPayload().isEmpty());

        Optional<Link> linkToAction = response.getLinks().stream().findFirst();

        assertTrue(linkToAction.isPresent());

        String rel = linkToAction.get().getRelationType();
        String target = linkToAction.get().getTarget();

        assertTrue(rel.isEmpty());

        Form f2 = new Form.Builder(target).build();

        Operation getStatus = b.bind(f2, TD.queryAction);
        Operation cancel = b.bind(f2, TD.cancelAction);

        assertInstanceOf(ROSGetStatusOperation.class, getStatus);
        assertInstanceOf(ROSCancelOperation.class, cancel);

        getStatus.sendRequest();
        Response status = getStatus.getResponse();

        assertTrue(status.getPayload().isPresent());

        Map<String, Object> payload = (Map<String, Object>) status.getPayload().get();

        assertEquals(1l, (long) payload.get("status"));

        cancel.sendRequest();

        try {
            Thread.sleep(1000l);
        } catch (InterruptedException e) {
            throw new IOException(e);
        }

        getStatus.sendRequest();
        status = getStatus.getResponse();

        if (status.getPayload().isPresent()) {
            payload = (Map<String, Object>) status.getPayload().get();
            assertEquals(2l, (long) payload.get("status"));
        } else {
            assertTrue(status.getStatus().equals(Response.ResponseStatus.CONSUMER_ERROR));
        }
    }

}
