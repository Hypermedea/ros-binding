shape_payload(json([ kv(edges, 5), kv(radius, 1.6) ])) . // FIXME strings aren't parsed?

+!start :
    true
    <-
    makeArtifact("turtlesim", "org.hypermedea.ThingArtifact", ["turtlesim.ttl"], ArtId) ;
    focus(ArtId) ;
    !run .

+!run :
    shape_payload(P)
    <-
    invokeAction("turtle_shape", P, Result) ;
    Result = resource(Action) ;
    query(Action) .

+!query(Action)
    <-
    queryAction(Action) ;
    .wait(1000) ;
    !!query(Action) .