package org.hypermedea.ros;

import ch.unisg.ics.interactions.wot.td.affordances.Link;
import ch.unisg.ics.interactions.wot.td.bindings.Response;

import java.util.Collection;
import java.util.HashSet;

public class ROSResponse implements Response {

    private final ResponseStatus status;

    private final Object payload;

    public ROSResponse() {
        this.status = ResponseStatus.OK;
        this.payload = null;
    }

    public ROSResponse(ResponseStatus status) {
        this.status = status;
        this.payload = null;
    }

    public ROSResponse(Object payload) {
        this.status = ResponseStatus.OK;
        this.payload = payload;
    }

    @Override
    public ResponseStatus getStatus() {
        return status;
    }

    @Override
    public Object getPayload() {
        return payload;
    }

    @Override
    public Collection<Link> getLinks() {
        return new HashSet<>();
    }

}
