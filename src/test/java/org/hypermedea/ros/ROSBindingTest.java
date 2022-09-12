package org.hypermedea.ros;

import ch.unisg.ics.interactions.wot.td.affordances.Form;
import ch.unisg.ics.interactions.wot.td.bindings.Operation;
import ch.unisg.ics.interactions.wot.td.bindings.ProtocolBinding;
import ch.unisg.ics.interactions.wot.td.bindings.ProtocolBindings;
import ch.unisg.ics.interactions.wot.td.bindings.Response;
import ch.unisg.ics.interactions.wot.td.vocabularies.TD;
import edu.wpi.rail.jrosbridge.Ros;
import edu.wpi.rail.jrosbridge.Topic;
import edu.wpi.rail.jrosbridge.messages.Message;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class ROSBindingTest {

    public static void main(String[] args) throws IOException {
//        ThingDescription td = TDGraphReader.readFromFile(ThingDescription.TDFormat.RDF_TURTLE, "turtle.ttl");
//
//        PropertyAffordance p = td.getPropertyByName("cmd_vel").get();
//        Form f = p.getFirstFormForOperationType(TD.writeProperty).get();
//        Operation op = ProtocolBindings.bind(f, TD.writeProperty);
//
//        Map<String, Object> linearVel = new HashMap<>();
//        linearVel.put("x", 2.0);
//        linearVel.put("y", 0.0);
//        linearVel.put("z", 0.0);
//
//        Map<String, Object> angularVel = new HashMap<>();
//        linearVel.put("x", 0.0);
//        linearVel.put("y", 0.0);
//        linearVel.put("z", 0.0);
//
//        Map<String, Object> payload = new HashMap<>();
//
//        payload.put("linear", linearVel);
//        payload.put("angular", angularVel);
//
//        op.setPayload(null, payload);
//        op.execute();

        //String host = "193.49.165.77";
        String host = "localhost";

        Ros ros = new Ros(host);
        ros.connect();

        System.out.println("ROS master connected: " + ros.isConnected());

        Topic pose = new Topic(ros, "/turtle1/pose", "turtlesim/Pose");
        Topic vel = new Topic(ros, "/turtle1/cmd_vel", "geometry_msgs/Twist");

        pose.subscribe((Message message) -> {
            System.out.println(message);
        });

        Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Message m = new Message("{\"linear\": { \"x\": 0.0, \"y\": -2.0, \"z\": 0.0 }, \"angular\": { \"x\": 0.0, \"y\": 0.0, \"z\": 0.0 }}");
                vel.publish(m);
            }
        }, 0l, 5000l);
    }

    @BeforeAll
    public static void init() {
        ProtocolBindings.registerBinding(ROSBinding.URI_SCHEME, ROSBinding.class.getName());
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
        linearVel.put("x", 0.0);
        linearVel.put("y", 0.0);
        linearVel.put("z", 0.0);

        Map<String, Object> payload = new HashMap<>();

        payload.put("linear", linearVel);
        payload.put("angular", angularVel);

        op.setPayload(null, payload);

        Response r = op.execute();

        assertEquals(Response.ResponseStatus.OK, r.getStatus());
    }

    @Test
    public void testSubscribe() {
        Form f = new Form.Builder("ros+ws://localhost:9090/turtle1/pose")
                .addProperty(ROS.messageType, "turtlesim/Pose")
                .build();

        ProtocolBinding b = ProtocolBindings.getBinding(f);
        Operation op = b.bind(f, TD.observeProperty);

        assertInstanceOf(ROSSubscribeOperation.class, op);
    }

}
