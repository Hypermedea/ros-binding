package org.hypermedea.ros;

import jason.asSyntax.Literal;
import org.hypermedea.ct.RepresentationHandlers;
import org.hypermedea.ct.json.JsonHandler;
import org.hypermedea.op.BaseResponse;

import javax.json.JsonObject;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

public class ROSResponse extends BaseResponse {

    private ResponseStatus status;

    private Collection<Literal> payload = new HashSet<>();

    public ROSResponse(ROSOperation op) {
        this(ResponseStatus.OK, op);
    }

    public ROSResponse(ResponseStatus status, ROSOperation op) {
        super(op);

        this.status = status;
    }

    public ROSResponse(JsonObject payload, ROSOperation op) {
        super(op);

        try {
            ByteArrayInputStream in = new ByteArrayInputStream(payload.toString().getBytes());
            this.payload = RepresentationHandlers.deserialize(in, op.getTargetURI(), JsonHandler.APPLICATION_JSON_CT);

            this.status = ResponseStatus.OK;
        } catch (IOException e) {
            // TODO log error
            this.status = ResponseStatus.SERVER_ERROR;
        }
    }

    public void addLink(String relationType, String target) {
        String t = String.format("<> <%s> <%s> .", relationType, target);

        ByteArrayInputStream in = new ByteArrayInputStream(t.getBytes());
        try {
            Collection<Literal> l = RepresentationHandlers.deserialize(in, operation.getTargetURI(), "text/turtle");
            payload.addAll(l);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseStatus getStatus() {
        return status;
    }

    @Override
    public Collection<Literal> getPayload() {
        return payload;
    }

}
