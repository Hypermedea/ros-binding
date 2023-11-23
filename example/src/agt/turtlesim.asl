shape_uri("ros+ws://localhost:9090/turtle_shape") .
shape_payload(json([ kv(edges, 5), kv(radius, 1.6) ])) . // FIXME strings aren't parsed?
shape_form([kv("https://github.com/RobotWebTools/rosbridge_suite/blob/ros1/ROSBRIDGE_PROTOCOL.md#messageType", "turtle_actionlib/ShapeActionGoal")]) .

+!start :
    shape_uri(URI) & shape_payload(P) & shape_form(F)
    <-
    post(URI, P, F) .

+rdf(Anchor, "https://github.com/RobotWebTools/rosbridge_suite/blob/ros1/ROSBRIDGE_PROTOCOL.md#goalId", Target) :
    shape_uri(Anchor)
    <-
    // TODO use watch() instead
    !query(Target) .

+!query(ActionURI)
    <-
    get(ActionURI) ;
    .wait(1000) ;
    !!query(ActionURI) .

{ include("$jacamoJar/templates/common-cartago.asl") }
{ include("$jacamoJar/templates/common-moise.asl") }
