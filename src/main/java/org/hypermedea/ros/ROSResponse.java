package org.hypermedea.ros;

import ch.unisg.ics.interactions.wot.td.affordances.Link;
import ch.unisg.ics.interactions.wot.td.bindings.Response;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

public class ROSResponse implements Response {

    private final ResponseStatus status;

    private final Optional<Object> payload;

    public ROSResponse() {
        this(ResponseStatus.OK);
    }

    public ROSResponse(ResponseStatus status) {
        this.status = status;
        this.payload = null;
    }

    public ROSResponse(Object payload) {
        this.status = ResponseStatus.OK;
        this.payload = Optional.of(payload);
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
        return new HashSet<>();
    }

}
