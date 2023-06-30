package org.hypermedea.ros;

import ch.unisg.ics.interactions.wot.td.affordances.Link;
import ch.unisg.ics.interactions.wot.td.bindings.BaseResponse;
import ch.unisg.ics.interactions.wot.td.bindings.Operation;

import java.util.*;

public class ROSResponse extends BaseResponse {

    private final ResponseStatus status;

    private final Optional<Object> payload;

    private final Collection<Link> links = new HashSet<>();

    public ROSResponse(ROSOperation op) {
        this(ResponseStatus.OK, op);
    }

    public ROSResponse(ResponseStatus status, ROSOperation op) {
        super(op);

        this.status = status;
        this.payload = Optional.empty();
    }

    public ROSResponse(Object payload, ROSOperation op) {
        super(op);

        this.status = ResponseStatus.OK;
        this.payload = Optional.of(payload);
    }

    public void addLink(String relationType, String target) {
        Link l = new Link(target, relationType);
        links.add(l);
    }

    @Override
    public Operation getOperation() {
        return null;
    }

    @Override
    public ResponseStatus getStatus() {
        return status;
    }

    @Override
    public Optional<Object> getPayload() {
        return payload;
    }

    @Override
    public Collection<Link> getLinks() {
        return links;
    }

}
