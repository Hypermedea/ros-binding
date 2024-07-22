shape_uri("ros+ws://localhost:9090/turtle_shape") .
shape_payload(json([ kv(edges, 5), kv(radius, 1.6) ])) . // FIXME strings aren't parsed?
shape_form([kv("urn:hypermedea:ros:messageType", "turtle_actionlib/ShapeActionGoal")]) .

+!start :
    shape_uri(URI) & shape_payload(P) & shape_form(F)
    <-
    post(URI, P, F) .

+rdf(Anchor, "urn:hypermedea:ros:goalId", Target) :
    shape_uri(Anchor)
    <-
    watch(Target) ;
    +watching(Target) .

+json(Msg) :
    watching(URI)
    <-
    .member(kv(status, Status), Msg) ;
    .print(Status) ;
    if (Status = 3) {
        .print("Action done, forgetting resource: ", URI) ;
        forget(URI)
    } .

{ include("$jacamoJar/templates/common-cartago.asl") }
{ include("$jacamoJar/templates/common-moise.asl") }
