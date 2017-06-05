# consul-microservices-simulator
A microservices simulator for consul.io registering/discovering

https://www.consul.io/

Usage:

[comment]: # (This actually is the most platform independent comment)

*//start consul*
<p>
nohup $CONSUL_HOME/consul agent -dev &

*// build simulator*
<p>
mvn clean package assembly:assembly
<p>
cd target/
<p>
java -jar consul-microservices-simulator-1.0-jar-with-dependencies.jar

*// use menu to interact with the simulator*
