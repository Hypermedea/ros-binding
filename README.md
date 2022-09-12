# ROS Binding for W3C WoT

Implementation of a ROS binding for Thing Descriptions, as specified in the W3C [Web of Things (WoT) Binding Templates](https://w3c.github.io/wot-binding-templates/) standard. The targeted version is ROS 1 (ROS 2 not supported).

The current implementation adheres to the interface defined in [`wot-td-java`](https://github.com/Interactions-HSG/wot-td-java) and uses [`jrosbridge`](https://github.com/rctoris/jrosbridge) for ROS communication over WebSockets (rosbridge).