package org.hypermedea.ros;

import jason.asSyntax.Literal;
import org.hypermedea.ct.RepresentationHandlers;
import org.hypermedea.ct.json.JsonHandler;
import org.hypermedea.op.BaseOperation;
import org.hypermedea.op.Operation;
import org.hypermedea.op.ProtocolBindings;
import org.hypermedea.op.Response;
import org.hypermedea.tools.Identifiers;
import org.junit.jupiter.api.Test;

import javax.json.Json;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ROSBindingTest {

    @Test
    public void testPublish() throws IOException {
        String uri = "ros+ws://localhost:9090/turtle1/cmd_vel";

        Map<String, Object> form = new HashMap<>();
        form.put(ROS.messageType, "geometry_msgs/Twist");
        form.put(Operation.METHOD_NAME_FIELD, Operation.PUT);

        Operation op = ProtocolBindings.bind(uri, form);

        assertInstanceOf(ROSPublishOperation.class, op);

       String twist = "{\n" +
               "  \"linear\": { \"x\":-2.0, \"y\": 0.0, \"z\": 0.0 },\n" +
               "  \"angular\": { \"x\":0.0, \"y\": 0.0, \"z\": 0.0 }\n" +
               "}";

        setJsonPayload(op, twist);

        op.sendRequest();
        Response r = op.getResponse();

        assertEquals(Response.ResponseStatus.OK, r.getStatus());
    }

    @Test
    public void testSubscribe() throws IOException {
        String uri = "ros+ws://localhost:9090/turtle1/pose";

        Map<String, Object> form = new HashMap<>();
        form.put(ROS.messageType, "turtlesim/Pose");
        form.put(Operation.METHOD_NAME_FIELD, Operation.WATCH);

        Operation op = ProtocolBindings.bind(uri, form);

        ((BaseOperation) op).setTimeout(2);

        assertInstanceOf(ROSSubscribeOperation.class, op);

        // wait synchronously for the first response
        op.sendRequest();
        Response response = op.getResponse();

        assertEquals(Response.ResponseStatus.OK, response.getStatus());

        assertFalse(response.getPayload().isEmpty());

        JsonObject pose = getJsonPayload(response);

        assertTrue(pose.containsKey("x"));
        assertInstanceOf(JsonNumber.class, pose.get("x"));
    }

    @Test
    public void testAction() throws IOException {
        String uri = "ros+ws://localhost:9090/turtle_shape";

        Map<String, Object> form = new HashMap<>();
        form.put(Operation.METHOD_NAME_FIELD, Operation.POST);

        Operation sendGoal = ProtocolBindings.bind(uri, form);

        assertInstanceOf(ROSSendGoalOperation.class, sendGoal);

        String goal = "{\n" +
                "  \"edges\": 4,\n" +
                "  \"radius\": 1.57\n" +
                "}";

        setJsonPayload(sendGoal, goal);

        sendGoal.sendRequest();
        Response response = sendGoal.getResponse();

        assertTrue(sendGoal.getPayload().size() == 1);

        Optional<Literal> linkToAction = response.getPayload().stream().findAny();

        assertTrue(linkToAction.isPresent());

        String rel = Identifiers.getLexicalForm(linkToAction.get().getTerm(1));
        String actionURI = Identifiers.getLexicalForm(linkToAction.get().getTerm(2));

        assertTrue(rel.equals(ROS.goalId));

        HashMap<String, Object> form1 = new HashMap<>();
        HashMap<String, Object> form2 = new HashMap<>();

        form1.put(Operation.METHOD_NAME_FIELD, Operation.GET);
        form2.put(Operation.METHOD_NAME_FIELD, Operation.DELETE);

        Operation getStatus = ProtocolBindings.bind(actionURI, form1);
        Operation cancel = ProtocolBindings.bind(actionURI, form2);

        assertInstanceOf(ROSGetStatusOperation.class, getStatus);
        assertInstanceOf(ROSCancelOperation.class, cancel);

        getStatus.sendRequest();
        Response status = getStatus.getResponse();

        assertFalse(status.getPayload().isEmpty());

        assertEquals(Json.createValue(1l), getJsonPayload(status).get("status"));

        cancel.sendRequest();

        try {
            Thread.sleep(1000l);
        } catch (InterruptedException e) {
            throw new IOException(e);
        }

        // operation must be rebound
        getStatus = ProtocolBindings.bind(actionURI, form1);

        getStatus.sendRequest();
        status = getStatus.getResponse();

        if (!status.getPayload().isEmpty()) {
            assertEquals(Json.createValue(2l), getJsonPayload(status).get("status"));
        } else {
            assertTrue(status.getStatus().equals(Response.ResponseStatus.CLIENT_ERROR));
        }
    }

    private void setJsonPayload(Operation op, String json) throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(json.getBytes());
        Collection<Literal> p = RepresentationHandlers.deserialize(in, op.getTargetURI(), JsonHandler.APPLICATION_JSON_CT);

        op.setPayload(p);
    }

    private JsonObject getJsonPayload(Response r) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        RepresentationHandlers.serialize(r.getPayload(), out, r.getOperation().getTargetURI());

        JsonReader reader = Json.createReader(new StringReader(out.toString()));
        return reader.readObject();
    }

}
