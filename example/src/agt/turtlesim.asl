shape_uri("ros+ws://localhost:9090/turtle_shape") .
shape_payload(json([ kv(edges, 5), kv(radius, 1.6) ])) . // FIXME strings aren't parsed?

+!start :
    shape_uri(URI) & shape_payload(P)
    <-
    post(URI, P) .

+rdf(Anchor, "https://github.com/RobotWebTools/rosbridge_suite/blob/ros1/ROSBRIDGE_PROTOCOL.md#goalId", Target) :
    shape_uri(Anchor)
    <-
    !query(Target) .

+!query(ActionURI)
    <-
    get(ActionURI) ;
    .wait(1000) ;
    !!query(ActionURI) .

{ include("$jacamoJar/templates/common-cartago.asl") }
{ include("$jacamoJar/templates/common-moise.asl") }
