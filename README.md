# consul-microservices-simulator
A microservices simulator for consul.io registering/discovering

https://www.consul.io/

Usage:


// start consul
nohup $CONSUL_HOME/consul agent -dev &

// build simulator
mvn clean package assembly:assembly
cd target/
java -jar consul-microservices-simulator-1.0-jar-with-dependencies.jar

// use menu to interact with the simulator
